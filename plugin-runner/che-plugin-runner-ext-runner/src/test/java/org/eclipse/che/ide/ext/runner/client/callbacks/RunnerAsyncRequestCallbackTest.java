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
package org.eclipse.che.ide.ext.runner.client.callbacks;

import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.commons.exception.ServerException;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.rest.Unmarshallable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class RunnerAsyncRequestCallbackTest {

    private final static String SOME_TEXT = "someText";

    @Mock
    private NotificationManager        notificationManager;
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private SuccessCallback<Object>    successCallback;
    @Mock
    private Unmarshallable<Object>     unmarshaller;
    @Mock
    private FailureCallback            failureCallback;
    @Mock
    private Object                     result;
    @Mock
    private ServerException            exception;

    @InjectMocks
    private RunnerAsyncRequestCallback<Object> asyncRequestCallback;

    @Test
    public void successCallBackShouldBeCalled() throws Exception {
        asyncRequestCallback.onSuccess(result);

        verify(successCallback).onSuccess(result);
    }

    @Test
    public void failureCallBackShouldBeCalledWhenItIsNotNull() throws Exception {
        asyncRequestCallback.onFailure(exception);

        verify(failureCallback).onFailure(exception);
        verify(notificationManager, never()).showError(anyString());
    }

    @Test
    public void errorMessageShouldBeShownWhenExceptionMessageIsEmpty() throws Exception {
        RunnerAsyncRequestCallback<Object> asyncRequestCallback = new RunnerAsyncRequestCallback<>(notificationManager,
                                                                                                   locale,
                                                                                                   unmarshaller,
                                                                                                   successCallback,
                                                                                                   null);
        when(exception.getMessage()).thenReturn("");
        when(locale.unknownErrorMessage()).thenReturn(SOME_TEXT);

        asyncRequestCallback.onFailure(exception);

        verify(notificationManager).showError(SOME_TEXT);
        verify(locale).unknownErrorMessage();
        verify(failureCallback, never()).onFailure(exception);
    }

    @Test
    public void errorMessageShouldBeShownWhenExceptionMessageIsNotEmpty() throws Exception {
        RunnerAsyncRequestCallback<Object> asyncRequestCallback = new RunnerAsyncRequestCallback<>(notificationManager,
                                                                                                   locale,
                                                                                                   unmarshaller,
                                                                                                   successCallback,
                                                                                                   null);
        when(exception.getMessage()).thenReturn(SOME_TEXT);

        asyncRequestCallback.onFailure(exception);

        verify(notificationManager).showError(SOME_TEXT);
        verify(locale, never()).unknownErrorMessage();
        verify(failureCallback, never()).onFailure(exception);
    }
}