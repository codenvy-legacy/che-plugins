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
package org.eclipse.che.ide.ext.git.server.nativegit;

import com.google.api.client.auth.oauth.OAuthCredentialsResponse;

import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.shared.GitUser;
import org.eclipse.che.security.oauth1.BitbucketOAuthAuthenticator;
import org.eclipse.che.security.oauth1.OAuthAuthenticationException;
import org.eclipse.che.security.oauth1.shared.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * {@link org.eclipse.che.ide.ext.git.server.nativegit.CredentialsProvider} implementation for Bitbucket.
 *
 * @author Kevin Pollet
 */
@Singleton
public class BitbucketOAuthCredentialProvider implements CredentialsProvider {
    private static final Logger LOG                 = LoggerFactory.getLogger(BitbucketOAuthCredentialProvider.class);
    private static final String OAUTH_PROVIDER_NAME = "bitbucket";

    private final BitbucketOAuthAuthenticator oAuthAuthenticator;

    @Inject
    public BitbucketOAuthCredentialProvider(@Nonnull final BitbucketOAuthAuthenticator oAuthAuthenticator) {
        this.oAuthAuthenticator = oAuthAuthenticator;
    }

    @Override
    public UserCredential getUserCredential() throws GitException {
        try {

            final OAuthCredentialsResponse credentials = oAuthAuthenticator.getToken(EnvironmentContext.getCurrent().getUser().getId());
            if (credentials != null) {
                return new UserCredential(credentials.token, credentials.tokenSecret, OAUTH_PROVIDER_NAME);
            }

        } catch (IOException e) {
            LOG.warn(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public GitUser getUser() throws GitException {
        try {

            final OAuthCredentialsResponse credentials = oAuthAuthenticator.getToken(EnvironmentContext.getCurrent().getUser().getId());
            if (credentials != null) {
                final User user = oAuthAuthenticator.getUser(credentials.token, credentials.tokenSecret);
                if (user != null) {
                    return DtoFactory.getInstance()
                                     .createDto(GitUser.class)
                                     .withEmail(user.getEmail())
                                     .withName(user.getName());
                }

            }

        } catch (final IOException | OAuthAuthenticationException e) {
            LOG.warn(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String getId() {
        return OAUTH_PROVIDER_NAME;
    }

    @Override
    public boolean canProvideCredentials(String url) {
        return url.contains("bitbucket.org");
    }
}
