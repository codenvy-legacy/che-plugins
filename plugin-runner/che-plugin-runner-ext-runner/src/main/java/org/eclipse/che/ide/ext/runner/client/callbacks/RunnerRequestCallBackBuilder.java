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
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.websocket.rest.RequestCallback;
import org.eclipse.che.ide.websocket.rest.Unmarshallable;
import com.google.inject.Inject;

import javax.validation.constraints.NotNull;

/**
 * The builder that provides an ability to create an instance of {@link RequestCallback}. It has to simplify work flow of creating
 * callback.
 *
 * @param <T>
 *         type of element that has to be returned from server
 * @author Andrey Plotnikov
 */
public class RunnerRequestCallBackBuilder<T> {

    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final NotificationManager    notificationManager;

    private SuccessCallback<T> successCallback;
    private Unmarshallable<T>  unmarshaller;
    private Class<T>           clazz;
    private FailureCallback    failureCallback;

    @Inject
    public RunnerRequestCallBackBuilder(NotificationManager notificationManager, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
    }

    /**
     * Add success callback to configuration of callback that needs to be created.
     *
     * @param successCallback
     *         callback that has to be added
     * @return an instance of builder with changed configuration
     */
    @NotNull
    public RunnerRequestCallBackBuilder<T> success(@NotNull SuccessCallback<T> successCallback) {
        this.successCallback = successCallback;
        return this;
    }

    /**
     * Add unmarshaller to configuration of callback that needs to be created.
     *
     * @param unmarshaller
     *         unmarshaller that has to be added
     * @return an instance of builder with changed configuration
     */
    @NotNull
    public RunnerRequestCallBackBuilder<T> unmarshaller(@NotNull Unmarshallable<T> unmarshaller) {
        this.unmarshaller = unmarshaller;
        return this;
    }

    /**
     * Add clazz of unmarshaller that has to be created automatically and add to configuration of callback that needs to be created.
     *
     * @param clazz
     *         class of unmarshaller
     * @return an instance of builder with changed configuration
     */
    @NotNull
    public RunnerRequestCallBackBuilder<T> unmarshaller(@NotNull Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    /**
     * Add failure callback to configuration of callback that needs to be created.
     *
     * @param failureCallback
     *         callback that has to be added
     * @return an instance of builder with changed configuration
     */
    @NotNull
    public RunnerRequestCallBackBuilder<T> failure(@NotNull FailureCallback failureCallback) {
        this.failureCallback = failureCallback;
        return this;
    }

    /** @return an instance of {link RequestCallback} with a given configuration */
    @NotNull
    public RequestCallback<T> build() {
        if (successCallback == null) {
            throw new IllegalStateException("You forgot to initialize success callback parameter. Please, fix it and try again.");
        }

        if (unmarshaller == null && clazz != null) {
            unmarshaller = dtoUnmarshallerFactory.newWSUnmarshaller(clazz);
        }

        return new RunnerRequestCallback<>(notificationManager, unmarshaller, successCallback, failureCallback);
    }

}