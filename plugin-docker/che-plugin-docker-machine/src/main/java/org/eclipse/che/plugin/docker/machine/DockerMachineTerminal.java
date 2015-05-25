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
package org.eclipse.che.plugin.docker.machine;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.notification.EventSubscriber;
import org.eclipse.che.api.machine.server.MachineException;
import org.eclipse.che.api.machine.server.MachineImpl;
import org.eclipse.che.api.machine.server.MachineManager;
import org.eclipse.che.api.machine.shared.dto.MachineStateEvent;
import org.eclipse.che.plugin.docker.client.DockerConnector;
import org.eclipse.che.plugin.docker.client.Exec;
import org.eclipse.che.plugin.docker.client.LogMessage;
import org.eclipse.che.plugin.docker.client.LogMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * Starts websocket terminal in the machine after container start
 *
 * @author Alexander Garagatyi
 */
@Singleton // must be eager
public class DockerMachineTerminal {
    private static final Logger LOG = LoggerFactory.getLogger(DockerMachineTerminal.class);

    private final EventService    eventService;
    private final DockerConnector docker;
    private final MachineManager  machineManager;

    @Inject
    public DockerMachineTerminal(EventService eventService,
                                 DockerConnector docker,
                                 MachineManager machineManager) {
        this.eventService = eventService;
        this.docker = docker;
        this.machineManager = machineManager;
    }

    @PostConstruct
    private void start() {
        eventService.subscribe(new EventSubscriber<MachineStateEvent>() {
            @Override
            public void onEvent(MachineStateEvent event) {
                try {
                    final MachineImpl machine = machineManager.getMachine(event.getMachineId());
                    final String containerId = machine.getMetadata().getProperties().get("id");

                    final Exec exec = docker.createExec(containerId, true, "/bin/bash", "-c",
                                                        "/usr/local/codenvy/terminal/terminal -addr :4300 -cmd /bin/sh -static /usr/local/codenvy/terminal/");
                    docker.startExec(exec.getId(), new LogMessageProcessor() {
                        @Override
                        public void process(LogMessage logMessage) {
                            // TODO log to machines logs
                            LOG.error(String.format("Terminal error in container %s. %s", containerId, logMessage.getContent()));
                        }
                    });
                    // TODO Add link to machine
                } catch (IOException | MachineException | NotFoundException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        });
    }
}
