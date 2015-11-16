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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.security.oauth.OAuthStatus;

import javax.inject.Inject;

/**
 * @author Sergii Leschenko
 */
@Singleton
public class ConnectAccountAction extends Action {
    private final OpenshiftAuthenticator        openshiftAuthenticator;
    private final OpenshiftAuthorizationHandler openshiftAuthorizationHandler;
    private final OpenshiftLocalizationConstant locale;
    private final NotificationManager           notificationManager;

    @Inject
    public ConnectAccountAction(OpenshiftAuthenticator openshiftAuthenticator,
                                OpenshiftAuthorizationHandler openshiftAuthorizationHandler,
                                OpenshiftLocalizationConstant locale,
                                NotificationManager notificationManager) {
        super(locale.connectAccountTitle());
        this.openshiftAuthenticator = openshiftAuthenticator;
        this.openshiftAuthorizationHandler = openshiftAuthorizationHandler;
        this.locale = locale;
        this.notificationManager = notificationManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        openshiftAuthenticator.authorize(new AsyncCallback<OAuthStatus>() {
            @Override
            public void onSuccess(OAuthStatus result) {
                if (result == OAuthStatus.NOT_PERFORMED) {
                    return;
                }
                openshiftAuthorizationHandler.registerLogin();
                notificationManager.showInfo(locale.loginSuccessful());
            }

            @Override
            public void onFailure(Throwable caught) {
                notificationManager.showError(locale.loginFailed());
            }
        });
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(!openshiftAuthorizationHandler.isLoggedIn());
    }
}
