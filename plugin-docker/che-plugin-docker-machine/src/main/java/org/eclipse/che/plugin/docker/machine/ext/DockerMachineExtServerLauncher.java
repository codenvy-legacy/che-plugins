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

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.notification.EventSubscriber;
import org.eclipse.che.api.machine.server.MachineManager;
import org.eclipse.che.api.machine.server.exception.MachineException;
import org.eclipse.che.api.machine.server.model.impl.CommandImpl;
import org.eclipse.che.api.machine.shared.dto.event.MachineStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Starts extensions server in the machine after start
 *
 * @author Alexander Garagatyi
 */
@Singleton
public class DockerMachineExtServerLauncher {
    public static final String WS_AGENT_PROCESS_START_COMMAND  = "machine.server.ext.run_command";
    public static final String WS_AGENT_PROCESS_OUTPUT_CHANNEL = "workspace:%s:ext-server:output";
    public static final String WS_AGENT_PROCESS_NAME           = "CheWsAgent";

    private static final Logger LOG = LoggerFactory.getLogger(DockerMachineExtServerLauncher.class);

    private final EventService   eventService;
    private final MachineManager machineManager;
    private final String         extServerStartCommandLine;

    @Inject
    public DockerMachineExtServerLauncher(EventService eventService,
                                          MachineManager machineManager,
                                          @Named(WS_AGENT_PROCESS_START_COMMAND) String extServerStartCommandLine) {
        this.eventService = eventService;
        this.machineManager = machineManager;
        this.extServerStartCommandLine = extServerStartCommandLine;
    }

    @PostConstruct
    void start() {
        eventService.subscribe(new EventSubscriber<MachineStatusEvent>() {
            @Override
            public void onEvent(MachineStatusEvent event) {
                // TODO launch it on dev machines only
                if (event.getEventType() == MachineStatusEvent.EventType.RUNNING) {

                    // ext server doesn't exist in non-dev machines
                    if (event.isDev()) {
                        try {
                            machineManager.exec(event.getMachineId(),
                                                new CommandImpl(WS_AGENT_PROCESS_NAME, extServerStartCommandLine, "Arbitrary"),
                                                String.format(WS_AGENT_PROCESS_OUTPUT_CHANNEL, event.getWorkspaceId()));
                        } catch (MachineException | NotFoundException | BadRequestException wsAgentLaunchingExc) {
                            // TODO send event about failed start of ws agent
                            LOG.error(wsAgentLaunchingExc.getLocalizedMessage(), wsAgentLaunchingExc);
                        }
                    }
                }
            }
        });
    }
}
