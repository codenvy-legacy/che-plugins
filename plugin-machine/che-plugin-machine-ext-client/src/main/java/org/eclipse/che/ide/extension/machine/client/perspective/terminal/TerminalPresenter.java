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
package org.eclipse.che.ide.extension.machine.client.perspective.terminal;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.content.TabPresenter;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.WebSocket;
import org.eclipse.che.ide.websocket.events.ConnectionErrorHandler;
import org.eclipse.che.ide.websocket.events.ConnectionOpenedHandler;
import org.eclipse.che.ide.websocket.events.MessageReceivedEvent;
import org.eclipse.che.ide.websocket.events.MessageReceivedHandler;

import javax.annotation.Nonnull;

/**
 * The class defines methods which contains business logic to control machine's terminal.
 *
 * @author Dmitry Shnurenko
 */
public class TerminalPresenter implements TabPresenter {

    //event which is performed when user input data into terminal
    private static final String DATA_EVENT_NAME = "data";

    private final TerminalView                view;
    private final NotificationManager         notificationManager;
    private final MachineLocalizationConstant locale;
    private final Machine                     machine;

    private Promise<Boolean> promise;
    private WebSocket        socket;
    private boolean          isFirstLoading;

    @Inject
    public TerminalPresenter(TerminalView view,
                             NotificationManager notificationManager,
                             MachineLocalizationConstant locale,
                             @Assisted Machine machine) {
        this.view = view;
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.machine = machine;

        isFirstLoading = true;

        promise = AsyncPromiseHelper.createFromAsyncRequest(new AsyncPromiseHelper.RequestCall<Boolean>() {
            @Override
            public void makeCall(final AsyncCallback<Boolean> callback) {
                ScriptInjector.fromUrl(GWT.getModuleBaseURL() + "term/term.js")
                              .setWindow(ScriptInjector.TOP_WINDOW)
                              .setCallback(new Callback<Void, Exception>() {
                                  @Override
                                  public void onFailure(Exception reason) {
                                      callback.onFailure(reason);
                                  }

                                  @Override
                                  public void onSuccess(Void result) {
                                      callback.onSuccess(true);
                                  }
                              }).inject();
            }
        });
    }

    /**
     * Connects to special WebSocket which allows get information from terminal on server side. The terminal is initialized only
     * when the method is called the first time.
     */
    public void connect() {
        if (isFirstLoading) {
            promise.then(new Operation<Boolean>() {
                @Override
                public void apply(Boolean arg) throws OperationException {
                    openWebSocket(machine.getWSTerminalUrl());
                }
            }).catchError(new Operation<PromiseError>() {
                @Override
                public void apply(PromiseError arg) throws OperationException {
                    notificationManager.showError(locale.terminalCanNotLoadScript());

                    if (arg != null) {
                        Log.error(TerminalViewImpl.class, arg);
                    }
                }
            });

            isFirstLoading = false;
        }
    }

    private void openWebSocket(@Nonnull String wsUrl) {
        socket = WebSocket.create(wsUrl);
        socket.setOnOpenHandler(new ConnectionOpenedHandler() {
            @Override
            public void onOpen() {
                final TerminalJso terminal = TerminalJso.create(TerminalOptionsJso.createDefault());

                view.openTerminal(terminal);

                terminal.on(DATA_EVENT_NAME, new Operation<String>() {
                    @Override
                    public void apply(String arg) throws OperationException {
                        socket.send(arg);
                    }
                });
                socket.setOnMessageHandler(new MessageReceivedHandler() {
                    @Override
                    public void onMessageReceived(MessageReceivedEvent event) {
                        terminal.write(event.getMessage());
                    }
                });
            }
        });

        socket.setOnErrorHandler(new ConnectionErrorHandler() {
            @Override
            public void onError() {
                notificationManager.showError(locale.terminalErrorConnection());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public IsWidget getView() {
        return view;
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }
}
