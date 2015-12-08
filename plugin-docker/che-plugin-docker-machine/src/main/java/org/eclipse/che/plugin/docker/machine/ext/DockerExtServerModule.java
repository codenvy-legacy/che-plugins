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
package org.eclipse.che.plugin.docker.machine.ext;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import org.eclipse.che.api.core.util.SystemInfo;
import org.eclipse.che.inject.CheBootstrap;
import org.eclipse.che.plugin.docker.machine.DockerInstanceMetadata;
import org.eclipse.che.plugin.docker.machine.ServerConf;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

/**
 * Guice module for extension servers feature in docker machines
 *
 * @author Alexander Garagatyi
 * @author Sergii Leschenko
 */
// Not a DynaModule, install manually
public class DockerExtServerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DockerMachineExtServerLauncher.class).asEagerSingleton();

        Multibinder<ServerConf> machineServers = Multibinder.newSetBinder(binder(),
                                                                          ServerConf.class,
                                                                          Names.named("machine.docker.dev_machine.machine_servers"));
        machineServers.addBinding().toInstance(new ServerConf("extensions", "4401", "http"));

        Multibinder<String> volumesMultibinder = Multibinder.newSetBinder(binder(),
                                                                          String.class,
                                                                          Names.named("machine.docker.dev_machine.machine_volumes"));
        volumesMultibinder.addBinding().toProvider(ExtServerVolumeProvider.class).in(Singleton.class);

        Multibinder<String> debMachineEnvVars = Multibinder.newSetBinder(binder(),
                                                                         String.class,
                                                                         Names.named("machine.docker.dev_machine.machine_env"));
        debMachineEnvVars.addBinding().toProvider(ApiEndpointEnvVariableProvider.class).in(Singleton.class);
        debMachineEnvVars.addBinding().toProvider(ProjectsRootEnvVariableProvider.class).in(Singleton.class);
        debMachineEnvVars.addBinding().toInstance(CheBootstrap.CHE_LOCAL_CONF_DIR
                                                  + '='
                                                  + DockerExtConfBindingProvider.EXT_CHE_LOCAL_CONF_DIR);

        DockerExtConfBindingProvider extConfBindingProvider = new DockerExtConfBindingProvider();
        if (extConfBindingProvider.get() != null) {
            volumesMultibinder.addBinding().toProvider(extConfBindingProvider);
        }
    }

    /**
     * Add env variable to docker dev-machine with url of Che API
     *
     * @author Alexander Garagatyi
     */
    private class ApiEndpointEnvVariableProvider implements Provider<String> {
        @Inject
        @Named("machine.docker.che_api.endpoint")
        private String apiEndpoint;

        @Override
        public String get() {
            return DockerInstanceMetadata.API_ENDPOINT_URL_VARIABLE + '=' + apiEndpoint;
        }
    }

    /**
     * Add env variable to docker dev-machine with path to root folder of projects
     *
     * @author Alexander Garagatyi
     */
    private class ProjectsRootEnvVariableProvider implements Provider<String> {
        @Inject
        @Named("che.projects.root")
        private String projectFolderPath;

        @Override
        public String get() {
            return DockerInstanceMetadata.PROJECTS_ROOT_VARIABLE + '=' + projectFolderPath;
        }
    }

    /**
     * Reads path to extensions server archive to mount it to docker machine
     *
     * On Windows hosts MUST be locate in "user.home" directory in case limitation windows+docker.
     *
     * @author Alexander Garagatyi
     * @author Vitalii Parfonov
     */
    private class ExtServerVolumeProvider implements Provider<String> {
        @Inject
        @Named("machine.server.ext.archive")
        private String extServerArchivePath;

        @Override
        public String get() {
            if (SystemInfo.isWindows()) {
                String extServerArchivePath = System.getProperty("user.home") + "\\AppData\\Local\\che\\ext-server.zip";
                return extServerArchivePath + ":/mnt/che/ext-server.zip:ro";
            } else {
                return extServerArchivePath + ":/mnt/che/ext-server.zip:ro";
            }
        }
    }
}
