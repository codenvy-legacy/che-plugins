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
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.common.LogMessagesHandler;
import org.eclipse.che.ide.ext.runner.client.util.WebSocketUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class OutputActionTest {

    private static final long   PROCESS_ID    = 123456;
    private static final String SOCKET_CHANEL = OutputAction.OUTPUT_CHANNEL + PROCESS_ID;

    //constructor mocks
    @Mock
    private HandlerFactory handlerFactory;
    @Mock
    private WebSocketUtil  webSocketUtil;

    //additional mocks
    @Mock
    private Runner             runner;
    @Mock
    private LogMessagesHandler runnerOutputHandler;

    @InjectMocks
    private OutputAction action;

    @Before
    public void setUp() throws Exception {
        when(handlerFactory.createLogMessageHandler(runner, action)).thenReturn(runnerOutputHandler);
        when(runner.getProcessId()).thenReturn(PROCESS_ID);
    }

    @Test
    public void actionShouldBePerformed() throws Exception {
        action.perform(runner);

        verify(handlerFactory).createLogMessageHandler(runner, action);
        verify(runner).getProcessId();
        verify(webSocketUtil).subscribeHandler(SOCKET_CHANEL, runnerOutputHandler);
    }

    @Test
    public void actionShouldNotBeStoppedWhenOutputHandlerIsNull() throws Exception {
        when(handlerFactory.createLogMessageHandler(runner, action)).thenReturn(null);

        action.perform(runner);

        verify(webSocketUtil, never()).unSubscribeHandler(anyString(), any(LogMessagesHandler.class));
    }

    @Test
    public void actionShouldNotBeStoppedWhenWeTryStopItTwice() throws Exception {
        action.stop();
        reset(webSocketUtil);

        action.stop();

        verify(webSocketUtil, never()).unSubscribeHandler(anyString(), any(LogMessagesHandler.class));
    }

    @Test
    public void actionShouldBeStopped() throws Exception {
        action.perform(runner);

        action.stop();

        verify(webSocketUtil).unSubscribeHandler(SOCKET_CHANEL, runnerOutputHandler);
    }

    @Test
    public void actionShouldBeStoppedWhenErrorHappened() throws Exception {
        action.perform(runner);

        action.onErrorHappened();

        verify(webSocketUtil).unSubscribeHandler(SOCKET_CHANEL, runnerOutputHandler);
    }
}