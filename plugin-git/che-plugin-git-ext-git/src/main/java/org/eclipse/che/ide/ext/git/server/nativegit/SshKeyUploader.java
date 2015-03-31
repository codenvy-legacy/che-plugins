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

import org.eclipse.che.api.auth.oauth.OAuthTokenProvider;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.ext.ssh.server.SshKey;

import java.io.IOException;

/**
 * Provides functionality to upload Public SSH key part to GitHub, BitBucket, etc.
 *
 * @author Vladyslav Zhukovskii
 */
public abstract class SshKeyUploader {
    protected final OAuthTokenProvider tokenProvider;

    protected SshKeyUploader(OAuthTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Upload public key part to GitRepository management.
     *
     * @throws IOException
     *         if an i/o error occurs
     * @throws UnauthorizedException
     *         if user is not authorized to access SSH key storage
     */
    public abstract void uploadKey(SshKey publicKey) throws IOException, UnauthorizedException;

    /**
     * Check if specified url matched to use current upload provider.
     *
     * @param url
     *         input url to check
     * @return true if current uploader can be applied to upload key to host specified in url, passed as parameter
     */
    public abstract boolean match(String url);
}
