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
package org.eclipse.che.ide.ext.bitbucket.server.rest;

import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.commons.ParsingResponseException;
import org.eclipse.che.ide.ext.bitbucket.server.Bitbucket;
import org.eclipse.che.ide.ext.bitbucket.server.BitbucketException;
import org.eclipse.che.ide.ext.bitbucket.server.BitbucketKeyUploader;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketPullRequest;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketPullRequests;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketRepositories;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketRepository;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketRepositoryFork;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketUser;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.ssh.server.SshKey;
import org.eclipse.che.ide.ext.ssh.server.SshKeyStore;
import org.eclipse.che.ide.ext.ssh.server.SshKeyStoreException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.IOException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * REST service for Bitbucket.
 *
 * @author Kevin Pollet
 */
@Path("bitbucket")
public class BitbucketService {
    private final Bitbucket            bitbucket;
    private final BitbucketKeyUploader bitbucketKeyUploader;
    private final SshKeyStore          sshKeyStore;

    @Inject
    public BitbucketService(@Nonnull final Bitbucket bitbucket,
                            @Nonnull final BitbucketKeyUploader bitbucketKeyUploader,
                            @Nonnull final SshKeyStore sshKeyStore) {
        this.bitbucket = bitbucket;
        this.bitbucketKeyUploader = bitbucketKeyUploader;
        this.sshKeyStore = sshKeyStore;
    }

    /**
     * @see org.eclipse.che.ide.ext.bitbucket.server.Bitbucket#getUser()
     */
    @GET
    @Path("user")
    @Produces(APPLICATION_JSON)
    public BitbucketUser getUser() throws IOException, BitbucketException, ParsingResponseException {
        return bitbucket.getUser();
    }

    /**
     * @see org.eclipse.che.ide.ext.bitbucket.server.Bitbucket#getRepository(String, String)
     */
    @GET
    @Path("repositories/{owner}/{repositorySlug}")
    @Produces(APPLICATION_JSON)
    public BitbucketRepository getRepository(@PathParam("owner") final String owner,
                                             @PathParam("repositorySlug") final String repositorySlug)
            throws IOException, BitbucketException, ParsingResponseException {

        return bitbucket.getRepository(owner, repositorySlug);
    }

    /**
     * @see org.eclipse.che.ide.ext.bitbucket.server.Bitbucket#getRepositoryForks(String, String)
     */
    @GET
    @Path("repositories/{owner}/{repositorySlug}/forks")
    @Produces(APPLICATION_JSON)
    public BitbucketRepositories getRepositoryForks(@PathParam("owner") final String owner,
                                                    @PathParam("repositorySlug") final String repositorySlug)
            throws IOException, BitbucketException, ParsingResponseException {

        return bitbucket.getRepositoryForks(owner, repositorySlug);
    }

    /**
     * @see org.eclipse.che.ide.ext.bitbucket.server.Bitbucket#forkRepository(String, String, String, boolean)
     */
    @POST
    @Path("repositories/{owner}/{repositorySlug}/fork")
    @Produces(APPLICATION_JSON)
    public BitbucketRepositoryFork forkRepository(@PathParam("owner") final String owner,
                                                  @PathParam("repositorySlug") final String repositorySlug,
                                                  @QueryParam("forkName") final String forkName,
                                                  @QueryParam("isForkPrivate") @DefaultValue("false") final boolean isForkPrivate)
            throws IOException, BitbucketException, ParsingResponseException {

        return bitbucket.forkRepository(owner, repositorySlug, forkName, isForkPrivate);
    }

    /**
     * @see org.eclipse.che.ide.ext.bitbucket.server.Bitbucket#getRepositoryPullRequests(String, String)
     */
    @GET
    @Path("repositories/{owner}/{repositorySlug}/pullrequests")
    @Produces(APPLICATION_JSON)
    public BitbucketPullRequests getRepositoryPullRequests(@PathParam("owner") final String owner,
                                                           @PathParam("repositorySlug") final String repositorySlug)
            throws IOException, BitbucketException, ParsingResponseException {

        return bitbucket.getRepositoryPullRequests(owner, repositorySlug);
    }

    /**
     * @see org.eclipse.che.ide.ext.bitbucket.server.Bitbucket#openPullRequest(String, String, org.eclipse.che.ide.ext.bitbucket.shared.BitbucketPullRequest)
     */
    @POST
    @Path("repositories/{owner}/{repositorySlug}/pullrequests")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public BitbucketPullRequest openPullRequest(@PathParam("owner") final String owner,
                                                @PathParam("repositorySlug") final String repositorySlug,
                                                BitbucketPullRequest pullRequest)
            throws IOException, BitbucketException, ParsingResponseException {

        return bitbucket.openPullRequest(owner, repositorySlug, pullRequest);
    }

    @POST
    @Path("ssh-keys")
    public void generateAndUploadSSHKey() throws GitException, UnauthorizedException {
        final String host = "bitbucket.org";
        SshKey publicKey;

        try {

            if (sshKeyStore.getPrivateKey(host) != null) {
                publicKey = sshKeyStore.getPublicKey(host);
                if (publicKey == null) {
                    sshKeyStore.removeKeys(host);
                    publicKey = sshKeyStore.genKeyPair(host, null, null).getPublicKey();
                }
            } else {
                publicKey = sshKeyStore.genKeyPair(host, null, null).getPublicKey();
            }

        } catch (final SshKeyStoreException e) {
            throw new GitException(e);
        }

        // update public key
        try {

            bitbucketKeyUploader.uploadKey(publicKey);

        } catch (final IOException e) {
            throw new GitException(e);
        }
    }
}
