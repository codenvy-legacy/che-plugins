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

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.TestUtil;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerPresenter;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.tabs.console.container.ConsoleContainer;
import org.eclipse.che.ide.ext.runner.client.util.TimerFactory;
import org.eclipse.che.ide.ext.runner.client.util.WebSocketUtil;
import org.eclipse.che.ide.ext.runner.client.constants.TimeInterval;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.user.client.Timer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.eclipse.che.ide.api.notification.Notification.Status.FINISHED;
import static org.eclipse.che.ide.api.notification.Notification.Type.WARNING;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.isNull;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class CheckHealthStatusActionTest {
    private static final String APPLICATION_URL      = "http://codenvy.runner.our.app";
    private static final String PROJECT_NAME         = "ProjectName";
    private static final String NOTIFICATION_MESSAGE = "some notification";
    private static final String APP_HEALTH_CHANNEL   = "runner:app_health:";
    private static final long   PROCESS_ID           = 1234567L;
    private static final String WEB_SOCKET_CHANNEL   = APP_HEALTH_CHANNEL + PROCESS_ID;
    private static final String RESULT               = "some message";

    //variables for constructor
    @Mock
    private AppContext                 appContext;
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private RunnerManagerPresenter     presenter;
    @Mock
    private WebSocketUtil              webSocketUtil;
    @Mock
    private ConsoleContainer           consoleContainer;
    @Mock
    private TimerFactory               timerFactory;
    @Mock
    private Notification               notification;

    @Mock
    private CurrentProject                              project;
    @Mock
    private Runner                                      runner;
    @Mock
    private ProjectDescriptor                           projectDescriptor;
    @Mock
    private Timer                                       changeAppAliveTimer;
    //captors
    @Captor
    private ArgumentCaptor<TimerFactory.TimerCallBack>  changeAppAliveTimerCaptor;
    @Captor
    private ArgumentCaptor<SubscriptionHandler<String>> runnerHealthHandlerCaptor;

    private CheckHealthStatusAction checkHealthStatusAction;

    @Before
    public void setUp() {
        checkHealthStatusAction = new CheckHealthStatusAction(appContext,
                                                              locale,
                                                              presenter,
                                                              webSocketUtil,
                                                              consoleContainer,
                                                              timerFactory,
                                                              notification);
        when(appContext.getCurrentProject()).thenReturn(project);
        when(runner.getApplicationURL()).thenReturn(APPLICATION_URL);

        when(projectDescriptor.getName()).thenReturn(PROJECT_NAME);
        when(locale.applicationMaybeStarted(PROJECT_NAME)).thenReturn(NOTIFICATION_MESSAGE);

        when(project.getProjectDescription()).thenReturn(projectDescriptor);
        when(projectDescriptor.getName()).thenReturn(PROJECT_NAME);
        when(timerFactory.newInstance(any(TimerFactory.TimerCallBack.class))).thenReturn(changeAppAliveTimer);
        when(runner.getProcessId()).thenReturn(PROCESS_ID);
    }

    @Test
    public void shouldPerformWhenApplicationURLIsNull() {
        when(runner.getApplicationURL()).thenReturn(null);

        checkHealthStatusAction.perform(runner);

        verify(appContext).getCurrentProject();
        verifyNoMoreInteractions(appContext,
                                 locale,
                                 presenter,
                                 webSocketUtil,
                                 consoleContainer,
                                 timerFactory);
    }

    @Test
    public void shouldCheckPerformActionBeforeSendIntoServer() {
        when(timerFactory.newInstance(any(TimerFactory.TimerCallBack.class))).thenReturn(changeAppAliveTimer);

        checkHealthStatusAction.perform(runner);

        verify(appContext).getCurrentProject();
        verify(runner).getApplicationURL();
    }

    @Test
    public void shouldPerformOperationBeforeSendDataToServer() throws Exception {
        checkHealthStatusAction.perform(runner);

        verify(appContext).getCurrentProject();
        verify(runner).getApplicationURL();

        verify(timerFactory).newInstance(changeAppAliveTimerCaptor.capture());
        TimerFactory.TimerCallBack timerCallBack = changeAppAliveTimerCaptor.getValue();
        timerCallBack.onRun();

        verify(presenter).update(runner);
        verify(notification).update(NOTIFICATION_MESSAGE, WARNING, FINISHED, null, true);
        verify(consoleContainer).printWarn(runner, NOTIFICATION_MESSAGE);
        verify(changeAppAliveTimer).schedule(TimeInterval.THIRTY_SEC.getValue());

        verify(runner).getProcessId();

        verify(webSocketUtil).subscribeHandler(eq(WEB_SOCKET_CHANNEL), runnerHealthHandlerCaptor.capture());
        SubscriptionHandler<String> runnerHealthHandler = runnerHealthHandlerCaptor.getValue();

        TestUtil.invokeMethodByName(runnerHealthHandler, "onMessageReceived", RESULT);

        verify(webSocketUtil).subscribeHandler(WEB_SOCKET_CHANNEL, runnerHealthHandler);
    }

    @Test
    public void shouldStopWhenTimerAndWebSocketChannelAndRunnerHealthHandlerNotNull() throws Exception {
        checkHealthStatusAction.perform(runner);

        verify(timerFactory).newInstance(changeAppAliveTimerCaptor.capture());
        TimerFactory.TimerCallBack timerCallBack = changeAppAliveTimerCaptor.getValue();
        timerCallBack.onRun();

        verify(webSocketUtil).subscribeHandler(eq(WEB_SOCKET_CHANNEL), runnerHealthHandlerCaptor.capture());
        SubscriptionHandler<String> runnerHealthHandler = runnerHealthHandlerCaptor.getValue();

        TestUtil.invokeMethodByName(runnerHealthHandler, "onMessageReceived", RESULT);

        checkHealthStatusAction.stop();

        verify(webSocketUtil).unSubscribeHandler(WEB_SOCKET_CHANNEL, runnerHealthHandler);
        verify(changeAppAliveTimer).cancel();
    }

    @Test
    public void shouldStopWhenTimerAndWebSocketChannelAndRunnerHealthHandlerAreNull() throws Exception {
        checkHealthStatusAction.stop();

        verify(webSocketUtil, never()).unSubscribeHandler(isNull(String.class), isNull(SubscriptionHandler.class));
        verify(changeAppAliveTimer, never()).cancel();
    }

}