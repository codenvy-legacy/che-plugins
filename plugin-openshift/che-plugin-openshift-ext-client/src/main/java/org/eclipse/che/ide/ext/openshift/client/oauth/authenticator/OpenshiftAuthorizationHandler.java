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
package org.eclipse.che.ide.ext.openshift.client.oauth.authenticator;

import org.eclipse.che.api.auth.client.OAuthServiceClient;
import org.eclipse.che.api.auth.shared.dto.OAuthToken;
import org.eclipse.che.ide.rest.AsyncRequestCallback;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Sergii Leschenko
 */
@Singleton
public class OpenshiftAuthorizationHandler {
    public boolean loggedIn = false;

    @Inject
    public OpenshiftAuthorizationHandler(OAuthServiceClient authServiceClient) {
        authServiceClient.getToken("openshift", new AsyncRequestCallback<OAuthToken>() {
            @Override
            protected void onSuccess(OAuthToken result) {
                loggedIn = true;
            }

            @Override
            protected void onFailure(Throwable exception) {
                loggedIn = false;
            }
        });
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void registerLogin() {
        loggedIn = true;
    }

    public void registerLogout() {
        loggedIn = false;
    }
}
