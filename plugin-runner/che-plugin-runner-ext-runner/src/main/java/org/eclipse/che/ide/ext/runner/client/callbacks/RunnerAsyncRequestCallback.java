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
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.Unmarshallable;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * Class to receive a response from a remote procedure call.
 *
 * @author Evgen Vidolob
 * @author Valeriy Svydenko
 * @author Dmitry Shnurenko
 * @author Andrey Plotnikov
 */
public class RunnerAsyncRequestCallback<T> extends AsyncRequestCallback<T> {

    private final NotificationManager        notificationManager;
    private final RunnerLocalizationConstant locale;
    private final SuccessCallback<T>         successCallback;
    private final FailureCallback            failureCallback;

    public RunnerAsyncRequestCallback(@NotNull NotificationManager notificationManager,
                                      @NotNull RunnerLocalizationConstant locale,
                                      @Nullable Unmarshallable<T> unmarshaller,
                                      @NotNull SuccessCallback<T> successCallback,
                                      @Nullable FailureCallback failureCallback) {
        super(unmarshaller);
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.successCallback = successCallback;
        this.failureCallback = failureCallback;
    }

    /** {@inheritDoc} */
    @Override
    protected void onSuccess(@NotNull T result) {
        successCallback.onSuccess(result);
    }

    /** {@inheritDoc} */
    @Override
    protected void onFailure(@NotNull Throwable exception) {
        if (failureCallback != null) {
            failureCallback.onFailure(exception);
            return;
        }

        String message = exception.getMessage();

        boolean isServerExceptionMessage = !message.isEmpty() && exception instanceof ServerException;

        notificationManager.showError(isServerExceptionMessage ? message : locale.unknownErrorMessage());
    }

}