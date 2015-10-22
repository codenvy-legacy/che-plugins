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

import org.eclipse.che.api.auth.shared.dto.OAuthToken;
import org.eclipse.che.commons.json.JsonHelper;
import org.eclipse.che.commons.json.JsonParseException;
import org.eclipse.che.ide.ext.openshift.shared.dto.User;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import static com.google.api.client.repackaged.com.google.common.base.Strings.isNullOrEmpty;

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
    public org.eclipse.che.security.oauth.shared.User getUser(OAuthToken accessToken) throws OAuthAuthenticationException {
        final User currentUser = requestUser(openshiftApiEndpoint + "/oapi/v1/users/~", accessToken.getToken());

        return new OpenshiftUser().withEmail(currentUser.getMetadata().getName())
                                  .withName(currentUser.getFullName());
    }

    @Override
    public final String getOAuthProvider() {
        return "openshift";
    }

    @Override
    public OAuthToken getToken(String userId) throws IOException {
        final OAuthToken token = super.getToken(userId);

        if (token == null || isNullOrEmpty(token.getToken())) {
            return null;
        }

        return token;
    }

    private User requestUser(String requestUrl, String token) throws OAuthAuthenticationException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)new URL(requestUrl).openConnection();
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            connection.setRequestMethod("GET");
            connection.addRequestProperty("Authorization", "Bearer " + token);

            try (InputStream urlInputStream = connection.getInputStream()) {
                return JsonHelper.fromJson(urlInputStream, User.class, null);
            }
        } catch (IOException | JsonParseException e) {
            throw new OAuthAuthenticationException(e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
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
