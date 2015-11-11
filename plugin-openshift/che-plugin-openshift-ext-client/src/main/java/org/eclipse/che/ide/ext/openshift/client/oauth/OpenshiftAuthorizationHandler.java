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
package org.eclipse.che.ide.ext.openshift.client.oauth;

import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.auth.client.OAuthServiceClient;
import org.eclipse.che.api.auth.shared.dto.OAuthToken;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Sergii Leschenko
 */
@Singleton
public class OpenshiftAuthorizationHandler {
    private final OAuthServiceClient     authServiceClient;
    private final DtoUnmarshallerFactory unmarshallerFactory;
    private final EventBus               eventBus;

    private String token;

    @Inject
    public OpenshiftAuthorizationHandler(OAuthServiceClient authServiceClient,
                                         DtoUnmarshallerFactory unmarshallerFactory,
                                         EventBus eventBus) {
        this.authServiceClient = authServiceClient;
        this.unmarshallerFactory = unmarshallerFactory;
        this.eventBus = eventBus;
        refreshToken();
    }

    public boolean isLoggedIn() {
        return token != null;
    }

    public void registerLogin() {
        refreshToken();
    }

    public void registerLogout() {
        token = null;
        eventBus.fireEvent(new OAuthTokenChangedEvent(null));
    }

    @Nullable
    public String getToken() {
        return token;
    }

    private void refreshToken() {
        authServiceClient.getToken("openshift",
                                   new AsyncRequestCallback<OAuthToken>(unmarshallerFactory.newUnmarshaller(OAuthToken.class)) {
                                       @Override
                                       protected void onSuccess(OAuthToken result) {
                                           token = result.getToken();
                                           eventBus.fireEvent(new OAuthTokenChangedEvent(token));
                                       }

                                       @Override
                                       protected void onFailure(Throwable exception) {
                                           token = null;
                                           eventBus.fireEvent(new OAuthTokenChangedEvent(null));
                                       }
                                   });
    }
}
