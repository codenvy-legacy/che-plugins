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

import elemental.client.Browser;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.WebSocket;
import org.eclipse.che.ide.websocket.events.ConnectionErrorHandler;
import org.eclipse.che.ide.websocket.events.ConnectionOpenedHandler;
import org.eclipse.che.ide.websocket.events.MessageReceivedEvent;
import org.eclipse.che.ide.websocket.events.MessageReceivedHandler;

import javax.annotation.Nonnull;

/**
 * The class contains methods to displaying terminal.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TerminalViewImpl extends Composite implements TerminalView {
    private static Promise<Boolean>    promise;
    private NotificationManager notificationManager;
    private WebSocket           socket;
    private TermJso             term;

    interface TerminalViewImplUiBinder extends UiBinder<Widget, TerminalViewImpl> {
    }

    private final static TerminalViewImplUiBinder UI_BINDER = GWT.create(TerminalViewImplUiBinder.class);

    @Inject
    public TerminalViewImpl(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
        initWidget(UI_BINDER.createAndBindUi(this));
        if(promise == null){
            promise = AsyncPromiseHelper.createFromAsyncRequest(new AsyncPromiseHelper.RequestCall<Boolean>() {
                @Override
                public void makeCall(final AsyncCallback<Boolean> callback) {
                    ScriptInjector.fromUrl(GWT.getModuleBaseURL() + "term/term.js").setWindow(ScriptInjector.TOP_WINDOW).setCallback(
                            new Callback<Void, Exception>() {
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
    }

    /** {@inheritDoc} */
    @Override
    public void updateTerminal(@Nonnull final Machine machine) {

        promise.then(new Operation<Boolean>() {
            @Override
            public void apply(Boolean arg) throws OperationException {
                openWebSocket(machine.getTerminalUrl());
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                notificationManager.showError("Can't load term.js script");
                if (arg != null) {
                    Log.error(TerminalViewImpl.class, arg);
                }
            }
        });

    }

    private void openWebSocket(String terminalUrl) {
        String url = createSocketUrl(terminalUrl);
        if (socket != null) {
            socket.close();
        }
        socket = WebSocket.create(url);
        socket.setOnOpenHandler(new ConnectionOpenedHandler() {
            @Override
            public void onOpen() {
                Scheduler.ScheduledCommand scheduledCommand = new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        term = TermJso.create(TermOptionsJso.createDefault());
                        term.open(Browser.getDocument().getElementById("bash"));
                        term.on("data", new Operation<String>() {
                            @Override
                            public void apply(String arg) throws OperationException {
                                socket.send(arg);
                            }
                        });
                        socket.setOnMessageHandler(new MessageReceivedHandler() {
                            @Override
                            public void onMessageReceived(MessageReceivedEvent event) {
                                term.write(event.getMessage());
                            }
                        });
                    }
                };
                Scheduler.get().scheduleDeferred(scheduledCommand);
            }
        });


        socket.setOnErrorHandler(new ConnectionErrorHandler() {
            @Override
            public void onError() {
                notificationManager.showError("Some error happened with terminal WebSocket connection.");
            }
        });
    }

    private String createSocketUrl(String terminalUrl) {
        //replace http with ws or wss scheme
        terminalUrl = terminalUrl.substring(terminalUrl.indexOf(':'), terminalUrl.length());
        boolean isSecureConnection = Window.Location.getProtocol().equals("https:");
        return (isSecureConnection ? "wss" : "ws") + terminalUrl + "/pty";

    }
}