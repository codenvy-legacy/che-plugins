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
package org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions;

import org.eclipse.che.ide.ext.runner.client.inject.factories.HandlerFactory;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.common.LogMessagesHandler;
import org.eclipse.che.ide.ext.runner.client.util.WebSocketUtil;
import com.google.inject.Inject;

import javax.validation.constraints.NotNull;

/**
 * The action for analyzing output messages.
 *
 * @author Andrey Plotnikov
 */
public class OutputAction extends AbstractRunnerAction implements LogMessagesHandler.ErrorHandler {
    /** WebSocket channel to get runner output. */
    public static final String OUTPUT_CHANNEL = "runner:output:";

    private final HandlerFactory handlerFactory;
    private final WebSocketUtil  webSocketUtil;

    private LogMessagesHandler runnerOutputHandler;
    private String             webSocketChannel;

    @Inject
    public OutputAction(HandlerFactory handlerFactory, WebSocketUtil webSocketUtil) {
        this.handlerFactory = handlerFactory;
        this.webSocketUtil = webSocketUtil;
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@NotNull Runner runner) {
        runnerOutputHandler = handlerFactory.createLogMessageHandler(runner, this);
        webSocketChannel = OUTPUT_CHANNEL + runner.getProcessId();

        webSocketUtil.subscribeHandler(webSocketChannel, runnerOutputHandler);
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        if (webSocketChannel == null || runnerOutputHandler == null) {
            // It is impossible to perform stop event twice.
            return;
        }

        webSocketUtil.unSubscribeHandler(webSocketChannel, runnerOutputHandler);

        super.stop();

        webSocketChannel = null;
        runnerOutputHandler = null;
    }

    /** {@inheritDoc} */
    @Override
    public void onErrorHappened() {
        stop();
    }

}