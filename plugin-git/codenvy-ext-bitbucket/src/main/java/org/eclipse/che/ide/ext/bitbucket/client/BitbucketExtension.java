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

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.ext.ssh.client.SshKeyService;

import javax.annotation.Nonnull;

/**
 * Extension adds Bitbucket support to the IDE Application.
 *
 * @author Kevin Pollet
 */
@Singleton
@Extension(title = "Bitbucket", version = "3.0.0")
public class BitbucketExtension {
    public static final String BITBUCKET_HOST = "bitbucket.org";

    @Inject
    public BitbucketExtension(@Nonnull final SshKeyService sshKeyService,
                              @Nonnull final BitbucketSshKeyProvider bitbucketSshKeyProvider) {

        sshKeyService.registerSshKeyProvider(BITBUCKET_HOST, bitbucketSshKeyProvider);
    }
}