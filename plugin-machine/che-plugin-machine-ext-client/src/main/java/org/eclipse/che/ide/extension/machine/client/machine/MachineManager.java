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
package org.eclipse.che.ide.extension.machine.client.machine;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.machine.gwt.client.ExtServerStateController;
import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.gwt.client.OutputMessageUnmarshaller;
import org.eclipse.che.api.machine.gwt.client.events.DevMachineStateEvent;
import org.eclipse.che.api.machine.gwt.client.events.DevMachineStateHandler;
import org.eclipse.che.api.machine.shared.dto.LimitsDto;
import org.eclipse.che.api.machine.shared.dto.MachineConfigDto;
import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.api.machine.shared.dto.MachineSourceDto;
import org.eclipse.che.api.machine.shared.dto.MachineStateDto;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.workspace.gwt.client.WorkspaceServiceClient;
import org.eclipse.che.api.workspace.shared.dto.UsersWorkspaceDto;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.machine.MachineStatusNotifier.RunningListener;
import org.eclipse.che.ide.extension.machine.client.machine.console.MachineConsolePresenter;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStartingEvent;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStateEvent;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStateHandler;
import org.eclipse.che.ide.extension.machine.client.watcher.SystemFileWatcher;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.MessageBusProvider;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;
import org.eclipse.che.ide.workspace.start.StartWorkspaceEvent;
import org.eclipse.che.ide.workspace.start.StartWorkspaceHandler;

import static org.eclipse.che.ide.extension.machine.client.machine.MachineManager.MachineOperationType.DESTROY;
import static org.eclipse.che.ide.extension.machine.client.machine.MachineManager.MachineOperationType.RESTART;
import static org.eclipse.che.ide.extension.machine.client.machine.MachineManager.MachineOperationType.START;

