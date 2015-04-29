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

import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.machine.shared.dto.MachineStateEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
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

import static org.eclipse.che.api.machine.shared.dto.MachineStateEvent.EventType.RUNNING;

/** @author Artem Zatsarynnyy */
@Singleton
public class MachineManager {

    /** WebSocket channel to receive messages about changing machine state (machine:state:machineID). */
    private static final String MACHINE_STATE_CHANNEL = "machine:state:";
    private static String currentMachineId;

    private final AppContext appContext;
    private final MachineResources machineResources;
    private final MachineServiceClient    machineServiceClient;
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private final MessageBus              messageBus;
    private final MachineConsolePresenter machineConsolePresenter;

    @Inject
    public MachineManager(AppContext appContext,
                          MachineResources machineResources,
                          MachineServiceClient machineServiceClient,
                          DtoUnmarshallerFactory dtoUnmarshallerFactory,
                          MessageBus messageBus,
                          MachineConsolePresenter machineConsolePresenter) {
        this.appContext = appContext;
        this.machineResources = machineResources;
        this.machineServiceClient = machineServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.messageBus = messageBus;
        this.machineConsolePresenter = machineConsolePresenter;
    }

    /** Returns ID of the machine where current project is bound. */
    public String getCurrentMachineId() {
        return currentMachineId;
    }

    public void startMachineAndBindProject(final String projectPath) {
        final String outputChannel = getNewOutputChannel();

        subscribeToOutput(outputChannel);

        machineServiceClient.createMachineFromRecipe(
                appContext.getWorkspace().getId(),
                "docker",
                "Dockerfile",
                machineResources.testDockerRecipe().getText(),
                outputChannel,
                new AsyncRequestCallback<MachineDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(MachineDescriptor.class)) {
                    @Override
                    protected void onSuccess(MachineDescriptor result) {
                        bindProjectWhenMachineWillRun(projectPath, result.getId());
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(MachineManager.class, exception);
                    }
                });
    }

    @Nonnull
    private String getNewOutputChannel() {
        return "machine:output:" + UUID.uuid();
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
                            Log.error(MachineManager.class, exception);
                        }
                    });
        } catch (WebSocketException e) {
            Log.error(MachineManager.class, e);
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
                            Log.error(MachineManager.class, exception);
                        }
                    });
        } catch (WebSocketException e) {
            Log.error(MachineManager.class, e);
        }
    }

    public void bindProject(final String projectPath, final String machineId) {
        machineServiceClient.bindProject(machineId, projectPath, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                currentMachineId = machineId;
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(MachineManager.class, exception);
            }
        });
    }
}
