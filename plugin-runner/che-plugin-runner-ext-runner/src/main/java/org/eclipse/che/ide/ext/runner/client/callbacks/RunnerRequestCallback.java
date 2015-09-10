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
import org.eclipse.che.ide.websocket.rest.RequestCallback;
import org.eclipse.che.ide.websocket.rest.Unmarshallable;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * Class to receive a response from a remote procedure call.
 *
 * @author Evgen Vidolob
 * @author Dmitry Shnurenko
 */
public class RunnerRequestCallback<T> extends RequestCallback<T> {

    private final FailureCallback     failureCallback;
    private final SuccessCallback<T>  successCallback;
    private final NotificationManager notificationManager;

    public RunnerRequestCallback(@NotNull NotificationManager notificationManager,
                                 @Nullable Unmarshallable<T> unmarshallable,
                                 @NotNull SuccessCallback<T> successCallback,
                                 @Nullable FailureCallback failureCallback) {
        super(unmarshallable);
        this.notificationManager = notificationManager;
        this.successCallback = successCallback;
        this.failureCallback = failureCallback;
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccess(T result) {
        successCallback.onSuccess(result);
    }

    /** {@inheritDoc} */
    @Override
    public void onFailure(Throwable exception) {
        if (failureCallback != null) {
            failureCallback.onFailure(exception);
            return;
        }

        notificationManager.showError(exception.getMessage());
    }
}