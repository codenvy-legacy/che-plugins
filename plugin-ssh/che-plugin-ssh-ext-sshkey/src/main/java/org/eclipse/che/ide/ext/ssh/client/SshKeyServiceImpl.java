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
package org.eclipse.che.ide.ext.ssh.client;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.ssh.dto.GenKeyRequest;
import org.eclipse.che.ide.ext.ssh.dto.KeyItem;
import org.eclipse.che.ide.ext.ssh.dto.PublicKey;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.RestContext;
import org.eclipse.che.ide.ui.loaders.request.LoaderFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The implementation of {@link SshKeyService}.
 *
 * @author Evgen Vidolob
 */
@Singleton
public class SshKeyServiceImpl implements SshKeyService {
    private final String                      baseUrl;
    private final String                      workspaceId;
    private final LoaderFactory               loaderFactory;
    private final DtoFactory                  dtoFactory;
    private final AsyncRequestFactory         asyncRequestFactory;
    private final Map<String, SshKeyProvider> sshKeyProviders;

    @Inject
    protected SshKeyServiceImpl(@RestContext String baseUrl,
                                AppContext appContext,
                                LoaderFactory loaderFactory,
                                DtoFactory dtoFactory,
                                AsyncRequestFactory asyncRequestFactory) {
        this.baseUrl = baseUrl;
        this.workspaceId = appContext.getWorkspace().getId();
        this.loaderFactory = loaderFactory;
        this.dtoFactory = dtoFactory;
        this.asyncRequestFactory = asyncRequestFactory;
        this.sshKeyProviders = new HashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public void getAllKeys(@NotNull AsyncRequestCallback<List<KeyItem>> callback) {
        asyncRequestFactory.createGetRequest(baseUrl + "/ssh-keys/" + workspaceId + "/all")
                           .loader(loaderFactory.newLoader("Getting SSH keys....")).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void generateKey(@NotNull String host, @NotNull AsyncRequestCallback<Void> callback) {
        String url = baseUrl + "/ssh-keys/" + workspaceId + "/gen";

        GenKeyRequest keyRequest = dtoFactory.createDto(GenKeyRequest.class).withHost(host);

        asyncRequestFactory.createPostRequest(url, keyRequest)
                           .loader(loaderFactory.newLoader("Generate keys for " + host))
                           .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getPublicKey(@NotNull KeyItem keyItem, @NotNull AsyncRequestCallback<PublicKey> callback) {
        asyncRequestFactory.createGetRequest(keyItem.getPublicKeyUrl())
                           .loader(loaderFactory.newLoader("Getting public SSH key for " + keyItem.getHost())).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteKey(@NotNull KeyItem keyItem, @NotNull AsyncRequestCallback<Void> callback) {
        asyncRequestFactory.createGetRequest(keyItem.getRemoteKeyUrl())
                           .loader(loaderFactory.newLoader("Deleting SSH keys for " + keyItem.getHost())).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, SshKeyProvider> getSshKeyProviders() {
        return sshKeyProviders;
    }

    /** {@inheritDoc} */
    @Override
    public void registerSshKeyProvider(@NotNull String host, @NotNull SshKeyProvider sshKeyProvider) {
        sshKeyProviders.put(host, sshKeyProvider);
    }
}