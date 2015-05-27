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
import org.eclipse.che.api.machine.shared.dto.MachineStateEvent;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
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
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.util.UUID;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.eclipse.che.api.machine.shared.dto.MachineStateEvent.EventType.RUNNING;

/**
 * Manager for machine operations.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MachineManager {

    /** WebSocket channel to receive messages about changing machine state (machine:state:machineID). */
    private static final String MACHINE_STATE_CHANNEL = "machine:state:";

    private final MachineResources            machineResources;
    private final MachineServiceClient        machineServiceClient;
    private final DtoUnmarshallerFactory      dtoUnmarshallerFactory;
    private final MessageBus                  messageBus;
    private final MachineConsolePresenter     machineConsolePresenter;
    private final OutputsContainerPresenter   outputsContainerPresenter;
    private final CommandConsoleFactory       commandConsoleFactory;
    private final NotificationManager         notificationManager;
    private final MachineLocalizationConstant localizationConstant;
    private final WorkspaceAgent              workspaceAgent;

    private String currentMachineId;

    @Inject
    public MachineManager(MachineResources machineResources,
                          MachineServiceClient machineServiceClient,
                          DtoUnmarshallerFactory dtoUnmarshallerFactory,
                          MessageBus messageBus,
                          MachineConsolePresenter machineConsolePresenter,
                          OutputsContainerPresenter outputsContainerPresenter,
                          CommandConsoleFactory commandConsoleFactory,
                          NotificationManager notificationManager,
                          MachineLocalizationConstant localizationConstant,
                          WorkspaceAgent workspaceAgent) {
        this.machineResources = machineResources;
        this.machineServiceClient = machineServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.messageBus = messageBus;
        this.machineConsolePresenter = machineConsolePresenter;
        this.outputsContainerPresenter = outputsContainerPresenter;
        this.commandConsoleFactory = commandConsoleFactory;
        this.notificationManager = notificationManager;
        this.localizationConstant = localizationConstant;
        this.workspaceAgent = workspaceAgent;
    }

    /** Returns ID of the current machine, where current project is bound. */
    @Nullable
    public String getCurrentMachineId() {
        return currentMachineId;
    }

    /** Sets ID of the current machine, where current project is bound. */
    public void setCurrentMachineId(String currentMachineId) {
        this.currentMachineId = currentMachineId;
    }

    /** Start machine and bind project. */
    public void startMachineAndBindProject(final String projectPath) {
        final String recipeScript = machineResources.testDockerRecipe().getText();
        final String outputChannel = "machine:output:" + UUID.uuid();
        subscribeToOutput(outputChannel);

        final Promise<MachineDescriptor> machinePromise = machineServiceClient.createMachineFromRecipe("docker",
                                                                                                       "Dockerfile",
                                                                                                       recipeScript,
                                                                                                       outputChannel);
        machinePromise.then(new Operation<MachineDescriptor>() {
            @Override
            public void apply(MachineDescriptor arg) throws OperationException {
                bindProjectWhenMachineWillRun(projectPath, arg.getId());
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

    private void bindProjectWhenMachineWillRun(final String projectPath, final String machineId) {
        try {
            messageBus.subscribe(
                    MACHINE_STATE_CHANNEL + machineId,
                    new SubscriptionHandler<MachineStateEvent>(dtoUnmarshallerFactory.newWSUnmarshaller(MachineStateEvent.class)) {
                        @Override
                        protected void onMessageReceived(MachineStateEvent result) {
                            if (RUNNING == result.getEventType()) {
                                bindProject(projectPath, machineId);
                            }
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

    /** Bind the project to the machine and set the given machine as current. */
    public void bindProject(final String projectPath, final String machineId) {
        machineServiceClient.bindProject(machineId, projectPath).then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                setCurrentMachineId(machineId);
            }
        });
    }

    public void destroyMachine(final String machineId) {
        machineServiceClient.destroyMachine(machineId).then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                if (getCurrentMachineId() != null && machineId.equals(getCurrentMachineId())) {
                    setCurrentMachineId(null);
                }
            }
        });
    }

    /** Execute the the given command configuration on current machine. */
    public void execute(@Nonnull CommandConfiguration configuration) {
        final String currentMachineId = getCurrentMachineId();
        if (currentMachineId == null) {
            notificationManager.showWarning(localizationConstant.noDevMachine());
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
