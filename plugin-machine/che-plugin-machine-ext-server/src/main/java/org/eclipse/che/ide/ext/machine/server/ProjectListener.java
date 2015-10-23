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

package org.eclipse.che.ide.ext.machine.server;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.model.machine.Server;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.notification.EventSubscriber;
import org.eclipse.che.api.machine.server.MachineManager;
import org.eclipse.che.api.machine.server.exception.MachineException;
import org.eclipse.che.api.machine.server.spi.Instance;
import org.eclipse.che.api.machine.shared.dto.event.MachineStatusEvent;
import org.eclipse.che.api.project.server.notification.ProjectItemModifiedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.eclipse.che.api.machine.shared.dto.event.MachineStatusEvent.EventType;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class ProjectListener {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectListener.class);

    private ConcurrentMap<String, CopyOnWriteArraySet<String>> workspace2machines = new ConcurrentHashMap<>();
    private ConcurrentMap<String, String>                      machine2workspace  = new ConcurrentHashMap<>();

    private final MachineManager machineManager;
    private       int            extServicesPort;

    @Inject
    public ProjectListener(EventService eventService, final MachineManager machineManager,
                           @Named("machine.extension.api_port") int extServicesPort) {
        this.machineManager = machineManager;
        this.extServicesPort = extServicesPort;
        eventService.subscribe(new EventSubscriber<ProjectItemModifiedEvent>() {
            @Override
            public void onEvent(ProjectItemModifiedEvent event) {
                notifyMachines(event);
            }
        });

        eventService.subscribe(new EventSubscriber<MachineStatusEvent>() {
            @Override
            public void onEvent(MachineStatusEvent event) {
                String machineId = event.getMachineId();
                EventType type = event.getEventType();
                switch (type) {
                    case RUNNING:
                        addMachine(machineId, machineManager);
                        break;
                    case DESTROYED:
                    case ERROR:
                        removeMachine(machineId);
                        break;
                }
            }
        });
    }

    private void notifyMachines(ProjectItemModifiedEvent event) {
        String workspace = event.getWorkspace();
        if (workspace2machines.containsKey(workspace)) {
            CopyOnWriteArraySet<String> machines = workspace2machines.get(workspace);
            if (!machines.isEmpty()) {
                Gson gson = new Gson();
                String json = gson.toJson(event);
                for (String machine : machines) {
                    sendEvent(machine, json);
                }
            }
        }
    }

    private void sendEvent(String machineId, String json) {
        HttpURLConnection connection;
        try {
            final Instance machine = machineManager.getMachine(machineId);
            final Server server = machine.getMetadata().getServers().get(Integer.toString(extServicesPort));
            if (server == null) {
                throw new ServerException("No extension server found in machine");
            }

            final StringBuilder url = new StringBuilder("http://").append(server.getAddress());
            url.append("/machine/project/item/event");
            connection = (HttpURLConnection)new URL(url.toString()).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            ByteStreams.copy(new ByteArrayInputStream(json.getBytes()), connection.getOutputStream());
            connection.getOutputStream().flush();
            InputStream inputStream = null;
            try {
                connection.connect();
                inputStream = connection.getInputStream();
            } finally {
                connection.disconnect();
                if(inputStream != null){
                    inputStream.close();
                }
            }

        } catch (NotFoundException | ServerException | IOException e) {
            LOG.error("Can't send event to machine.", e);
        }
    }


    private void removeMachine(String machineId) {
        String wsId = machine2workspace.get(machineId);
        if (wsId != null) {
            machine2workspace.remove(machineId);
            if (workspace2machines.containsKey(wsId)) {
                workspace2machines.get(wsId).remove(machineId);
            }
        }
    }

    private void addMachine(String machineId, MachineManager machineManager) {
        try {
            final Instance machine = machineManager.getMachine(machineId);
            String workspaceId = machine.getWorkspaceId();
            if (workspaceId != null) {
                if (!workspace2machines.containsKey(workspaceId)) {
                    workspace2machines.putIfAbsent(workspaceId, new CopyOnWriteArraySet<String>());
                }
                workspace2machines.get(workspaceId).add(machineId);
                machine2workspace.putIfAbsent(machineId, workspaceId);
            }
        } catch (NotFoundException | MachineException e) {
            LOG.error("Can't find machine.", e);
        }
    }


}
