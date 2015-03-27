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
package org.eclipse.che.ide.ext.github.client;

import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.commons.exception.UnauthorizedException;
import org.eclipse.che.ide.ext.ssh.client.SshKeyProvider;
import org.eclipse.che.ide.rest.AsyncRequestCallback;

import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.Config;
import org.eclipse.che.security.oauth.JsOAuthWindow;
import org.eclipse.che.security.oauth.OAuthCallback;
import org.eclipse.che.security.oauth.OAuthStatus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static org.eclipse.che.security.oauth.OAuthStatus.LOGGED_IN;

/**
 * Provides SSH keys for github.com and deploys it.
 *
 * @author Ann Shumilova
 */
@Singleton
public class GitHubSshKeyProvider implements SshKeyProvider, OAuthCallback {

    private GitHubClientService        gitHubService;
    private String                     baseUrl;
    private GitHubLocalizationConstant constant;
    private AsyncCallback<Void>        callback;
    private String                     userId;
    private NotificationManager        notificationManager;
    private DialogFactory              dialogFactory;

    @Inject
    public GitHubSshKeyProvider(GitHubClientService gitHubService,
                                @Named("restContext") String baseUrl,
                                GitHubLocalizationConstant constant,
                                NotificationManager notificationManager,
                                DialogFactory dialogFactory) {
        this.gitHubService = gitHubService;
        this.baseUrl = baseUrl;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dialogFactory = dialogFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void generateKey(final String userId, final AsyncCallback<Void> callback) {
        this.callback = callback;
        this.userId = userId;

        gitHubService.updatePublicKey(new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void o) {
                callback.onSuccess(o);
            }

            @Override
            protected void onFailure(Throwable e) {
                if (e instanceof UnauthorizedException) {
                    oAuthLoginStart();
                    return;
                }

                callback.onFailure(e);
            }
        });
    }

    /** Log in github */
    private void oAuthLoginStart() {
        dialogFactory.createConfirmDialog(constant.githubSshKeyTitle(), constant.githubSshKeyLabel(), new ConfirmCallback() {
            @Override
            public void accepted() {
                showPopUp();
            }
        }, null).show();
    }

    private void showPopUp() {
        String authUrl = baseUrl + "/oauth/authenticate?oauth_provider=github"
                         + "&scope=user,repo,write:public_key&userId=" + userId + "&redirect_after_login=" +
                         Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/ws/" + Config.getWorkspaceName();
        JsOAuthWindow authWindow = new JsOAuthWindow(authUrl, "error.url", 500, 980, this);
        authWindow.loginWithOAuth();
    }

    /** {@inheritDoc} */
    @Override
    public void onAuthenticated(OAuthStatus authStatus) {
        if (LOGGED_IN.equals(authStatus)) {
            generateKey(userId, callback);
        } else {
            notificationManager.showNotification(new Notification(constant.gitHubSshKeyUpdateFailed(), Notification.Type.ERROR));
        }
    }
}
