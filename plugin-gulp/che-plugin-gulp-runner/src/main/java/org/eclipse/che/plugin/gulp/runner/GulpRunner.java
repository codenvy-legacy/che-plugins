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
package org.eclipse.che.plugin.gulp.runner;

import org.eclipse.che.api.builder.BuilderService;
import org.eclipse.che.api.builder.dto.BuildOptions;
import org.eclipse.che.api.builder.dto.BuildTaskDescriptor;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.rest.HttpJsonHelper;
import org.eclipse.che.api.core.rest.RemoteServiceDescriptor;
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.api.core.util.DownloadPlugin;
import org.eclipse.che.api.core.util.HttpDownloadPlugin;
import org.eclipse.che.api.project.server.ProjectEventService;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironment;
import org.eclipse.che.api.runner.RunnerException;
import org.eclipse.che.api.runner.dto.RunRequest;
import org.eclipse.che.api.runner.internal.ApplicationProcess;
import org.eclipse.che.api.runner.internal.Constants;
import org.eclipse.che.api.runner.internal.DeploymentSources;
import org.eclipse.che.api.runner.internal.ResourceAllocators;
import org.eclipse.che.api.runner.internal.Runner;
import org.eclipse.che.api.runner.internal.RunnerConfiguration;
import org.eclipse.che.api.runner.internal.RunnerConfigurationFactory;
import org.eclipse.che.commons.lang.Pair;
import org.eclipse.che.commons.lang.ZipUtils;
import org.eclipse.che.dto.server.DtoFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Runner implementation to run Gulp application
 *
 * @author Florent Benoit
 */
@Singleton
public class GulpRunner extends Runner {

    private final String              hostName;
    private final ProjectEventService projectEventService;
    private final DownloadPlugin      downloadPlugin;

    @Inject
    public GulpRunner(@Named(Constants.DEPLOY_DIRECTORY) java.io.File deployDirectoryRoot,
                      @Named(Constants.APP_CLEANUP_TIME) int cleanupDelay,
                      @Named("runner.javascript_gulp.host_name") String hostName,
                      ResourceAllocators allocators,
                      EventService eventService,
                      ProjectEventService projectEventService) {
        super(deployDirectoryRoot, cleanupDelay, allocators, eventService);
        this.hostName = hostName;
        this.projectEventService = projectEventService;
        this.downloadPlugin = new HttpDownloadPlugin();


    }

    @Override
    public String getName() {
        return "javascript/web";
    }

    @Override
    public String getDescription() {
        return "gulp.js The streaming build system";
    }

    @Override
    public List<RunnerEnvironment> getEnvironments() {
        final DtoFactory dtoFactory = DtoFactory.getInstance();
        return Collections.singletonList(dtoFactory.createDto(RunnerEnvironment.class)
                                                   .withId("gulp").withDescription(getDescription()));
    }

