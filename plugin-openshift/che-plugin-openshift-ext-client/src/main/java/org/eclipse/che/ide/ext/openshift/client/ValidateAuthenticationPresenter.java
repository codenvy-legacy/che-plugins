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
package org.eclipse.che.ide.ext.openshift.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.openshift.client.oauth.OpenshiftAuthenticator;
import org.eclipse.che.ide.ext.openshift.client.oauth.OpenshiftAuthorizationHandler;
import org.eclipse.che.security.oauth.OAuthStatus;

/**
 * @author Ann Shumilova
 */
public abstract class ValidateAuthenticationPresenter {

    private final OpenshiftAuthenticator openshiftAuthenticator;
    private final OpenshiftAuthorizationHandler openshiftAuthorizationHandler;
    private final OpenshiftLocalizationConstant locale;
    private final NotificationManager           notificationManager;

    protected ValidateAuthenticationPresenter(OpenshiftAuthenticator openshiftAuthenticator,
                                              OpenshiftAuthorizationHandler openshiftAuthorizationHandler,
                                              OpenshiftLocalizationConstant locale,
                                              NotificationManager notificationManager) {
        this.openshiftAuthenticator = openshiftAuthenticator;
        this.openshiftAuthorizationHandler = openshiftAuthorizationHandler;
        this.locale = locale;
        this.notificationManager = notificationManager;
    }

    public void show() {
        if (openshiftAuthorizationHandler.isLoggedIn()) {
            onSuccessAuthentication();
        } else {
            openshiftAuthenticator.authorize(new AsyncCallback<OAuthStatus>() {
                @Override
                public void onSuccess(OAuthStatus result) {
                    if (result == OAuthStatus.NOT_PERFORMED) {
                        return;
                    }

                    openshiftAuthorizationHandler.registerLogin();
                    notificationManager.showInfo(locale.loginSuccessful());
                    onSuccessAuthentication();
                }

                @Override
                public void onFailure(Throwable caught) {
                    notificationManager.showError(locale.loginFailed());
                }
            });
        }
    }

    protected abstract void onSuccessAuthentication();
}
