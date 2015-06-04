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
import com.google.inject.Singleton;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.event.ProjectActionEvent;
import org.eclipse.che.ide.api.event.ProjectActionHandler;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.OutputMessageUnmarshaller;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.machine.console.MachineConsolePresenter;
import org.eclipse.che.ide.extension.machine.client.outputspanel.OutputsContainerPresenter;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.CommandConsoleFactory;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.OutputConsole;
import org.eclipse.che.ide.util.UUID;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Manager for machine operations.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MachineManager implements ProjectActionHandler {

    private final MachineResources            machineResources;
    private final MachineServiceClient        machineServiceClient;
    private final MessageBus                  messageBus;
    private final MachineConsolePresenter     machineConsolePresenter;
    private final OutputsContainerPresenter   outputsContainerPresenter;
    private final CommandConsoleFactory       commandConsoleFactory;
    private final NotificationManager         notificationManager;
    private final MachineLocalizationConstant localizationConstant;
    private final WorkspaceAgent              workspaceAgent;
    private final MachineStateNotifier        machineStateNotifier;

    private String currentMachineId;

    @Inject
    public MachineManager(MachineResources machineResources,
                          MachineServiceClient machineServiceClient,
                          MessageBus messageBus,
                          MachineConsolePresenter machineConsolePresenter,
                          OutputsContainerPresenter outputsContainerPresenter,
                          CommandConsoleFactory commandConsoleFactory,
                          NotificationManager notificationManager,
                          MachineLocalizationConstant localizationConstant,
                          WorkspaceAgent workspaceAgent,
                          MachineStateNotifier machineStateNotifier) {
        this.machineResources = machineResources;
        this.machineServiceClient = machineServiceClient;
        this.messageBus = messageBus;
        this.machineConsolePresenter = machineConsolePresenter;
        this.outputsContainerPresenter = outputsContainerPresenter;
        this.commandConsoleFactory = commandConsoleFactory;
        this.notificationManager = notificationManager;
        this.localizationConstant = localizationConstant;
        this.workspaceAgent = workspaceAgent;
        this.machineStateNotifier = machineStateNotifier;
    }

    @Override
    public void onProjectOpened(ProjectActionEvent event) {
        machineServiceClient.getMachines(null).then(new Operation<List<MachineDescriptor>>() {
            @Override
            public void apply(List<MachineDescriptor> arg) throws OperationException {
                for (MachineDescriptor machineDescriptor : arg) {
                    if (machineDescriptor.isWorkspaceBound()) {
                        currentMachineId = machineDescriptor.getId();
                        return;
                    }
                }
                startMachine(true);
            }
        });
    }

    @Override
    public void onProjectClosing(ProjectActionEvent event) {
    }

    @Override
    public void onProjectClosed(ProjectActionEvent event) {
        currentMachineId = null;
        machineConsolePresenter.clear();
    }

    /** Start machine and bind workspace to created machine if {@code bindWorkspace} is {@code true}. */
    public void startMachine(final boolean bindWorkspace) {
        final String recipeScript = machineResources.testDockerRecipe().getText();
        final String outputChannel = "machine:output:" + UUID.uuid();
        subscribeToOutput(outputChannel);

        final Promise<MachineDescriptor> machinePromise = machineServiceClient.createMachineFromRecipe("docker",
                                                                                                       "Dockerfile",
                                                                                                       recipeScript,
                                                                                                       bindWorkspace,
                                                                                                       outputChannel);
        machinePromise.then(new Operation<MachineDescriptor>() {
            @Override
            public void apply(final MachineDescriptor arg) throws OperationException {
                MachineStateNotifier.RunningListener runningListener = null;
                if (bindWorkspace) {
                    runningListener = new MachineStateNotifier.RunningListener() {
                        @Override
                        public void onRunning() {
                            currentMachineId = arg.getId();
                        }
                    };
                }
                machineStateNotifier.trackMachine(arg.getId(), runningListener);
            }
        });
    }

    public void destroyMachine(final String machineId) {
        machineServiceClient.destroyMachine(machineId).then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                machineStateNotifier.trackMachine(machineId);
                if (getCurrentMachineId() != null && machineId.equals(getCurrentMachineId())) {
                    currentMachineId = null;
                }
            }
        });
    }

    /** Returns ID of the current machine (where workspace or current project is bound). */
    @Nullable
    public String getCurrentMachineId() {
        return currentMachineId;
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

    /** Execute the the given command configuration on current machine. */
    public void execute(@Nonnull CommandConfiguration configuration) {
        final String currentMachineId = getCurrentMachineId();
        if (currentMachineId == null) {
            notificationManager.showWarning(localizationConstant.noCurrentMachine());
            return;
        }

        final String outputChannel = "process:output:" + UUID.uuid();

        final OutputConsole console = commandConsoleFactory.create(configuration);
        console.attachToOutput(outputChannel);
        outputsContainerPresenter.addConsole(console);
        workspaceAgent.setActivePart(outputsContainerPresenter);

        machineServiceClient.executeCommand(currentMachineId, configuration.toCommandLine(), outputChannel);
    }
}
