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
package org.eclipse.che.plugin.yeoman.builder;

import org.eclipse.che.api.builder.BuilderException;
import org.eclipse.che.api.builder.internal.BuildListener;
import org.eclipse.che.api.builder.internal.BuildResult;
import org.eclipse.che.api.builder.internal.BuildTask;
import org.eclipse.che.api.builder.internal.Builder;
import org.eclipse.che.api.builder.internal.BuilderConfiguration;
import org.eclipse.che.api.builder.internal.Constants;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.util.CommandLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder that will use Yeoman.
 *
 * @author Florent Benoit
 */
@Singleton
public class YeomanBuilder extends Builder implements BuildListener {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(YeomanBuilder.class);

    /**
     * Map between the script to execute and the command line object.
     */
    private Map<CommandLine, File> commandLineToFile;

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
    public YeomanBuilder(@Named(Constants.REPOSITORY) java.io.File rootDirectory,
                         @Named(Constants.NUMBER_OF_WORKERS) int numberOfWorkers,
                         @Named(Constants.INTERNAL_QUEUE_SIZE) int queueSize,
                         @Named(Constants.CLEANUP_RESULT_TIME) int cleanBuildResultDelay,
                         EventService eventService) {
        super(rootDirectory, numberOfWorkers, queueSize, cleanBuildResultDelay, eventService);
        this.commandLineToFile = new HashMap<>();
        getBuildListeners().add(this);
    }


    /**
     * @return the name of this builder
     */
    @Override
    public String getName() {
        return "yeoman";
    }

    /**
     * @return the description of this builder
     */
    @Override
    public String getDescription() {
        return "Yeoman tool";
    }


    /**
     * Creates the yeoman build command line
     *
     * @param config
     *         the configuration that may help to build the command line
     * @return the command line
     * @throws BuilderException
     *         if command line can't be build
     */
    @Override
    protected CommandLine createCommandLine(BuilderConfiguration config) throws BuilderException {

        File workDir = config.getWorkDir();
        File scriptFile = new java.io.File(workDir.getParentFile(), workDir.getName() + ".yo-script");

        // now, write the script
        try (Writer fw = new OutputStreamWriter(new FileOutputStream(scriptFile), "UTF-8")) {
            // for each couple of targets
            List<String> targets = config.getTargets();
            int i = 0;

            // disable anonymous Insight tracking
            while(i < targets.size()) {
                fw.write("yo --no-insight ".concat(targets.get(i++)).concat(" ").concat(targets.get(i++)).concat("\n"));
            }
        } catch (IOException e) {
            throw new BuilderException(e);
        }

        if (!scriptFile.setExecutable(true)) {
            throw new BuilderException("Unable to set executable flag on '" + scriptFile + "'");
        }


        final CommandLine commandLine = new CommandLine(scriptFile.getAbsolutePath());

        // register the command line
        commandLineToFile.put(commandLine, scriptFile);

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
    protected BuildResult getTaskResult(final FutureBuildTask task, boolean successful) throws BuilderException {
        if (!successful) {
            return new BuildResult(false, (File)null);
        }



        // zip bower folder
        List<File> artifacts = new ArrayList<>();
        File zipFile = zipYeomanFiles(task.getConfiguration());
        artifacts.add(zipFile);

        return new BuildResult(true, artifacts);
    }

    /**
     * Build the zip file of yeoman generated stuff
     *
     * @param builderConfiguration
     *         the configuration
     * @return the expected zip file
     * @throws BuilderException
     */
    protected File zipYeomanFiles(BuilderConfiguration builderConfiguration) throws BuilderException {
        // get working directory
        File workingDirectory = builderConfiguration.getWorkDir();

        // collect all files generated after the current timestamp of the working directory
        BasicFileAttributes basicFileAttributes;
        try {
             basicFileAttributes = Files.readAttributes(workingDirectory.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            throw new BuilderException("Unable to create archive of the current workspace", e);
        }
        long timeBeforeGeneration = basicFileAttributes.creationTime().toMillis();

        // build zip containing all the source code in parent folder
        File zipFile = new File(workingDirectory.getParentFile(), "yeoman-content.zip");
        try {
            ZipHelper.zip(zipFile.toPath(), workingDirectory.toPath(), timeBeforeGeneration);
        } catch (IOException e) {
            throw new BuilderException("Unable to create archive of the current workspace", e);
        }
        File returnFile = new File(workingDirectory, zipFile.getName());
        // move it to the workdir path (in order that link is not with ../)
        try {
            Files.move(zipFile.toPath(), returnFile.toPath());
        } catch (IOException e) {
            throw new BuilderException("Unable to move archive of the current workspace", e);
        }

        return returnFile;
    }

    @Override
    public void begin(BuildTask task) {

    }

    /**
     * Cleanup the script file once the task has been completed
     * @param task
     */
    @Override
    public void end(BuildTask task) {
        File scriptFile = commandLineToFile.remove(task.getCommandLine());
        if (scriptFile != null) {
            if (!scriptFile.delete()) {
                LOG.warn("Unable to delete ''{0}''", scriptFile);
            }
        }

    }


    public void stop() {
        super.stop();
        // also cleanup scripts
        for (File file : commandLineToFile.values()) {
            if (!file.delete()) {
                LOG.warn("Unable to delete ''{0}''", file);
            }
        }
    }
}