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
import org.eclipse.che.api.machine.shared.MachineState;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.machine.shared.dto.event.MachineStateEvent;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.events.MessageHandler;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;
import org.eclipse.che.ide.websocket.rest.Unmarshallable;

import javax.annotation.Nonnull;

import static org.eclipse.che.api.machine.shared.MachineState.CREATING;
import static org.eclipse.che.api.machine.shared.MachineState.DESTROYING;
import static org.eclipse.che.ide.api.notification.Notification.Status.FINISHED;
import static org.eclipse.che.ide.api.notification.Notification.Status.PROGRESS;
import static org.eclipse.che.ide.api.notification.Notification.Type.ERROR;
import static org.eclipse.che.ide.api.notification.Notification.Type.INFO;

/**
 * Notifies about changing machine state.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
class MachineStateNotifier {

    /** WebSocket channel to receive messages about changing machine state. */
    private static final String MACHINE_STATE_WS_CHANNEL = "machine:state:";

    private final MessageBus                  messageBus;
    private final DtoUnmarshallerFactory      dtoUnmarshallerFactory;
    private final NotificationManager         notificationManager;
    private final MachineServiceClient        machineServiceClient;
    private final MachineLocalizationConstant localizationConstant;

    @Inject
    MachineStateNotifier(MessageBus messageBus,
                         DtoUnmarshallerFactory dtoUnmarshallerFactory,
                         NotificationManager notificationManager,
                         MachineServiceClient machineServiceClient,
                         MachineLocalizationConstant localizationConstant) {
        this.messageBus = messageBus;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.machineServiceClient = machineServiceClient;
        this.localizationConstant = localizationConstant;
    }

    /**
     * Start tracking machine state and notify about state changing.
     *
     * @param machineId
     *         ID of the machine to track
     */
    void trackMachine(@Nonnull final String machineId) {
        final String wsChannel = MACHINE_STATE_WS_CHANNEL + machineId;
        final Notification notification = new Notification("", INFO, true);

        final Unmarshallable<MachineStateEvent> unmarshaller = dtoUnmarshallerFactory.newWSUnmarshaller(MachineStateEvent.class);
        final MessageHandler handler = new SubscriptionHandler<MachineStateEvent>(unmarshaller) {
            @Override
            protected void onMessageReceived(MachineStateEvent result) {
                switch (result.getEventType()) {
                    case RUNNING:
                        unsubscribe(wsChannel, this);
                        notification.setMessage(localizationConstant.notificationMachineIsRunning(result.getMachineId()));
                        notification.setStatus(FINISHED);
                        notification.setType(INFO);
                        break;
                    case DESTROYED:
                        unsubscribe(wsChannel, this);
                        notification.setMessage(localizationConstant.notificationMachineDestroyed(result.getMachineId()));
                        notification.setStatus(FINISHED);
                        notification.setType(INFO);
                        break;
                    case ERROR:
                        unsubscribe(wsChannel, this);
                        notification.setMessage(result.getError());
                        notification.setStatus(FINISHED);
                        notification.setType(ERROR);
                        break;
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                unsubscribe(wsChannel, this);
                notification.setMessage(exception.getMessage());
                notification.setStatus(FINISHED);
                notification.setType(ERROR);
            }
        };

        machineServiceClient.getMachine(machineId).then(new Operation<MachineDescriptor>() {
            @Override
            public void apply(MachineDescriptor arg) throws OperationException {
                final MachineState state = arg.getState();
                if (state == CREATING || state == DESTROYING) {
                    notification.setMessage(state == CREATING ? localizationConstant.notificationCreatingMachine(machineId)
                                                              : localizationConstant.notificationDestroyingMachine(machineId));
                    notification.setStatus(PROGRESS);
                    notificationManager.showNotification(notification);

                    try {
                        messageBus.subscribe(wsChannel, handler);
                    } catch (WebSocketException e) {
                        Log.error(getClass(), e);
                    }
                }
            }
        });
    }

    private void unsubscribe(String wsChannel, MessageHandler handler) {
        try {
            messageBus.unsubscribe(wsChannel, handler);
        } catch (WebSocketException e) {
            Log.error(getClass(), e);
        }
    }
}