    @Override
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                final GulpRunnerConfiguration configuration =
                        new GulpRunnerConfiguration(request.getMemorySize(), 5000, request);
                configuration.getLinks().add(DtoFactory.getInstance().createDto(Link.class).withRel("web url")
                                                       .withHref(String.format("http://%s:%d", hostName, 5000)));
                return configuration;
            }
        };
    }

    @Override
    protected ApplicationProcess newApplicationProcess(final DeploymentSources toDeploy,
                                                       final RunnerConfiguration configuration) throws RunnerException {
        // Cast the configuration
        if (!(configuration instanceof GulpRunnerConfiguration)) {
            throw new RunnerException("Unable to get the configuration. Not the expected type");
        }

        final GulpRunnerConfiguration gulpRunnerConfiguration = (GulpRunnerConfiguration)configuration;

        // Needs to launch Grunt

        File path;
        File sourceFile = toDeploy.getFile();

        // Zip file, unpack it as it contains the source repository
        if (toDeploy.isArchive()) {
            try {
                path = Files.createTempDirectory(getDeployDirectory().toPath(), null).toFile();
            } catch (IOException e) {
                throw new RunnerException("Unable to create a temporary file", e);
            }
            try {
                ZipUtils.unzip(toDeploy.getFile(), path);

                File gulpFile = new File(path, "gulpfile.js");
                if (!gulpFile.exists()) {
                    throw new RunnerException("Unable to find gulpfile.js file in the project");
                }

            } catch (IOException e) {
                throw new RunnerException("Unable to unpack the zip file", e);
            }
        } else {

            try (Reader reader = new InputStreamReader(new FileInputStream(sourceFile), "UTF-8");
                 BufferedReader bufferedReader = new BufferedReader(reader)) {
                path = new File(bufferedReader.readLine());
            } catch (IOException e) {
                throw new RunnerException("Unable to read file", e);
            }
        }

        // project properties
        RunRequest runRequest = gulpRunnerConfiguration.getRequest();
        String projectName = runRequest.getProject();
        String workspace = runRequest.getWorkspace();


        String baseURL = configuration.getRequest().getProjectDescriptor().getBaseUrl();

        // Ask NPM builder for getting NPM dependencies
        File packageJsonFile = new File(path, "package.json");
        if (packageJsonFile.exists()) {
            BuildOptions buildOptions = DtoFactory.getInstance().createDto(BuildOptions.class).withBuilderName("npm").withTargets(
                    Arrays.asList("install"));

            final RemoteServiceDescriptor builderService = getBuilderServiceDescriptor(workspace, baseURL);
            // schedule build
            final BuildTaskDescriptor buildDescriptor;
            try {
                final Link buildLink = builderService.getLink(org.eclipse.che.api.builder.internal.Constants.LINK_REL_BUILD);
                if (buildLink == null) {
                    throw new RunnerException("Unable get URL for starting build of the application");
                }
                buildDescriptor =
                        HttpJsonHelper.request(BuildTaskDescriptor.class, buildLink, buildOptions, Pair.of("project", projectName));
            } catch (IOException e) {
                throw new RunnerException(e);
            } catch (ServerException | UnauthorizedException | ForbiddenException | NotFoundException | ConflictException e) {
                throw new RunnerException(e.getServiceError());
            }


            final Link buildStatusLink = getLink(org.eclipse.che.api.builder.internal.Constants.LINK_REL_GET_STATUS,
                                                 buildDescriptor.getLinks());
            if (buildStatusLink == null) {
                throw new RunnerException("Invalid response from builder service. Unable get URL for checking build status");
            }
            String downloadLinkHref;

            // Execute builder
            RemoteBuilderRunnable remoteBuilderRunnable = new RemoteBuilderRunnable(buildDescriptor);

            // Wait the builder
            Future<String> future = getExecutor().submit(remoteBuilderRunnable);
            try {
                downloadLinkHref = future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RunnerException("Unable to update source code", e);
            }
            DeploymentSources deploymentSources = downloadApplicationLink(downloadLinkHref, path);
            // unzip it
            if (deploymentSources.getFile().exists()) {
                try (InputStream is = new FileInputStream(deploymentSources.getFile())) {
                    ZipUtils.unzip(is, path);
                } catch (IOException e) {
                    throw new RunnerException("Unable to unzip NPM dependencies");
                }
            }
        }


        // Create the process
        final GulpProcess process = new GulpProcess(getExecutor(), path, baseURL);

        // Register the listener
        projectEventService.addListener(workspace, projectName, process);

        //FIXME : unregister the listener ?

        return process;
    }


    private static Link getLink(String rel, List<Link> links) {
        for (Link link : links) {
            if (rel.equals(link.getRel())) {
                return link;
            }
        }
        return null;
    }

    private RemoteServiceDescriptor getBuilderServiceDescriptor(String workspace, String runnerURL) {

        UriBuilder baseBuilderUriBuilder = UriBuilder.fromUri(runnerURL.substring(0, runnerURL.indexOf("/project")));
        final String builderUrl = baseBuilderUriBuilder.path(BuilderService.class).build(workspace).toString();
        return new RemoteServiceDescriptor(builderUrl);
    }


    protected DeploymentSources downloadApplicationLink(String url, File destFolder) throws RunnerException {
        DownloadCallback downloadCallback = new DownloadCallback();
        final File downloadDir;

        try {
            downloadDir = Files.createTempDirectory(destFolder.toPath(), "updated").toFile();
        } catch (IOException e) {
            throw new RunnerException(e);
        }
        downloadPlugin.download(url, downloadDir, downloadCallback);
        if (downloadCallback.getErrorHolder() != null) {
            throw new RunnerException(downloadCallback.getErrorHolder());
        }
        return downloadCallback.getResultHolder();
    }

}