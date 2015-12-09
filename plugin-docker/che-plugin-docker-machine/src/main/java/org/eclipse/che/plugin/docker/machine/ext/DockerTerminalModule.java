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
import org.eclipse.che.plugin.docker.machine.ServerConf;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

/**
 * Guice module for terminal feature in docker machines
 *
 * @author Alexander Garagatyi
 */
// Not a DynaModule, install manually
public class DockerTerminalModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DockerMachineTerminalLauncher.class).asEagerSingleton();

        bindConstant().annotatedWith(Names.named(DockerMachineTerminalLauncher.START_TERMINAL_COMMAND))
                      .to("mkdir -p ~/che " +
                          "&& cp /mnt/che/terminal -nR ~/che" +
                          "&& ~/che/terminal/terminal -addr :4411 -cmd /bin/bash -static ~/che/terminal/");

        Multibinder<ServerConf> machineServers = Multibinder.newSetBinder(binder(),
                                                                          ServerConf.class,
                                                                          Names.named("machine.docker.machine_servers"));
        machineServers.addBinding().toInstance(new ServerConf("terminal", "4411", "http"));

        Multibinder<String> volumesMultibinder =
                Multibinder.newSetBinder(binder(), String.class, Names.named("machine.docker.machine_volumes"));
        volumesMultibinder.addBinding().toProvider(TerminalVolumeProvider.class).in(Singleton.class);
    }

    /**
     * Provides volumes configuration of machine for terminal
     *
     * On Windows MUST be locate in "user.home" directory in case limitation windows+docker.
     *
     * @author Alexander Garagatyi
     * @author Vitalii Parfonov
     */
    private static class TerminalVolumeProvider implements Provider<String> {
        @Inject
        @Named("machine.server.terminal.archive")
        private String terminalArchivePath;

        @Override
        public String get() {
            if (SystemInfo.isWindows()) {
                return System.getProperty("user.home") + "\\AppData\\Local\\che\\terminal" + ":/mnt/che/terminal:ro";
            } else {
                return terminalArchivePath + ":/mnt/che/terminal:ro";
            }
        }
    }
}
