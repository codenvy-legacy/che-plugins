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
package org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.common;

import org.eclipse.che.ide.ext.runner.client.tabs.console.container.ConsoleContainer;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.util.TimerFactory;
import org.eclipse.che.ide.ext.runner.client.constants.TimeInterval;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class listens for log messages from the server and process it. Logic of this class is slightly complicated since we can't guaranty
 * correct order of messages and delivery it from the server over WebSocket connection. So messages may be received in shuffled order and
 * some messages may be never received.
 *
 * @author Artem Zatsarynnyy
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public class LogMessagesHandler extends SubscriptionHandler<LogMessage> {

    private final Runner                   runner;
    private final ErrorHandler             errorHandler;
    private final Map<Integer, LogMessage> postponedMessages;
    private final Timer                    flushTimer;
    private final ConsoleContainer         consoleContainer;

    private int lastPrintedMessageNum;

    @Inject
    public LogMessagesHandler(LogMessageUnmarshaller unmarshaller,
                              ConsoleContainer consoleContainer,
                              TimerFactory timerFactory,
                              @NotNull @Assisted Runner runner,
                              @NotNull @Assisted ErrorHandler errorHandler) {
        super(unmarshaller);

        this.runner = runner;
        this.errorHandler = errorHandler;
        this.consoleContainer = consoleContainer;
        this.postponedMessages = new HashMap<>();

        this.flushTimer = timerFactory.newInstance(new TimerFactory.TimerCallBack() {
            @Override
            public void onRun() {
                printAllPostponedMessages();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    protected void onMessageReceived(LogMessage message) {
        int number = message.getNumber();

        if (number == lastPrintedMessageNum + 1) {
            flushTimer.cancel();
            printLine(message);
        } else if (number > lastPrintedMessageNum) {
            postponedMessages.put(number, message);
        }

        printNextPostponedMessages();

        flushTimer.schedule(TimeInterval.FIVE_SEC.getValue());
    }

    /** Print all messages from buffer for the moment and stop handling. */
    public void stop() {
        printAllPostponedMessages();
        flushTimer.cancel();
    }

    /** Print next postponed messages with contiguous line numbers. */
    private void printNextPostponedMessages() {
        LogMessage nextLogMessage = postponedMessages.remove(lastPrintedMessageNum + 1);

        while (nextLogMessage != null) {
            printLine(nextLogMessage);

            nextLogMessage = postponedMessages.remove(nextLogMessage.getNumber() + 1);
        }
    }

    /** Print all postponed messages in correct order. */
    private void printAllPostponedMessages() {
        for (int i = lastPrintedMessageNum + 1; !postponedMessages.isEmpty(); i++) {
            LogMessage nextLogMessage = postponedMessages.remove(i);

            if (nextLogMessage == null) {
                continue;
            }

            printLine(nextLogMessage);
        }
    }

    private void printLine(@NotNull LogMessage logMessage) {
        consoleContainer.print(runner, logMessage.getText());
        lastPrintedMessageNum = logMessage.getNumber();
    }

    /** {@inheritDoc} */
    @Override
    protected void onErrorReceived(Throwable throwable) {
        Log.error(LogMessagesHandler.class, throwable);
        errorHandler.onErrorHappened();
    }

    public interface ErrorHandler {
        /** Perform some actions in response on some error happens */
        void onErrorHappened();
    }

}