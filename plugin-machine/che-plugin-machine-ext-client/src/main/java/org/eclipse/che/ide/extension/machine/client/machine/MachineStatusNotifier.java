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

import com.google.gwt.core.client.Callback;
import com.google.gwt.inject.client.multibindings.GinMapBinder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.machine.shared.dto.event.MachineStatusEvent;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.bootstrap.ProfileComponent;
import org.eclipse.che.ide.bootstrap.ProjectTemplatesComponent;
import org.eclipse.che.ide.bootstrap.ProjectTypeComponent;
import org.eclipse.che.ide.core.Component;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager.MachineOperationType;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStateEvent;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.events.MessageHandler;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;
import org.eclipse.che.ide.websocket.rest.Unmarshallable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.eclipse.che.ide.api.notification.Notification.Status.FINISHED;
import static org.eclipse.che.ide.api.notification.Notification.Status.PROGRESS;
import static org.eclipse.che.ide.api.notification.Notification.Type.ERROR;
import static org.eclipse.che.ide.api.notification.Notification.Type.INFO;
import static org.eclipse.che.ide.extension.machine.client.machine.MachineManager.MachineOperationType.RESTART;

/**
 * Notifies about changing machine state.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
class MachineStatusNotifier {

    /** WebSocket channel to receive messages about changing machine state. */
    public static final String MACHINE_STATUS_WS_CHANNEL = "machine:status:";

    private final MessageBus                  messageBus;
    private final EventBus                    eventBus;
    private final DtoUnmarshallerFactory      dtoUnmarshallerFactory;
    private final NotificationManager         notificationManager;
    private final MachineLocalizationConstant locale;
    private       ProjectTypeComponent        projectTypeComponent;
    private ProjectTemplatesComponent projectTemplatesComponent;

    @Inject
    MachineStatusNotifier(MessageBus messageBus,
                          EventBus eventBus,
                          DtoUnmarshallerFactory dtoUnmarshallerFactory,
                          NotificationManager notificationManager,
                          MachineLocalizationConstant locale,
                          ProjectTypeComponent projectTypeComponent,
                          ProjectTemplatesComponent projectTemplatesComponent) {
        this.messageBus = messageBus;
        this.eventBus = eventBus;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.projectTypeComponent = projectTypeComponent;
        this.projectTemplatesComponent = projectTemplatesComponent;
    }

    /**
     * Start tracking machine state and notify about state changing.
     *
     * @param machine
     *         machine to track
     */
    void trackMachine(@Nonnull final Machine machine, @Nonnull final MachineOperationType operationType) {
        trackMachine(machine, null, operationType);
    }

    /**
     * Start tracking machine state and notify about state changing.
     *
     * @param machine
     *         machine to track
     * @param runningListener
     *         listener that will be notified when machine is running
     */
    void trackMachine(@Nonnull final Machine machine,
                      @Nullable final RunningListener runningListener,
                      @Nonnull final MachineOperationType operationType) {
        final String wsChannel = MACHINE_STATUS_WS_CHANNEL + machine.getId();
        final Notification notification = new Notification("", INFO, true);
        final String machineName = machine.getDisplayName();

        final Unmarshallable<MachineStatusEvent> unmarshaller = dtoUnmarshallerFactory.newWSUnmarshaller(MachineStatusEvent.class);
        final MessageHandler messageHandler = new SubscriptionHandler<MachineStatusEvent>(unmarshaller) {
            @Override
            protected void onMessageReceived(MachineStatusEvent result) {

                switch (result.getEventType()) {
                    case RUNNING:
                        unsubscribe(wsChannel, this);
                        projectTypeComponent.start(new Callback<Component, Exception>() {

                            @Override
                            public void onFailure(Exception reason) {

                            }

                            @Override
                            public void onSuccess(Component result) {
                                Log.info(getClass(), "projectTypeComponent >>>>>>>>>>>>>>>>>");

                            }
                        });

                        projectTemplatesComponent.start(new Callback<Component, Exception>() {

                            @Override
                            public void onFailure(Exception reason) {

                            }

                            @Override
                            public void onSuccess(Component result) {
                                Log.info(getClass(), ">>>>>>>>>>>>>>>>>>>>>> projectTemplatesComponent");

                            }
                        });
                        if (runningListener != null) {
                            runningListener.onRunning();
                        }

                        showInfo(RESTART.equals(operationType) ? locale.machineRestarted(machineName)
                                                               : locale.notificationMachineIsRunning(machineName), notification);
                        eventBus.fireEvent(MachineStateEvent.createMachineRunningEvent(machine));
                        break;
                    case DESTROYED:
                        unsubscribe(wsChannel, this);
                        showInfo(locale.notificationMachineDestroyed(machineName), notification);
                        eventBus.fireEvent(MachineStateEvent.createMachineDestroyedEvent(machine));
                        break;
                    case ERROR:
                        unsubscribe(wsChannel, this);
                        showError(result.getError(), notification);
                        break;
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                unsubscribe(wsChannel, this);
                showError(exception.getMessage(), notification);
            }
        };

        switch (operationType) {
            case START:
                notification.setMessage(locale.notificationCreatingMachine(machineName));
                break;
            case RESTART:
                notification.setMessage(locale.notificationMachineRestarting(machineName));
                break;
            case DESTROY:
                notification.setMessage(locale.notificationDestroyingMachine(machineName));
                break;
        }

        notification.setStatus(PROGRESS);
        notificationManager.showNotification(notification);

        subscribe(wsChannel, messageHandler);
    }

    private void showInfo(@Nonnull String message, @Nonnull Notification notification) {
        notification.setMessage(message);
        notification.setStatus(FINISHED);
        notification.setType(INFO);
    }

    private void showError(@Nonnull String message, @Nonnull Notification notification) {
        notification.setMessage(message);
        notification.setStatus(FINISHED);
        notification.setType(ERROR);
    }

    private void subscribe(@Nonnull String wsChannel, @Nonnull MessageHandler handler) {
        try {
            messageBus.subscribe(wsChannel, handler);
        } catch (WebSocketException e) {
            Log.error(getClass(), e);
        }
    }

    private void unsubscribe(@Nonnull String wsChannel, @Nonnull MessageHandler handler) {
        try {
            messageBus.unsubscribe(wsChannel, handler);
        } catch (WebSocketException e) {
            Log.error(getClass(), e);
        }
    }

    /** Listener's method will be invoked when machine is running. */
    interface RunningListener {
        void onRunning();
    }
}
