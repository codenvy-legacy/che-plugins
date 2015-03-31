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
package org.eclipse.che.plugin.grunt.builder;

import org.eclipse.che.api.builder.BuilderException;
import org.eclipse.che.api.builder.internal.BuildResult;
import org.eclipse.che.api.builder.internal.Builder;
import org.eclipse.che.api.builder.internal.BuilderConfiguration;
import org.eclipse.che.api.builder.internal.Constants;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.util.CommandLine;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder that will run grunt build.
 *
 * @author Florent Benoit
 */
@Singleton
public class GruntBuilder extends Builder {

    /**
     * Default constructor.
     *
     * @param rootDirectory
     *         the directory where we can store data
     * @param numberOfWorkers
     *         the number of workers
     * @param queueSize
     *         the size of the queue
     * @param cleanBuildResultDelay
     *         delay
     */
    @Inject
    public GruntBuilder(@Named(Constants.REPOSITORY) java.io.File rootDirectory,
                        @Named(Constants.NUMBER_OF_WORKERS) int numberOfWorkers,
                        @Named(Constants.INTERNAL_QUEUE_SIZE) int queueSize,
                        @Named(Constants.CLEANUP_RESULT_TIME) int cleanBuildResultDelay,
                        EventService eventService) {
        super(rootDirectory, numberOfWorkers, queueSize, cleanBuildResultDelay, eventService);
    }

    /**
     * @return the name of this builder
     */
    @Override
    public String getName() {
        return "grunt";
    }

    /**
     * @return the description of this builder
     */
    @Override
    public String getDescription() {
        return "Grunt JS, the JavaScript Task Runner";
    }

    /**
     * Creates the grunt build command line
     *
     * @param config
     *         the configuration that may help to build the command line
     * @return the command line
     * @throws BuilderException
     *         if command line can't be build
     */
    @Override
    protected CommandLine createCommandLine(BuilderConfiguration config) throws BuilderException {
        final CommandLine commandLine = new CommandLine("grunt");
        switch (config.getTaskType()) {
            case DEFAULT:
                commandLine.add("build");
                break;
            default:
        }
        commandLine.add(config.getOptions());
        return commandLine;
    }

    /**
     * Build a dummy artifact containing the path to the builder as the runner may require this artifact.
     * Also the result will contain the log of this command line
     *
     * @param task
     *         task
     * @param successful
     *         reports whether build process terminated normally or not.
     *         Note: {@code true} is not indicated successful build but only normal process termination. Build itself may be unsuccessful
     *         because to compilation error, failed tests, etc.
     * @return
     * @throws BuilderException
     */
    @Override
    protected BuildResult getTaskResult(FutureBuildTask task, boolean successful) throws BuilderException {
        if (!successful) {
            return new BuildResult(false, (File)null);
        }

        //FIXME : log in another place ?

        File logFile = new File(task.getConfiguration().getWorkDir(), "log-builder.txt");
        File sourceFile = new File(task.getConfiguration().getWorkDir(), ".datapath");

        // Check if the build has been aborted
        boolean buildSuccess = true;
        BufferedReader logReader = null;
        try (Writer fw = new OutputStreamWriter(new FileOutputStream(logFile), "UTF-8")) {
            logReader = new BufferedReader(task.getBuildLogger().getReader());
            String line;
            while ((line = logReader.readLine()) != null) {
                if (line.contains("Aborted due to warnings")) {
                    buildSuccess = false;
                }
                // Dump the log result
                fw.write(line);
            }
        } catch (IOException e) {
            throw new BuilderException(e);
        } finally {
            if (logReader != null) {
                try {
                    logReader.close();
                } catch (IOException ignored) {
                }
            }
        }


        // FIXME : we may want to zip the current source folder ?
        // for now add the path to the directory
        try (Writer fw = new OutputStreamWriter(new FileOutputStream(sourceFile), "UTF-8")) {
            fw.write(task.getConfiguration().getWorkDir().getAbsolutePath());
        } catch (IOException e) {
            throw new BuilderException(e);
        }
        List<File> artifacts = new ArrayList<>();
        artifacts.add(sourceFile);

        return new BuildResult(buildSuccess, artifacts, logFile);
    }

}
