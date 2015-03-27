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
import org.eclipse.che.ide.websocket.rest.Unmarshallable;

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
public class RunnerRequestCallbackTest {

    private final static String SOME_TEXT = "someText";

    @Mock
    private FailureCallback         failureCallback;
    @Mock
    private SuccessCallback<Object> successCallback;
    @Mock
    private NotificationManager     notificationManager;
    @Mock
    private Unmarshallable<Object>  unmarshaller;
    @Mock
    private Object                  result;
    @Mock
    private Throwable               throwable;

    @InjectMocks
    private RunnerRequestCallback<Object> requestCallback;

    @Test
    public void successCallBackShouldBeCalled() throws Exception {
        requestCallback.onSuccess(result);

        verify(successCallback).onSuccess(result);
    }

    @Test
    public void failureCallBackShouldBeDoneWhenFailureIsNotNull() throws Exception {
        requestCallback.onFailure(throwable);

        verify(failureCallback).onFailure(throwable);
        verify(notificationManager, never()).showError(anyString());
    }

    @Test
    public void failureCallBackShouldBeDoneWhenFailureIsNull() throws Exception {
        when(throwable.getMessage()).thenReturn(SOME_TEXT);
        RunnerRequestCallback<Object> requestCallback = new RunnerRequestCallback<>(notificationManager,
                                                                                    unmarshaller,
                                                                                    successCallback,
                                                                                    null);
        requestCallback.onFailure(throwable);

        verify(failureCallback, never()).onFailure(throwable);
        verify(notificationManager).showError(SOME_TEXT);
    }
}