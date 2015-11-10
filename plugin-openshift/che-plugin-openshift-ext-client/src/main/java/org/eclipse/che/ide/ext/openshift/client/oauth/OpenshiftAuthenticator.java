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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.rest.RestContext;
import org.eclipse.che.ide.ui.dialogs.CancelCallback;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.security.oauth.JsOAuthWindow;
import org.eclipse.che.security.oauth.OAuthCallback;
import org.eclipse.che.security.oauth.OAuthStatus;

import javax.validation.constraints.NotNull;

/**
 * @author Sergii Leschenko
 */
public class OpenshiftAuthenticator implements OAuthCallback {
    private final OpenshiftLocalizationConstant locale;
    private final String                        baseUrl;
    private final AppContext                    appContext;
    private final DialogFactory                 dialogFactory;
    private       AsyncCallback<OAuthStatus>    callback;

    @Inject
    public OpenshiftAuthenticator(OpenshiftLocalizationConstant locale,
                                  @RestContext String baseUrl,
                                  AppContext appContext,
                                  DialogFactory dialogFactory) {
        this.locale = locale;
        this.baseUrl = baseUrl;
        this.appContext = appContext;
        this.dialogFactory = dialogFactory;
    }

    public void authorize(@NotNull final AsyncCallback<OAuthStatus> callback) {
        this.callback = callback;
        dialogFactory.createConfirmDialog(locale.authorizationRequestTitle(), locale.authorizationRequestMessage(), new ConfirmCallback() {
            @Override
            public void accepted() {
                JsOAuthWindow authWindow = new JsOAuthWindow(getAuthUrl(), "error.url", 500, 980, OpenshiftAuthenticator.this);
                authWindow.loginWithOAuth();
            }
        }, new CancelCallback() {
            @Override
            public void cancelled() {
                callback.onSuccess(OAuthStatus.NOT_PERFORMED);
            }
        }).show();
    }

    @Override
    public void onAuthenticated(OAuthStatus authStatus) {
        callback.onSuccess(authStatus);
    }

    private String getAuthUrl() {
        return baseUrl + "/oauth/authenticate?oauth_provider=openshift&userId=" + appContext.getCurrentUser().getProfile().getId()
               + "&redirect_after_login="//TODO Fix redirect after login
               + Window.Location.getProtocol() + "//"
               + Window.Location.getHost() + "/dashboard/";
    }
}
