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
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.eclipse.che.ide.ext.runner.client.TestUtil.getFieldValueByIndex;
import static org.eclipse.che.ide.ext.runner.client.TestUtil.getFieldValueByName;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class AsyncCallbackBuilderTest {

    private static final String UNMARSHALLER_FIELD_NAME = "unmarshaller";

    @Mock
    private NotificationManager        notificationManager;
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    @Mock
    private SuccessCallback<Object>    successCallback;
    @Mock
    private Unmarshallable<Object>     unmarshaller;
    @Mock
    private FailureCallback            failureCallback;

    @InjectMocks
    private AsyncCallbackBuilder<Object> callbackBuilder;

    @Test
    public void callBackShouldBeBuilt() throws Exception {
        AsyncRequestCallback<Object> asyncCallback = callbackBuilder
                .unmarshaller(unmarshaller)
                .success(successCallback)
                .failure(failureCallback).build();

        NotificationManager notificationManagerValue = (NotificationManager)getFieldValueByIndex(asyncCallback, 0);
        RunnerLocalizationConstant localeValue = (RunnerLocalizationConstant)getFieldValueByIndex(asyncCallback, 1);

        //noinspection unchecked
        SuccessCallback<Object> successCallbackValue = (SuccessCallback<Object>)getFieldValueByIndex(asyncCallback, 2);
        FailureCallback failureCallbackValue = (FailureCallback)getFieldValueByIndex(asyncCallback, 3);
        //noinspection unchecked
        Unmarshallable<Object> unmarshallerValue = (Unmarshallable<Object>)getFieldValueByName(asyncCallback, UNMARSHALLER_FIELD_NAME);

        assertThat(notificationManagerValue, equalTo(notificationManager));
        assertThat(localeValue, equalTo(locale));
        assertThat(successCallbackValue, equalTo(successCallback));
        assertThat(failureCallbackValue, equalTo(failureCallback));
        assertThat(unmarshallerValue, equalTo(unmarshaller));
    }

    @Test(expected = IllegalStateException.class)
    public void illegalStateExceptionShouldBeThrownWhenSuccessCallBackIsNull() throws Exception {
        //noinspection ConstantConditions
        callbackBuilder.unmarshaller(unmarshaller).unmarshaller(Object.class).success(null).failure(failureCallback).build();
    }

    @Test
    public void unmarshallerShouldBeCreatedWhenItIsNull() throws Exception {
        when(dtoUnmarshallerFactory.newUnmarshaller(Object.class)).thenReturn(unmarshaller);

        AsyncRequestCallback<Object> asyncCallback = callbackBuilder
                .unmarshaller(Object.class)
                .success(successCallback)
                .failure(failureCallback).build();

        //noinspection unchecked
        Unmarshallable<Object> unmarshallerValue = (Unmarshallable)getFieldValueByName(asyncCallback, UNMARSHALLER_FIELD_NAME);

        assertThat(unmarshallerValue, equalTo(unmarshaller));
    }

}