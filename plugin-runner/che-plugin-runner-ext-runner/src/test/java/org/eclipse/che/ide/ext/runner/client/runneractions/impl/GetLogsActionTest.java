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
package org.eclipse.che.ide.ext.runner.client.runneractions.impl;

import com.google.inject.Provider;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.api.runner.gwt.client.RunnerServiceClient;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import org.eclipse.che.ide.ext.runner.client.callbacks.FailureCallback;
import org.eclipse.che.ide.ext.runner.client.callbacks.SuccessCallback;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerPresenter;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerView;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.tabs.console.container.ConsoleContainer;
import org.eclipse.che.ide.ext.runner.client.util.RunnerUtil;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrey Plotnikov
 */
@RunWith(MockitoJUnitRunner.class)
public class GetLogsActionTest {

    private static final String SOME_TEXT = "some text";

    @Captor
    private ArgumentCaptor<SuccessCallback<String>> successCallbackCaptor;
    @Captor
    private ArgumentCaptor<FailureCallback>         failureCallbackCaptor;

    @Mock
    private AsyncCallbackBuilder<String> callbackBuilder;
    @Mock
    private AsyncRequestCallback<String> callback;
    @Mock
    private CurrentProject               project;
    @Mock
    private Runner                       runner;
    @Mock
    private RunnerManagerView            view;

    // constructor parameters
    @Mock
    private RunnerServiceClient                    service;
    @Mock
    private AppContext                             appContext;
    @Mock
    private Provider<AsyncCallbackBuilder<String>> callbackBuilderProvider;
    @Mock
    private RunnerLocalizationConstant             constant;
    @Mock
    private RunnerUtil                             runnerUtil;
    @Mock
    private RunnerManagerPresenter                 runnerManagerPresenter;
    @Mock
    private ConsoleContainer                       consoleContainer;
    @Mock
    private AnalyticsEventLogger                   eventLogger;

    private GetLogsAction action;

    @Before
    public void setUp() throws Exception {
        when(callbackBuilderProvider.get()).thenReturn(callbackBuilder);

        when(callbackBuilder.unmarshaller(Matchers.<Unmarshallable<String>>anyObject())).thenReturn(callbackBuilder);
        when(callbackBuilder.success(Matchers.<SuccessCallback<String>>anyObject())).thenReturn(callbackBuilder);
        when(callbackBuilder.failure(any(FailureCallback.class))).thenReturn(callbackBuilder);

        when(runnerManagerPresenter.getView()).thenReturn(view);
        action = new GetLogsAction(service,
                                   appContext,
                                   callbackBuilderProvider,
                                   constant,
                                   runnerUtil,
                                   consoleContainer,
                                   runnerManagerPresenter,
                                   eventLogger);
    }

    @Test
    public void nothingShouldHappenWhenACurrentProjectIsEmpty() throws Exception {
        action.perform(runner);

        verify(eventLogger).log(action);
        verify(runnerUtil, never()).showError(any(Runner.class), anyString(), any(Throwable.class));
        verify(runnerManagerPresenter, never()).setActive();
        verify(service, never()).getLogs(any(Link.class), Matchers.<AsyncRequestCallback<String>>anyObject());
    }

    @Test
    public void errorShouldBeShownWhenLogUrlIsEmpty() throws Exception {
        when(constant.applicationLogsFailed()).thenReturn(SOME_TEXT);
        when(appContext.getCurrentProject()).thenReturn(project);

        action.perform(runner);

        verify(eventLogger).log(action);
        verify(runnerManagerPresenter, never()).setActive();
        verify(service, never()).getLogs(any(Link.class), Matchers.<AsyncRequestCallback<String>>anyObject());
    }

    @Test
    public void requestShouldBeExecutedWhenAllIsOK() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(project);

        Link viewLogsLink = mock(Link.class);
        when(runner.getLogUrl()).thenReturn(viewLogsLink);

        action.perform(runner);

        verify(eventLogger).log(action);
        verify(runnerUtil, never()).showError(any(Runner.class), anyString(), any(Throwable.class));

        verify(runnerManagerPresenter).setActive();
        verify(service).getLogs(eq(viewLogsLink), Matchers.<AsyncRequestCallback<String>>anyObject());
    }

    @Test
    public void successCallbackShouldBeExecutedWhenRequestIsSuccessful() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(project);

        Link viewLogsLink = mock(Link.class);
        when(runner.getLogUrl()).thenReturn(viewLogsLink);

        action.perform(runner);

        verify(eventLogger).log(action);
        verify(callbackBuilder).success(successCallbackCaptor.capture());

        SuccessCallback<String> successCallback = successCallbackCaptor.getValue();
        successCallback.onSuccess(SOME_TEXT);

        verify(consoleContainer).print(runner, SOME_TEXT);
    }

    @Test
    public void failedCallbackShouldBeExecutedWhenRequestIsFailed() throws Exception {
        Throwable throwable = mock(Throwable.class);

        when(appContext.getCurrentProject()).thenReturn(project);

        Link viewLogsLink = mock(Link.class);
        when(runner.getLogUrl()).thenReturn(viewLogsLink);

        when(constant.applicationLogsFailed()).thenReturn(SOME_TEXT);

        action.perform(runner);

        verify(eventLogger).log(action);
        verify(callbackBuilder).failure(failureCallbackCaptor.capture());

        FailureCallback failureCallback = failureCallbackCaptor.getValue();
        failureCallback.onFailure(throwable);

        verify(runnerUtil).showError(runner, SOME_TEXT, throwable);
    }

}