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
package org.eclipse.che.ide.extension.machine.client.outputspanel.console;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.extension.machine.client.OutputMessageUnmarshaller;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;

/**
 * Console for command output.
 *
 * @author Artem Zatsarynnyy
 */
public class CommandOutputConsole implements OutputConsole, OutputConsoleView.ActionDelegate {

    private final OutputConsoleView   view;
    private final MessageBus          messageBus;
    private final NotificationManager notificationManager;
    private final CommandConfiguration commandConfiguration;

    @Inject
    public CommandOutputConsole(OutputConsoleView view,
                                MessageBus messageBus,
                                NotificationManager notificationManager,
                                @Assisted CommandConfiguration commandConfiguration) {
        this.view = view;
        this.messageBus = messageBus;
        this.notificationManager = notificationManager;
        this.commandConfiguration = commandConfiguration;

        view.setDelegate(this);
        view.printCommandLine(commandConfiguration.toCommandLine());
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public String getTitle() {
        return commandConfiguration.getName();
    }

    @Override
    public void attachToOutput(String outputChannel) {
        // TODO: unsubscribe from previous channel

        try {
            messageBus.subscribe(outputChannel, new SubscriptionHandler<String>(new OutputMessageUnmarshaller()) {
                @Override
                protected void onMessageReceived(String result) {
                    view.print(result, result.endsWith("\r"));
                    view.scrollBottom();
                }

                @Override
                protected void onErrorReceived(Throwable exception) {
                    notificationManager.showError(exception.getMessage());
                }
            });
        } catch (WebSocketException e) {
            notificationManager.showError(e.getMessage());
        }
    }
}
