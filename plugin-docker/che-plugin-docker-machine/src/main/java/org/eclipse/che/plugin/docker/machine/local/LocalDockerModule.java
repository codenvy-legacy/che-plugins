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
package org.eclipse.che.plugin.docker.machine.local;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import org.eclipse.che.api.core.util.SystemInfo;
import org.eclipse.che.api.machine.server.MachineService;
import org.eclipse.che.api.machine.server.spi.Instance;
import org.eclipse.che.api.machine.server.spi.InstanceProcess;
import org.eclipse.che.api.machine.server.spi.InstanceProvider;
import org.eclipse.che.plugin.docker.client.DockerConnectorConfiguration;
import org.eclipse.che.plugin.docker.machine.DockerInstance;
import org.eclipse.che.plugin.docker.machine.DockerInstanceProvider;
import org.eclipse.che.plugin.docker.machine.DockerMachineFactory;
import org.eclipse.che.plugin.docker.machine.node.DockerNode;
import org.eclipse.che.plugin.docker.machine.DockerProcess;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.net.URI;

/**
 * The Module for Local Docker components
 * Note that LocalDockerNodeFactory requires machine.docker.local.project parameter pointed to
 * directory containing workspace projects tree
 *
 * @author gazarenkov
 */
public class LocalDockerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MachineService.class);

        install(new FactoryModuleBuilder()
                        .implement(Instance.class, DockerInstance.class)
                        .implement(InstanceProcess.class, DockerProcess.class)
                        .implement(DockerNode.class, LocalDockerNode.class)
                        .build(DockerMachineFactory.class));

        Multibinder.newSetBinder(binder(), InstanceProvider.class).addBinding().to(DockerInstanceProvider.class);

        bind(String.class).annotatedWith(Names.named("host.projects.root"))
                          .toProvider(org.eclipse.che.plugin.docker.machine.local.LocalDockerModule.HostProjectFolderProvider.class);

        bind(org.eclipse.che.plugin.docker.machine.node.WorkspaceFolderPathProvider.class)
                .to(org.eclipse.che.plugin.docker.machine.local.node.LocalWorkspaceFolderPathProvider.class);

        bind(org.eclipse.che.plugin.docker.client.DockerRegistryChecker.class).asEagerSingleton();

        Multibinder<String> debMachineEnvVars = Multibinder.newSetBinder(binder(),
                                                                         String.class,
                                                                         Names.named("machine.docker.dev_machine.machine_env"))
                                                           .permitDuplicates();
        debMachineEnvVars.addBinding().toProvider(DockerApiHostEnvVariableProvider.class).in(Singleton.class);
        debMachineEnvVars.addBinding().toProvider(DockerApiTlsVerifyEnvVariableProvider.class).in(Singleton.class);

        Multibinder<String> allMachinesEnvVars = Multibinder.newSetBinder(binder(),
                                                                          String.class,
                                                                          Names.named("machine.docker.machine_env"))
                                                            .permitDuplicates();
    }

    /**
     * Provide path to the project folder on hosted machine
     *
     * On Unix managed by vfs.local.fs_root_dir property.
     * On Windows MUST be locate in "user.home" directory in case limitation windows+docker
     *
     * @author Alexander Garagatyi
     * @author Vitalii Parfonov
     */
    private static class HostProjectFolderProvider implements Provider<String> {
        @Inject
        @Named("vfs.local.fs_root_dir")
        private String projectsFolder;

        @Override
        public String get() {
            if (SystemInfo.isWindows()) {
                return System.getProperty("user.home") + "\\che\\projects";
            } else {
                return projectsFolder;
            }
        }
    }

    /**
     * Provides DOCKER_HOST env variable for the sake of access to docker API within docker container
     *
     * @author Alexander Garagatyi
     */
    private static class DockerApiHostEnvVariableProvider implements Provider<String> {
        @Inject
        private DockerConnectorConfiguration dockerConnectorConfiguration;

        @Override
        public String get() {
            final URI dockerDaemonUri = dockerConnectorConfiguration.getDockerDaemonUri();
            if ("http".equals(dockerDaemonUri.getScheme())) {
                return DockerConnectorConfiguration.DOCKER_HOST_PROPERTY
                       + "=tcp://"
                       + dockerConnectorConfiguration.getDockerHost()
                       + ':'
                       + dockerDaemonUri.getPort();
            }
            return "";
        }
    }

    /**
     * Provides DOCKER_TLS_VERIFY env variable for the sake of access to docker API within docker container
     *
     * @author Alexander Garagatyi
     */
    private static class DockerApiTlsVerifyEnvVariableProvider implements Provider<String> {
        @Inject
        private DockerConnectorConfiguration dockerConnectorConfiguration;

        @Override
        public String get() {
            final URI dockerApiUri = dockerConnectorConfiguration.getDockerDaemonUri();
            if ("http".equals(dockerApiUri.getScheme())) {
                return DockerConnectorConfiguration.DOCKER_TLS_VERIFY_PROPERTY + "=false";
            }
            return "";
        }
    }
}
