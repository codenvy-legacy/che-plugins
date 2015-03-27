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
package org.eclipse.che.plugin.npm.builder;

import org.eclipse.che.api.builder.BuilderException;
import org.eclipse.che.api.builder.internal.BuildResult;
import org.eclipse.che.api.builder.internal.Builder;
import org.eclipse.che.api.builder.internal.BuilderConfiguration;
import org.eclipse.che.api.builder.internal.Constants;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.util.CommandLine;
import org.eclipse.che.commons.lang.ZipUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder that will use NPM for download dependencies.
 *
 * @author Florent Benoit
 */
@Singleton
public class NpmBuilder extends Builder {

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
    public NpmBuilder(@Named(Constants.REPOSITORY) File rootDirectory,
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
        return "npm";
    }

    /**
     * @return the description of this builder
     */
    @Override
    public String getDescription() {
        return "NPM package managaer";
    }


    /**
     * Launch NPM to download dependencies
     *
     * @param config
     *         the configuration that may help to build the command line
     * @return the command line
     * @throws BuilderException
     *         if command line can't be build
     */
    @Override
    protected CommandLine createCommandLine(BuilderConfiguration config) throws BuilderException {
        final CommandLine commandLine = new CommandLine("npm");
        // add the given options (like install)
        commandLine.add(config.getTargets());
        return commandLine;
    }

    /**
     * Once NPM has been called, add back files in the projects
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
        File zipFile = zipNpmFiles(task.getConfiguration());
        artifacts.add(zipFile);

        return new BuildResult(true, artifacts);
    }


    /**
     * Build the zip file of npm modules.
     *
     * @param builderConfiguration
     *         the configuration
     * @return the expected zip file
     * @throws BuilderException
     */
    protected File zipNpmFiles(BuilderConfiguration builderConfiguration) throws BuilderException {
        // get working directory
        File workingDirectory = builderConfiguration.getWorkDir();

        // build zip of node modules containing all the downloaded .js
        File zipFile = new File(workingDirectory, "content.zip");
        File nodeModulesDirectory = new File(workingDirectory, "node_modules");
        try {
            ZipUtils.zipDir(workingDirectory.getPath(), nodeModulesDirectory, zipFile, null);
        } catch (IOException e) {
            throw new BuilderException("Unable to create archive of the NPM dependencies", e);
        }


        return zipFile;
    }

}