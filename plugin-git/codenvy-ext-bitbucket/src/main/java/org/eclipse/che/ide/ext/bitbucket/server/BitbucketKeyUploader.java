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
package org.eclipse.che.ide.ext.bitbucket.server;

import org.eclipse.che.api.auth.oauth.OAuthAuthorizationHeaderProvider;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.json.JsonHelper;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketKey;
import org.eclipse.che.ide.ext.git.server.commons.Util;
import org.eclipse.che.ide.ext.git.server.nativegit.SshKeyUploader;
import org.eclipse.che.ide.ext.ssh.server.SshKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.MimeType.APPLICATION_JSON;
import static org.eclipse.che.ide.rest.HTTPHeader.ACCEPT;
import static org.eclipse.che.ide.rest.HTTPHeader.AUTHORIZATION;
import static org.eclipse.che.ide.rest.HTTPHeader.CONTENT_LENGTH;
import static org.eclipse.che.ide.rest.HTTPHeader.CONTENT_TYPE;
import static org.eclipse.che.ide.rest.HTTPMethod.GET;
import static org.eclipse.che.ide.rest.HTTPMethod.POST;
import static org.eclipse.che.ide.rest.HTTPStatus.FORBIDDEN;
import static org.eclipse.che.ide.rest.HTTPStatus.OK;

/**
 * Uploads keys to Bitbucket.
 *
 * @author Kevin Pollet
 */
@Singleton
public class BitbucketKeyUploader extends SshKeyUploader {
    private static final Logger LOG                 = LoggerFactory.getLogger(BitbucketKeyUploader.class);
    private static final String OAUTH_PROVIDER_NAME = "bitbucket";

    private final OAuthAuthorizationHeaderProvider authorizationHeaderProvider;

    @Inject
    public BitbucketKeyUploader(@Nonnull final OAuthAuthorizationHeaderProvider authorizationHeaderProvider) {
        super(null);
        this.authorizationHeaderProvider = authorizationHeaderProvider;
    }

    @Override
    public boolean match(final String url) {
        return Util.isSSH(url) && Util.isBitbucket(url);
    }

    @Override
    public void uploadKey(final SshKey publicKey) throws IOException, UnauthorizedException {
        final StringBuilder answer = new StringBuilder();
        final String publicKeyString = new String(publicKey.getBytes());
        final String sshKeysUrl = "https://api.bitbucket.org/1.0/ssh-keys";

        final List<BitbucketKey> bitbucketUserPublicKeys = getUserPublicKeys(sshKeysUrl, answer);
        for (final BitbucketKey oneBitbucketUserPublicKey : bitbucketUserPublicKeys) {
            if (publicKeyString.startsWith(oneBitbucketUserPublicKey.getKey())) {
                return;
            }
        }

        final Map<String, String> postParams = new HashMap<>(2);
        postParams.put("label", Util.getCodenvyTimeStampKeyLabel());
        postParams.put("key", new String(publicKey.getBytes()));

        final String postBody = JsonHelper.toJson(postParams);

        LOG.debug("Upload public key: {}", postBody);

        int responseCode;
        HttpURLConnection conn = null;
        try {

            conn = (HttpURLConnection)new URL(sshKeysUrl).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod(POST);
            conn.setRequestProperty(ACCEPT, APPLICATION_JSON);
            conn.setRequestProperty(AUTHORIZATION, authorizationHeaderProvider
                    .getAuthorizationHeader(OAUTH_PROVIDER_NAME, getUserId(), POST, sshKeysUrl, Collections.<String, String>emptyMap()));

            conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
            conn.setRequestProperty(CONTENT_LENGTH, String.valueOf(postBody.length()));
            conn.setDoOutput(true);

            try (OutputStream out = conn.getOutputStream()) {
                out.write(postBody.getBytes());
            }
            responseCode = conn.getResponseCode();

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        LOG.debug("Upload key response code: {}", responseCode);

        if (responseCode != OK) {
            final String exceptionMessage = String.format("%d: Failed to upload public key to https://bitbucket.org", responseCode);

            if (responseCode == FORBIDDEN) {
                throw new UnauthorizedException(exceptionMessage);
            }
            throw new IOException(exceptionMessage);
        }
    }

    private List<BitbucketKey> getUserPublicKeys(final String requestUrl, final StringBuilder answer) {
        HttpURLConnection conn = null;

        try {

            conn = (HttpURLConnection)new URL(requestUrl).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod(GET);
            conn.setRequestProperty(ACCEPT, APPLICATION_JSON);
            conn.setRequestProperty(AUTHORIZATION, authorizationHeaderProvider
                    .getAuthorizationHeader(OAUTH_PROVIDER_NAME, getUserId(), GET, requestUrl, Collections.<String, String>emptyMap()));

            if (conn.getResponseCode() == OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        answer.append(line).append('\n');
                    }
                }

                return DtoFactory.getInstance().createListDtoFromJson(answer.toString(), BitbucketKey.class);
            }
            return Collections.emptyList();

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String getUserId() {
        return EnvironmentContext.getCurrent().getUser().getId();
    }
}
