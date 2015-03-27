/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.runner.webapps;

import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.util.CommandLine;
import org.eclipse.che.api.core.util.ProcessUtil;
import org.eclipse.che.api.core.util.StreamPump;
import org.eclipse.che.api.runner.RunnerException;
import org.eclipse.che.api.runner.internal.ApplicationLogger;
import org.eclipse.che.api.runner.internal.ApplicationLogsPublisher;
import org.eclipse.che.api.runner.internal.ApplicationProcess;
import org.eclipse.che.api.runner.internal.DeploymentSources;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code ApplicationServer} implementation to deploy application to Apache Tomcat servlet container for *nix like system.
 *
 * @author Artem Zatsarynnyy
 * @author Roman Nikitenko
 */
@Singleton
public class UnixTomcatServer extends BaseTomcatServer {
    private static final Logger LOG = LoggerFactory.getLogger(UnixTomcatServer.class);

    @Inject
    public UnixTomcatServer(@Named(MEM_SIZE_PARAMETER) int memSize,
                            @Named(TOMCAT_HOME_PARAMETER) File tomcatHome,
                            EventService eventService) {
        super(memSize, tomcatHome, eventService);
    }

    @Override
    public ApplicationProcess deploy(File appDir,
                                     DeploymentSources toDeploy,
                                     ApplicationServerRunnerConfiguration runnerConfiguration,
                                     ApplicationProcess.Callback callback) throws RunnerException {
        prepare(appDir, toDeploy, runnerConfiguration);
        final File logsDir = new File(appDir, "logs");
        final File startUpScriptFile;
        try {
            startUpScriptFile = generateStartUpScript(appDir, runnerConfiguration);
            Files.createDirectory(logsDir.toPath());
        } catch (IOException e) {
            throw new RunnerException(e);
        }
        final List<File> logFiles = new ArrayList<>(1);
        logFiles.add(new File(logsDir, "output.log"));

        return new TomcatProcess(appDir, startUpScriptFile, logFiles, runnerConfiguration, callback, eventService);
    }

    private File generateStartUpScript(File appDir, ApplicationServerRunnerConfiguration runnerConfiguration)
            throws IOException {
        final String startupScript = "#!/bin/sh\n" +
                                     exportEnvVariables(runnerConfiguration) +
                                     "cd tomcat\n" +
                                     "chmod +x bin/*.sh\n" +
                                     catalinaUnix(runnerConfiguration) +
                                     "PID=$!\n" +
                                     "echo \"$PID\" > ../run.pid\n" +
                                     "wait $PID";
        final File startUpScriptFile = new File(appDir, "startup.sh");
        Files.write(startUpScriptFile.toPath(), startupScript.getBytes());
        if (!startUpScriptFile.setExecutable(true, false)) {
            throw new IOException("Unable to update attributes of the startup script");
        }
        return startUpScriptFile;
    }

    private String exportEnvVariables(ApplicationServerRunnerConfiguration runnerConfiguration) {
        int memory = runnerConfiguration.getMemory();
        if (memory <= 0) {
            memory = getMemSize();
        }
        final String catalinaOpts = String.format("export CATALINA_OPTS=\"-Xms%dm -Xmx%dm\"%n", memory, memory);
        final int debugPort = runnerConfiguration.getDebugPort();
        if (debugPort <= 0) {
            return catalinaOpts;
        }
        /*
        From catalina.sh:
        -agentlib:jdwp=transport=$JPDA_TRANSPORT,address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND
         */
        return catalinaOpts +
               String.format("export JPDA_ADDRESS=%d%n", debugPort) +
               String.format("export JPDA_TRANSPORT=%s%n", "dt_socket") +
               String.format("export JPDA_SUSPEND=%s%n", runnerConfiguration.isDebugSuspend() ? "y" : "n");
    }

    private String catalinaUnix(ApplicationServerRunnerConfiguration runnerConfiguration) {
        final boolean debug = runnerConfiguration.getDebugPort() > 0;
        if (debug) {
            return "./bin/catalina.sh jpda run 2>&1 | tee ../logs/output.log &\n";
        }
        return "./bin/catalina.sh run 2>&1 | tee ../logs/output.log &\n";
    }

    private static class TomcatProcess extends ApplicationProcess {
        final int          httpPort;
        final List<File>   logFiles;
        final int          debugPort;
        final File         startUpScriptFile;
        final File         workDir;
        final Callback     callback;
        final EventService eventService;
        final String       workspace;
        final String       project;
        final long         id;

        ApplicationLogger logger;
        Process           process;
        StreamPump        output;

        TomcatProcess(File appDir, File startUpScriptFile, List<File> logFiles,
                      ApplicationServerRunnerConfiguration runnerConfiguration, Callback callback, EventService eventService) {
            this.httpPort = runnerConfiguration.getHttpPort();
            this.logFiles = logFiles;
            this.debugPort = runnerConfiguration.getDebugPort();
            this.startUpScriptFile = startUpScriptFile;
            this.workDir = appDir;
            this.callback = callback;
            this.eventService = eventService;
            this.workspace = runnerConfiguration.getRequest().getWorkspace();
            this.project = runnerConfiguration.getRequest().getProject();
            this.id = runnerConfiguration.getRequest().getId();
        }

        @Override
        public synchronized void start() throws RunnerException {
            if (process != null && ProcessUtil.isAlive(process)) {
                throw new IllegalStateException("Process is already started");
            }
            try {
                process = Runtime.getRuntime().exec(new CommandLine(startUpScriptFile.getAbsolutePath()).toShellCommand(), null, workDir);
                logger = new ApplicationLogsPublisher(new TomcatLogger(logFiles), eventService, id, workspace, project);
                output = new StreamPump();
                output.start(process, logger);
                LOG.debug("Start Tomcat at port {}, application {}", httpPort, workDir);
            } catch (IOException e) {
                throw new RunnerException(e);
            }
        }

        @Override
        public synchronized void stop() throws RunnerException {
            if (process == null) {
                throw new IllegalStateException("Process is not started yet");
            }
            // Use ProcessUtil.kill(process) because java.lang.Process.destroy() method doesn't
            // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
            ProcessUtil.kill(process);
            if (output != null) {
                output.stop();
            }
            callback.stopped();
            LOG.debug("Stop Tomcat at port {}, application {}", httpPort, workDir);
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
            } catch (InterruptedException e) {
                Thread.interrupted();
                ProcessUtil.kill(process);
            } finally {
                if (output != null) {
                    output.stop();
                }
            }
            return process.exitValue();
        }

        @Override
        public synchronized int exitCode() throws RunnerException {
            if (process == null || ProcessUtil.isAlive(process)) {
                return -1;
            }
            return process.exitValue();
        }

        @Override
        public synchronized boolean isRunning() throws RunnerException {
            return process != null && ProcessUtil.isAlive(process);
        }

        @Override
        public synchronized ApplicationLogger getLogger() throws RunnerException {
            if (logger == null) {
                // is not started yet
                return ApplicationLogger.DUMMY;
            }
            return logger;
        }
    }
}