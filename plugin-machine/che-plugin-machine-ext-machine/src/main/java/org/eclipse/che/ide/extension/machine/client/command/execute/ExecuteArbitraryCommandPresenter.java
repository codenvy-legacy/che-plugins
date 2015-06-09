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
package org.eclipse.che.ide.extension.machine.client.command.execute;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.OutputMessageUnmarshaller;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
import org.eclipse.che.ide.extension.machine.client.machine.console.MachineConsolePresenter;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.UUID;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;

/**
 * Presenter for executing arbitrary command in machine.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ExecuteArbitraryCommandPresenter implements ExecuteArbitraryCommandView.ActionDelegate {
    private final MessageBus                  messageBus;
    private final ExecuteArbitraryCommandView view;
    private final MachineServiceClient        machineServiceClient;
    private final MachineConsolePresenter     machineConsole;
    private final MachineManager              machineManager;
    private final DialogFactory               dialogFactory;
    private final MachineLocalizationConstant localizationConstant;

    @Inject
    protected ExecuteArbitraryCommandPresenter(MessageBus messageBus,
                                               ExecuteArbitraryCommandView view,
                                               MachineServiceClient machineServiceClient,
                                               MachineConsolePresenter machineConsole,
                                               MachineManager machineManager,
                                               DialogFactory dialogFactory,
                                               MachineLocalizationConstant localizationConstant) {
        this.messageBus = messageBus;
        this.view = view;
        this.machineServiceClient = machineServiceClient;
        this.machineConsole = machineConsole;
        this.machineManager = machineManager;
        this.dialogFactory = dialogFactory;
        this.localizationConstant = localizationConstant;
        this.view.setDelegate(this);
    }

    /** Show dialog. */
    public void show() {
        view.show();
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void onExecuteClicked() {
        view.close();

        final String devMachineId = machineManager.getDeveloperMachineId();
        if (devMachineId != null) {
            executeCommandInMachine(view.getCommand(), devMachineId);
        } else {
            dialogFactory.createMessageDialog("", localizationConstant.noCurrentMachine(), null).show();
        }
    }

    private void executeCommandInMachine(String command, String machineId) {
        final String outputChannel = "process:output:" + UUID.uuid();
        subscribeToOutput(outputChannel);
        machineServiceClient.executeCommand(machineId, command, outputChannel);
    }

    private void subscribeToOutput(final String channel) {
        try {
            messageBus.subscribe(
                    channel,
                    new SubscriptionHandler<String>(new OutputMessageUnmarshaller()) {
                        @Override
                        protected void onMessageReceived(String result) {
                            machineConsole.print(result);
                        }

                        @Override
                        protected void onErrorReceived(Throwable exception) {
                            Log.error(ExecuteArbitraryCommandPresenter.class, exception);
                        }
                    });
        } catch (WebSocketException e) {
            Log.error(ExecuteArbitraryCommandPresenter.class, e);
        }
    }
}
