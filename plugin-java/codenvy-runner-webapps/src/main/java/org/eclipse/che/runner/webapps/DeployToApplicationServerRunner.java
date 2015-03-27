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
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.api.core.util.CustomPortService;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironment;
import org.eclipse.che.api.runner.RunnerException;
import org.eclipse.che.api.runner.dto.RunRequest;
import org.eclipse.che.api.runner.internal.ApplicationProcess;
import org.eclipse.che.api.runner.internal.Constants;
import org.eclipse.che.api.runner.internal.DeploymentSources;
import org.eclipse.che.api.runner.internal.DeploymentSourcesValidator;
import org.eclipse.che.api.runner.internal.Disposer;
import org.eclipse.che.api.runner.internal.ResourceAllocators;
import org.eclipse.che.api.runner.internal.Runner;
import org.eclipse.che.api.runner.internal.RunnerConfiguration;
import org.eclipse.che.api.runner.internal.RunnerConfigurationFactory;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.dto.server.DtoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Runner implementation to run Java web applications by deploying it to application server.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DeployToApplicationServerRunner extends Runner {
    private static final Logger LOG = LoggerFactory.getLogger(DeployToApplicationServerRunner.class);

    public static final String DEFAULT_SERVER_NAME = "tomcat7";
    public static final String HOST_NAME           = "runner.java_webapp.host_name";

    private final Map<String, ApplicationServer> servers;
    private final String                         hostName;
    private final CustomPortService              portService;
    private final DeploymentSourcesValidator     applicationValidator;

    @Inject
    public DeployToApplicationServerRunner(@Named(Constants.DEPLOY_DIRECTORY) java.io.File deployDirectoryRoot,
                                           @Named(Constants.APP_CLEANUP_TIME) int cleanupTime,
                                           @Named(HOST_NAME) String hostName,
                                           ResourceAllocators allocators,
                                           CustomPortService portService,
                                           Set<ApplicationServer> appServers,
                                           EventService eventService) {
        super(deployDirectoryRoot, cleanupTime, allocators, eventService);
        this.hostName = hostName;
        this.portService = portService;
        this.servers = new HashMap<>();
        for (ApplicationServer server : appServers) {
            this.servers.put(server.getName(), server);
        }
        this.applicationValidator = new JavaWebApplicationValidator();
    }

    @Override
    public String getName() {
        return "java/web";
    }

    @Override
    public String getDescription() {
        return "Java Web application runner";
    }

    @Override
    public List<RunnerEnvironment> getEnvironments() {
        final DtoFactory dtoFactory = DtoFactory.getInstance();
        final List<RunnerEnvironment> environments = new LinkedList<>();
        for (ApplicationServer server : servers.values()) {
            final RunnerEnvironment runnerEnvironment = dtoFactory.createDto(RunnerEnvironment.class)
                                                                  .withId(server.getName())
                                                                  .withDescription(server.getDescription());
            environments.add(runnerEnvironment);
        }
        return environments;
    }

    @Override
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                final String server = request.getEnvironmentId();
                final int httpPort = portService.acquire();
                final ApplicationServerRunnerConfiguration configuration =
                        new ApplicationServerRunnerConfiguration(server, request.getMemorySize(), httpPort, request);
                configuration.getLinks().add(DtoFactory.getInstance().createDto(Link.class)
                                                       .withRel(Constants.LINK_REL_WEB_URL)
                                                       .withHref(String.format("http://%s:%d", hostName, httpPort)));
                if (request.isInDebugMode()) {
                    configuration.setDebugHost(hostName);
                    configuration.setDebugPort(portService.acquire());
                }
                return configuration;
            }
        };
    }

    @Override
    protected DeploymentSourcesValidator getDeploymentSourcesValidator() {
        return applicationValidator;
    }

    @Override
    protected ApplicationProcess newApplicationProcess(final DeploymentSources toDeploy,
                                                       final RunnerConfiguration configuration) throws RunnerException {
        // It always should be ApplicationServerRunnerConfiguration.
        final ApplicationServerRunnerConfiguration webAppsRunnerCfg = (ApplicationServerRunnerConfiguration)configuration;
        final ApplicationServer server = servers.get(webAppsRunnerCfg.getServer());
        if (server == null) {
            throw new RunnerException(String.format("Server %s not found", webAppsRunnerCfg.getServer()));
        }

        final java.io.File appDir;
        try {
            appDir = Files.createTempDirectory(getDeployDirectory().toPath(),
                                               (server.getName() + '_' + getName().replace("/", "."))).toFile();
        } catch (IOException e) {
            throw new RunnerException(e);
        }

        final ApplicationProcess process =
                server.deploy(appDir, toDeploy, webAppsRunnerCfg, new ApplicationProcess.Callback() {
                    @Override
                    public void started() {
                    }

                    @Override
                    public void stopped() {
                        portService.release(webAppsRunnerCfg.getHttpPort());
                        final int debugPort = webAppsRunnerCfg.getDebugPort();
                        if (debugPort > 0) {
                            portService.release(debugPort);
                        }
                    }
                });

        registerDisposer(process, new Disposer() {
            @Override
            public void dispose() {
                if (!IoUtil.deleteRecursive(appDir)) {
                    LOG.error("Unable to remove app: {}", appDir);
                }
            }
        });

        return process;
    }
}