/**
 * Manager for machine operations.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MachineManager {

    private final ExtServerStateController extServerStateController;
    private final MachineServiceClient     machineServiceClient;
    private final WorkspaceServiceClient   workspaceServiceClient;
    private final MachineConsolePresenter  machineConsolePresenter;
    private final NotificationManager      notificationManager;
    private final MachineStatusNotifier    machineStatusNotifier;
    private final EntityFactory            entityFactory;
    private final AppContext               appContext;
    private final DtoFactory               dtoFactory;
    private final EventBus                 eventBus;

    private MessageBus messageBus;
    private Machine    devMachine;
    private boolean    isMachineRestarting;

    @Inject
    public MachineManager(ExtServerStateController extServerStateController,
                          MachineServiceClient machineServiceClient,
                          WorkspaceServiceClient workspaceServiceClient,
                          MachineConsolePresenter machineConsolePresenter,
                          NotificationManager notificationManager,
                          MachineStatusNotifier machineStatusNotifier,
                          final MessageBusProvider messageBusProvider,
                          EntityFactory entityFactory,
                          EventBus eventBus,
                          AppContext appContext,
                          Provider<SystemFileWatcher> systemFileWatcherProvider,
                          DtoFactory dtoFactory) {
        this.extServerStateController = extServerStateController;
        this.machineServiceClient = machineServiceClient;
        this.workspaceServiceClient = workspaceServiceClient;
        this.machineConsolePresenter = machineConsolePresenter;
        this.notificationManager = notificationManager;
        this.machineStatusNotifier = machineStatusNotifier;
        this.entityFactory = entityFactory;
        this.appContext = appContext;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;

        this.messageBus = messageBusProvider.getMessageBus();

        systemFileWatcherProvider.get();

        eventBus.addHandler(StartWorkspaceEvent.TYPE, new StartWorkspaceHandler() {
            @Override
            public void onWorkspaceStarted(UsersWorkspaceDto workspace) {
                messageBus = messageBusProvider.getMessageBus();
            }
        });

        eventBus.addHandler(DevMachineStateEvent.TYPE, new DevMachineStateHandler() {
            @Override
            public void onMachineStarted(DevMachineStateEvent event) {
                onMachineRunning(event.getMachineId());
            }

            @Override
            public void onMachineDestroyed(DevMachineStateEvent event) {

            }
        });
    }

    public void restartMachine(final MachineStateDto machineState) {
        eventBus.addHandler(MachineStateEvent.TYPE, new MachineStateHandler() {
            @Override
            public void onMachineRunning(MachineStateEvent event) {

            }

            @Override
            public void onMachineDestroyed(MachineStateEvent event) {
                if (isMachineRestarting) {
                    final String recipeUrl = machineState.getSource().getLocation();
                    final String displayName = machineState.getName();
                    final boolean isDev = machineState.isDev();

                    startMachine(recipeUrl, displayName, isDev, RESTART);

                    isMachineRestarting = false;
                }
            }
        });

        destroyMachine(machineState).then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                isMachineRestarting = true;
            }
        });
    }

    /** Start new machine. */
    public void startMachine(String recipeURL, String displayName) {
        startMachine(recipeURL, displayName, false, START);
    }

    /** Start new machine as dev-machine (bind workspace to running machine). */
    public void startDevMachine(String recipeURL, String displayName) {
        startMachine(recipeURL, displayName, true, START);
    }

    private void startMachine(final String recipeURL,
                              final String displayName,
                              final boolean isDev,
                              final MachineOperationType operationType) {

        LimitsDto limitsDto = dtoFactory.createDto(LimitsDto.class).withMemory(1024);
        if (isDev) {
            limitsDto.withMemory(3072);
        }
        MachineSourceDto sourceDto = dtoFactory.createDto(MachineSourceDto.class).withType("Recipe").withLocation(recipeURL);

        MachineConfigDto configDto = dtoFactory.createDto(MachineConfigDto.class)
                                               .withDev(isDev)
                                               .withName(displayName)
                                               .withSource(sourceDto)
                                               .withLimits(limitsDto)
                                               .withType("docker");

        Promise<MachineStateDto> machineStatePromise = workspaceServiceClient.createMachine(appContext.getWorkspace().getId(), configDto);

        machineStatePromise.then(new Operation<MachineStateDto>() {
            @Override
            public void apply(final MachineStateDto machineStateDto) throws OperationException {
                eventBus.fireEvent(new MachineStartingEvent(machineStateDto));

                subscribeToOutput(machineStateDto.getChannels().getOutput());

                RunningListener runningListener = null;

                if (isDev) {
                    runningListener = new RunningListener() {
                        @Override
                        public void onRunning() {
                            onMachineRunning(machineStateDto.getId());
                        }
                    };
                }

                machineStatusNotifier.trackMachine(machineStateDto, runningListener, operationType);
            }
        });
    }

    public void onMachineRunning(final String machineId) {
        machineServiceClient.getMachine(machineId).then(new Operation<MachineDto>() {
            @Override
            public void apply(MachineDto machineDto) throws OperationException {
                appContext.setDevMachineId(machineId);
                devMachine = entityFactory.createMachine(machineDto);
                extServerStateController.initialize(devMachine.getWsServerExtensionsUrl() + "/" + appContext.getWorkspace().getId());
            }
        });
    }

    public Promise<Void> destroyMachine(final MachineStateDto machineState) {
        return machineServiceClient.destroyMachine(machineState.getId()).then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                machineStatusNotifier.trackMachine(machineState, DESTROY);

                final String devMachineId = appContext.getDevMachineId();
                if (devMachineId != null && machineState.getId().equals(devMachineId)) {
                    appContext.setDevMachineId(null);
                }
            }
        });
    }

    private void subscribeToOutput(final String channel) {
        try {
            messageBus.subscribe(
                    channel,
                    new SubscriptionHandler<String>(new OutputMessageUnmarshaller()) {
                        @Override
                        protected void onMessageReceived(String result) {
                            machineConsolePresenter.print(result);
                        }

                        @Override
                        protected void onErrorReceived(Throwable exception) {
                            notificationManager.showError(exception.getMessage());
                        }
                    });
        } catch (WebSocketException e) {
            Log.error(MachineManager.class, e);
            notificationManager.showError(e.getMessage());
        }
    }

    enum MachineOperationType {
        START, RESTART, DESTROY
    }
}
