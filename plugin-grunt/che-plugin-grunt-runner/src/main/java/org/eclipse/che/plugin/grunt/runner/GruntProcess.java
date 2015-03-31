/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.grunt.runner;

import org.eclipse.che.api.project.server.ProjectEvent;
import org.eclipse.che.api.project.server.ProjectEventListener;
import org.eclipse.che.api.runner.RunnerException;
import org.eclipse.che.api.runner.internal.ApplicationLogger;
import org.eclipse.che.api.runner.internal.ApplicationProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Application process used for Grunt
 *
 * @author Florent Benoit
 */
public class GruntProcess extends ApplicationProcess implements ProjectEventListener {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(GruntProcess.class);

    /**
     * Result of the Runtime.exec command
     */
    private Process process;

    /**
     * Excutor service.
     */
    private ExecutorService executorService;

    /**
     * Directory where launch grunt.
     */
    private final File workDir;

    /**
     * Base URL for Rest API.
     */
    private String baseURL;

    /**
     * Grunt runner configuration.
     */
    private GruntRunnerConfiguration gruntRunnerConfiguration;

    /**
     * Grunt runner used to build this process.
     */
    private GruntRunner gruntRunner;

    /**
     * Logger.
     */
    private ApplicationLogger logger;

    /**
     * Build a new process for the following directory
     *
     * @param workDir
     *         the directory to start grunt
     */
    public GruntProcess(ExecutorService executorService, File workDir, String baseURL, GruntRunnerConfiguration gruntRunnerConfiguration,
                        GruntRunner gruntRunner) {
        super();
        this.executorService = executorService;
        this.workDir = workDir;
        this.baseURL = baseURL;
        this.gruntRunnerConfiguration = gruntRunnerConfiguration;
        this.gruntRunner = gruntRunner;
    }

    /**
     * Run the process is not yet done
     *
     * @throws RunnerException
     *         if command can't be launched
     */
    @Override
    public void start() throws RunnerException {
        if (process != null) {
            throw new IllegalStateException("Process is already started");
        }

        // needs to replace the http/livreload port
        Path path = new File(workDir, "Gruntfile.js").toPath();
        Charset charset = StandardCharsets.UTF_8;

        String content;
        try {
            content = new String(Files.readAllBytes(path), charset);
        } catch (IOException e) {
            throw new RunnerException("Unable to read Grunt configuration file", e);
        }
        content = content.replaceAll("9000", String.valueOf(gruntRunnerConfiguration.getHttpPort()));
        content = content.replaceAll("35729", String.valueOf(gruntRunnerConfiguration.getLiveReloadPort()));
        try {
            Files.write(path, content.getBytes(charset));
        } catch (IOException e) {
            throw new RunnerException("Unable to replace http port in the runner", e);
        }


        String taskName = null;
        Map<String, String> options = gruntRunnerConfiguration.getRequest().getOptions();
        if (options != null) {
            taskName = options.get("taskname");
        }
        // no task defined, use default one
        if (taskName == null) {
            taskName = "server";
        }


        // Create the process builder
        ProcessBuilder processBuilder = new ProcessBuilder().command("grunt", taskName).directory(workDir).redirectErrorStream(true);

        try {
            this.process = processBuilder.start();
        } catch (IOException e) {
            throw new RunnerException("Unable to launch grunt command", e);
        }


        // create log file
        final File logFile = new File(workDir, "grunt-output.log");

        // start to listen output
        new Thread() {
            public void run() {
                try (Writer fileWriter = new OutputStreamWriter(new FileOutputStream(logFile), "UTF-8"); BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))) {
                    String str;
                    while ((str = br.readLine()) != null) {
                        fileWriter.write(str);
                        fileWriter.write(System.getProperty("line.separator"));
                    }
                } catch (IOException e) {
                    LOG.error("Unable to write content", e);
                }

            }}.start();


        // Capture output
        this.logger = new GruntApplicationLogger(logFile);



    }

    /**
     * Stop the process
     *
     * @throws RunnerException
     */
    @Override
    public void stop() throws RunnerException {
        if (process == null) {
            throw new IllegalStateException("Process is not started yet");
        }
        process.destroy();

        // callback on stop
        gruntRunner.onStop(this, gruntRunnerConfiguration);
    }


    @Override
    public int waitFor() throws RunnerException {
        synchronized (this) {
            if (process == null) {
                throw new IllegalStateException("Process is not started yet");
            }
        }
        try {
            process.waitFor();
        } catch (InterruptedException ignored) {
        }
        return process.exitValue();
    }

    @Override
    public int exitCode() throws RunnerException {
        if (process == null) {
            return -1;
        }
        return process.exitValue();
    }

    @Override
    public boolean isRunning() throws RunnerException {
        // no process so it's not running
        if (process == null) {
            return false;
        }

        // if we have exit value it means that process has been exited
        try {
            process.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            // exception so it is still running
            return true;
        }
    }

    @Override
    public ApplicationLogger getLogger() throws RunnerException {
        if (logger == null) {
            // is not started yet
            return ApplicationLogger.DUMMY;
        }
        return logger;
    }

    /**
     * There is a change on the project that we're monitoring, whatever the type of event is, we need to updated the runner.
     * @param event
     */
    @Override
    public void onEvent(ProjectEvent event) {
        if (event.getType() == ProjectEvent.EventType.UPDATED || event.getType() == ProjectEvent.EventType.CREATED) {
            // needs update
            update(event);
        }

    }

    /**
     * Update the current code through the executor service
     * Download the new source again and unpack.
     */
    protected void update(final ProjectEvent event) {
         executorService.execute(new Runnable() {
            @Override
            public void run()  {

                // connect to the project API URL
                int index = baseURL.indexOf(event.getProject());
                HttpURLConnection conn;
                try {
                    conn = (HttpURLConnection)new URL(baseURL.substring(0, index).concat("/file").concat(event.getProject()).concat("/").concat(
                            event.getPath())).openConnection();
                conn.setConnectTimeout(30 * 1000);
                conn.setRequestMethod("GET");
                conn.addRequestProperty("content-type", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to build connection", e);
                }

                // If file has been found, dump the content
                final int responseCode;
                try {
                    responseCode = conn.getResponseCode();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to get response code", e);
                }
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    File updatedFile = new File(workDir, event.getPath());
                    byte[] buffer = new byte[8192];
                    try (InputStream input = conn.getInputStream(); OutputStream output = new FileOutputStream(updatedFile)) {
                            int bytesRead;
                            while ((bytesRead = input.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                    } catch (IOException ioe) {
                        throw new RuntimeException("Unable to send answer", ioe);
                    }
                }

            }
        });
    }

}
