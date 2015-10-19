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
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.rest.AsyncRequestCallback;

import javax.inject.Inject;

/**
 * @author Sergii Leschenko
 */
public class DisconnectAccountAction extends Action {
    private final OAuthServiceClient            oAuthServiceClient;
    private final OpenshiftAuthorizationHandler openshiftAuthorizationHandler;
    private final OpenshiftLocalizationConstant locale;
    private final NotificationManager notificationManager;

    @Inject
    public DisconnectAccountAction(OAuthServiceClient oAuthServiceClient,
                                   OpenshiftAuthorizationHandler openshiftAuthorizationHandler,
                                   OpenshiftLocalizationConstant locale,
                                   NotificationManager notificationManager) {
        super(locale.disconnectAccountTitle());
        this.oAuthServiceClient = oAuthServiceClient;
        this.openshiftAuthorizationHandler = openshiftAuthorizationHandler;
        this.locale = locale;
        this.notificationManager = notificationManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        oAuthServiceClient.invalidateToken("openshift", new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                openshiftAuthorizationHandler.registerLogout();
                notificationManager.showInfo(locale.logoutSuccessful());
            }

            @Override
            protected void onFailure(Throwable exception) {
                notificationManager.showError(locale.logoutFailed());
            }
        });
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(openshiftAuthorizationHandler.isLoggedIn());
    }
}
