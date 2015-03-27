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
package org.eclipse.che.ide.ext.bitbucket.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

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

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.eclipse.che.ide.api.notification.Notification.Type.ERROR;
import static org.eclipse.che.security.oauth.OAuthStatus.LOGGED_IN;

/**
 * Provides SSH keys for bitbucket.org and deploys it.
 *
 * @author Kevin Pollet
 */
@Singleton
public class BitbucketSshKeyProvider implements SshKeyProvider, OAuthCallback {
    private final BitbucketClientService        bitbucketService;
    private final String                        baseUrl;
    private final BitbucketLocalizationConstant constant;
    private final NotificationManager           notificationManager;
    private final DialogFactory                 dialogFactory;
    private       AsyncCallback<Void>           callback;
    private       String                        userId;

    @Inject
    public BitbucketSshKeyProvider(@Nonnull final BitbucketClientService bitbucketService,
                                   @Nonnull @Named("restContext") final String baseUrl,
                                   @Nonnull final BitbucketLocalizationConstant constant,
                                   @Nonnull final NotificationManager notificationManager,
                                   @Nonnull final DialogFactory dialogFactory) {

        this.bitbucketService = bitbucketService;
        this.baseUrl = baseUrl;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dialogFactory = dialogFactory;
    }

    @Override
    public void generateKey(final String userId, final AsyncCallback<Void> callback) {
        this.callback = callback;
        this.userId = userId;

        bitbucketService.generateAndUploadSSHKey(new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(final Void notUsed) {
                callback.onSuccess(notUsed);
            }

            @Override
            protected void onFailure(final Throwable exception) {
                if (exception instanceof UnauthorizedException) {
                    oAuthLoginStart();
                    return;
                }

                callback.onFailure(exception);
            }
        });
    }

    private void oAuthLoginStart() {
        dialogFactory.createConfirmDialog(constant.bitbucketSshKeyTitle(), constant.bitbucketSshKeyLabel(), new ConfirmCallback() {
            @Override
            public void accepted() {
                showPopUp();
            }
        }, null).show();
    }

    private void showPopUp() {
        final String authUrl = baseUrl + "/oauth/1.0/authenticate?oauth_provider=bitbucket&userId=" + userId + "&redirect_after_login=" +
                               Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/ws/" + Config.getWorkspaceName();

        new JsOAuthWindow(authUrl, "error.url", 500, 980, this).loginWithOAuth();
    }

    @Override
    public void onAuthenticated(final OAuthStatus authStatus) {
        if (LOGGED_IN.equals(authStatus)) {
            generateKey(userId, callback);

        } else {
            notificationManager.showNotification(new Notification(constant.bitbucketSshKeyUpdateFailed(), ERROR));
        }
    }
}
