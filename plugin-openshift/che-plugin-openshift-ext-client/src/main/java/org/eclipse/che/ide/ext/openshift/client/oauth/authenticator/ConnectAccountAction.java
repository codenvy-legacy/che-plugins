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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
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

    @Inject
    public ConnectAccountAction(OpenshiftAuthenticator openshiftAuthenticator,
                                OpenshiftAuthorizationHandler openshiftAuthorizationHandler,
                                OpenshiftLocalizationConstant locale) {
        super(locale.connectAccountTitle());
        this.openshiftAuthenticator = openshiftAuthenticator;
        this.openshiftAuthorizationHandler = openshiftAuthorizationHandler;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        openshiftAuthenticator.authorize(new AsyncCallback<OAuthStatus>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(OAuthStatus result) {
                openshiftAuthorizationHandler.registerLogin();
            }
        });
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(!openshiftAuthorizationHandler.isLoggedIn());
    }
}
