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
package org.eclipse.che.ide.ext.openshift.server;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ISSLCertificateCallback;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;

import org.eclipse.che.api.auth.shared.dto.OAuthToken;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.security.oauth.RemoteOAuthTokenProvider;

import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.security.cert.X509Certificate;

import static com.google.api.client.repackaged.com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Sergii Leschenko
 */
public class ClientFactory {
    private final String                   openshiftApiEndpoint;
    private final RemoteOAuthTokenProvider provider;

    @Inject
    public ClientFactory(@Named("openshift.api.endpoint") String openshiftApiEndpoint,
                         RemoteOAuthTokenProvider provider) {
        this.openshiftApiEndpoint = openshiftApiEndpoint;
        this.provider = provider;
    }

    public IClient createClient() throws UnauthorizedException, ServerException {
        try {
            final OAuthToken token = provider.getToken("openshift", EnvironmentContext.getCurrent().getUser().getId());
            if (token == null || isNullOrEmpty(token.getToken())) {
                throw new UnauthorizedException("User doesn't have access token to openshift");
            }
            return createClient(token.getToken());
        } catch (IOException e) {
            throw new ServerException("Error getting of access token to openshift");
        }
    }

    public IClient createClient(String token) {
        IClient client = new com.openshift.restclient.ClientFactory().create(openshiftApiEndpoint, new ISSLCertificateCallback() {
            @Override
            public boolean allowCertificate(X509Certificate[] x509Certificates) {
                return true;
            }

            @Override
            public boolean allowHostname(String s, SSLSession sslSession) {
                return true;
            }
        });
        client.setAuthorizationStrategy(new TokenAuthorizationStrategy(token));
        return client;
    }
}
