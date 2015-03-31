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
package org.eclipse.che.plugin.angularjs.builder;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builder that will use Bower for download dependencies.
 *
 * @author Florent Benoit
 */
@Singleton
public class BowerBuilder extends Builder {


    private static final Pattern DIRECTORY_PATTERN = Pattern.compile("\"directory\"\\s*:\\s*\"(.*)\"");

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
    public BowerBuilder(@Named(Constants.REPOSITORY) java.io.File rootDirectory,
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
        return "bower";
    }

    /**
     * @return the description of this builder
     */
    @Override
    public String getDescription() {
        return "Bower, a package manager for the web";
    }


    /**
     * Launch NPM to download dependencies
     *
     * @param config
     *         the configuration that may help to build the command line
     * @return the command line
     * @throws org.eclipse.che.api.builder.BuilderException
     *         if command line can't be build
     */
    @Override
    protected CommandLine createCommandLine(BuilderConfiguration config) throws BuilderException {
        final CommandLine commandLine = new CommandLine("bower");
        // add the given options (like install)
        commandLine.add(config.getTargets());
        // disable interactive mode
        commandLine.add("--config.interactive=false");
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
     * @throws org.eclipse.che.api.builder.BuilderException
     */
    @Override
    protected BuildResult getTaskResult(final FutureBuildTask task, boolean successful) throws BuilderException {
        if (!successful) {
            return new BuildResult(false, (File)null);
        }

        // zip bower folder
        List<File> artifacts = new ArrayList<>();
        File zipFile = zipBowerFiles(task.getConfiguration());
        artifacts.add(zipFile);


        return new BuildResult(true, artifacts);

    }



    protected File zipBowerFiles(BuilderConfiguration builderConfiguration) throws BuilderException {
        // get working directory
        File workingDirectory = builderConfiguration.getWorkDir();

        // Default bower directory
        File appDirectory = new File(workingDirectory, "app");
        File bowerComponentsDirectory = new File(appDirectory, "bower_components");


        // Check bower.rc resource ?
        File bowerRcResource = new File(workingDirectory, ".bowerrc");
        if (bowerRcResource.exists()) {
            // read content
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = Files.newBufferedReader(bowerRcResource.toPath(), Charset.defaultCharset())) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
            } catch (IOException e) {
                throw new BuilderException("Unable to read the .bowerrc file", e);
            }

            String jsonContent = stringBuilder.toString();
            // parse
            Matcher paramMatcher = DIRECTORY_PATTERN.matcher(jsonContent);

            if (paramMatcher.find()) {
                String directoryName = paramMatcher.group(1);
                bowerComponentsDirectory = new File(workingDirectory, directoryName);
            }
        }

        // build zip of node modules containing all the downloaded .js
        File zipFile = new File(workingDirectory, "content.zip");

        if (bowerComponentsDirectory.exists()) {
            try {
                ZipUtils.zipDir(workingDirectory.getPath(), bowerComponentsDirectory, zipFile, null);
            } catch (IOException e) {
                throw new BuilderException("Unable to create archive of the NPM dependencies", e);
            }
        } else {
            throw new BuilderException("No app/bower_components directory found");
        }


        return zipFile;
    }
}