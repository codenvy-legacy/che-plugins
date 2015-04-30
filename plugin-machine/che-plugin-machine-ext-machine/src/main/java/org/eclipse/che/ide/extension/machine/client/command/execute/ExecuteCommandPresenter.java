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

import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.machine.shared.dto.ProcessDescriptor;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.extension.machine.client.console.MachineConsolePresenter;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.util.UUID;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.StringUnmarshallerWS;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;

import javax.annotation.Nonnull;

/**
 * Presenter for executing command in machine.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ExecuteCommandPresenter implements ExecuteCommandView.ActionDelegate {
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private final MessageBus              messageBus;
    private final ExecuteCommandView      view;
    private final MachineServiceClient    machineServiceClient;
    private final MachineConsolePresenter machineConsolePresenter;
    private final AppContext appContext;

    @Inject
    protected ExecuteCommandPresenter(DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                      MessageBus messageBus,
                                      ExecuteCommandView view,
                                      MachineServiceClient machineServiceClient,
                                      MachineConsolePresenter machineConsolePresenter,
                                      AppContext appContext) {
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.messageBus = messageBus;
        this.view = view;
        this.machineServiceClient = machineServiceClient;
        this.machineConsolePresenter = machineConsolePresenter;
        this.appContext = appContext;
        this.view.setDelegate(this);
    }

    public void show() {
        view.showDialog();
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void onExecuteClicked() {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        view.close();

        // TODO: for now, command will be executed in the first machine to which project is bound
        machineServiceClient.getMachines(
                appContext.getWorkspace().getId(),
                currentProject.getRootProject().getPath(),
                new AsyncRequestCallback<Array<MachineDescriptor>>(dtoUnmarshallerFactory.newArrayUnmarshaller(MachineDescriptor.class)) {
                    @Override
                    protected void onSuccess(Array<MachineDescriptor> result) {
                        if (!result.isEmpty()) {
                            executeCommandInMachine(view.getCommand(), result.get(0).getId());
                        }
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(ExecuteCommandPresenter.class, exception);
                    }
                });
    }

    private void executeCommandInMachine(final String command, final String machineId) {
        final String outputChannel = getNewOutputChannel();

        subscribeToOutput(outputChannel);

        machineServiceClient.executeCommandInMachine(
                machineId, command, outputChannel,
                new AsyncRequestCallback<ProcessDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(ProcessDescriptor.class)) {
                    @Override
                    protected void onSuccess(ProcessDescriptor result) {
                        Log.info(ExecuteCommandPresenter.class, "Process with PID" + result.getPid() + " executed");
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(ExecuteCommandPresenter.class, exception);
                    }
                });
    }

    @Nonnull
    private String getNewOutputChannel() {
        return "process:output:" + UUID.uuid();
    }

    private void subscribeToOutput(final String channel) {
        try {
            messageBus.subscribe(
                    channel,
                    new SubscriptionHandler<String>(new StringUnmarshallerWS()) {
                        @Override
                        protected void onMessageReceived(String result) {
                            final JSONString jsonString = JSONParser.parseStrict(result).isString();
                            machineConsolePresenter.print(jsonString.stringValue());
                        }

                        @Override
                        protected void onErrorReceived(Throwable exception) {
                            Log.error(ExecuteCommandPresenter.class, exception);
                        }
                    });
        } catch (WebSocketException e) {
            Log.error(ExecuteCommandPresenter.class, e);
        }
    }
}
