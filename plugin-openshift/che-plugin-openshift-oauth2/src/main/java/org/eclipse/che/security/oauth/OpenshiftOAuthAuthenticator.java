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
package org.eclipse.che.security.oauth;

import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.openshift.internal.restclient.http.HttpClientException;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ISSLCertificateCallback;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.model.user.IUser;

import org.eclipse.che.api.auth.shared.dto.OAuthToken;
import org.eclipse.che.security.oauth.shared.User;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * OAuth authentication  for openshift account.
 *
 * @author Sergii Leschenko
 */
@Singleton
public class OpenshiftOAuthAuthenticator extends OAuthAuthenticator {
    private final String openshiftApiEndpoint;

    @Inject
    public OpenshiftOAuthAuthenticator(@Named("oauth.openshift.clientid") String clientId,
                                       @Named("oauth.openshift.clientsecret") String clientSecret,
                                       @Named("oauth.openshift.redirecturis") String[] redirectUris,
                                       @Named("oauth.openshift.authuri") String authUri,
                                       @Named("oauth.openshift.tokenuri") String tokenUri,
                                       @Named("openshift.api.endpoint") String openshiftApiEndpoint) throws IOException {
        super(clientId, clientSecret, redirectUris, authUri, tokenUri, new MemoryDataStoreFactory());
        this.openshiftApiEndpoint = openshiftApiEndpoint;
    }

    @Override
    public User getUser(OAuthToken accessToken) throws OAuthAuthenticationException {
        final IUser currentUser = createClient(accessToken.getToken()).getCurrentUser();
        return new OpenshiftUser().withEmail(currentUser.getName())
                                  .withName(currentUser.getFullName());
    }

    @Override
    public final String getOAuthProvider() {
        return "openshift";
    }

    @Override
    public OAuthToken getToken(String userId) throws IOException {
        final OAuthToken token = super.getToken(userId);

        if (token == null || token.getToken() == null || token.getToken().isEmpty()) {
            return null;
        }

        // Need to check if token which stored is valid for requests, then if valid - we returns it to caller
        try {
            createClient(token.getToken()).getOpenShiftAPIVersion();
        } catch (HttpClientException e) {
            return null;
        }

        return token;
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

    //TODO Remove when server will has normal sll certificates
    //Need for working with https://api.codenvy.openshift.com/ server that has broken sll certificates.
    static {
        disableSLLVerification();
    }

    private static void disableSLLVerification() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }

                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            try {
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
