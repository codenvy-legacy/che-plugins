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
import com.google.inject.name.Named;

import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketPullRequest;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketPullRequests;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketRepositories;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketRepository;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketRepositoryFork;
import org.eclipse.che.ide.ext.bitbucket.shared.BitbucketUser;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.AsyncRequestLoader;

import javax.annotation.Nonnull;

import static org.eclipse.che.ide.ext.bitbucket.shared.Preconditions.checkArgument;
import static org.eclipse.che.ide.ext.bitbucket.shared.StringHelper.isNullOrEmpty;

/**
 * The Bitbucket service implementation to be use by the client.
 *
 * @author Kevin Pollet
 */
@Singleton
public class BitbucketClientService {
    private static final String BITBUCKET    = "/bitbucket";
    private static final String USER         = "/user";
    private static final String REPOSITORIES = "/repositories";
    private static final String SSH_KEYS     = "/ssh-keys";

    private final String              baseUrl;
    private final AsyncRequestLoader  loader;
    private final AsyncRequestFactory asyncRequestFactory;

    @Inject
    protected BitbucketClientService(@Nonnull @Named("restContext") final String baseUrl,
                                     @Nonnull final AsyncRequestLoader loader,
                                     @Nonnull final AsyncRequestFactory asyncRequestFactory) {
        this.baseUrl = baseUrl + BITBUCKET;
        this.loader = loader;
        this.asyncRequestFactory = asyncRequestFactory;
    }

    /**
     * Get authorized user information.
     *
     * @param callback
     *         callback called when operation is done, cannot be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *         if one parameter is not valid.
     */
    public void getUser(@Nonnull AsyncRequestCallback<BitbucketUser> callback) throws IllegalArgumentException {
        checkArgument(callback != null, "callback");

        final String requestUrl = baseUrl + USER;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /**
     * Get Bitbucket repository information.
     *
     * @param owner
     *         the repository owner, cannot be {@code null}.
     * @param repositorySlug
     *         the repository name, cannot be {@code null}.
     * @param callback
     *         callback called when operation is done, cannot be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *         if one parameter is not valid.
     */
    public void getRepository(@Nonnull final String owner,
                              @Nonnull final String repositorySlug,
                              @Nonnull final AsyncRequestCallback<BitbucketRepository> callback) throws IllegalArgumentException {
        checkArgument(owner != null, "owner");
        checkArgument(repositorySlug != null, "repositorySlug");
        checkArgument(callback != null, "callback");

        final String requestUrl = baseUrl + REPOSITORIES + "/" + owner + "/" + repositorySlug;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /**
     * Get Bitbucket repository forks.
     *
     * @param owner
     *         the repository owner, cannot be {@code null}.
     * @param repositorySlug
     *         the repository name, cannot be {@code null}.
     * @param callback
     *         callback called when operation is done, cannot be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *         if one parameter is not valid.
     */
    public void getRepositoryForks(@Nonnull final String owner,
                                   @Nonnull final String repositorySlug,
                                   @Nonnull final AsyncRequestCallback<BitbucketRepositories> callback) throws IllegalArgumentException {
        checkArgument(owner != null, "owner");
        checkArgument(repositorySlug != null, "repositorySlug");
        checkArgument(callback != null, "callback");

        final String requestUrl = baseUrl + REPOSITORIES + "/" + owner + "/" + repositorySlug + "/forks";
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /**
     * Fork a Bitbucket repository.
     *
     * @param owner
     *         the repository owner, cannot be {@code null}.
     * @param repositorySlug
     *         the repository name, cannot be {@code null}.
     * @param forkName
     *         the fork name, cannot be {@code null} or empty.
     * @param isForkPrivate
     *         if the fork must be private.
     * @param callback
     *         callback called when operation is done, cannot be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *         if one parameter is not valid.
     */
    public void forkRepository(@Nonnull final String owner,
                               @Nonnull final String repositorySlug,
                               @Nonnull final String forkName,
                               final boolean isForkPrivate,
                               @Nonnull final AsyncRequestCallback<BitbucketRepositoryFork> callback) throws IllegalArgumentException {
        checkArgument(owner != null, "owner");
        checkArgument(repositorySlug != null, "repositorySlug");
        checkArgument(forkName != null && !isNullOrEmpty(forkName), "forkName");
        checkArgument(callback != null, "callback");

        final String requestUrl =
                baseUrl + REPOSITORIES + "/" + owner + "/" + repositorySlug + "/fork?forkName=" + forkName + "&isForkPrivate=" +
                isForkPrivate;
        asyncRequestFactory.createPostRequest(requestUrl, null).loader(loader).send(callback);
    }

    /**
     * Get Bitbucket repository pull requests.
     *
     * @param owner
     *         the repository owner, cannot be {@code null}.
     * @param repositorySlug
     *         the repository name, cannot be {@code null}.
     * @param callback
     *         callback called when operation is done, cannot be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *         if one parameter is not valid.
     */
    public void getRepositoryPullRequests(@Nonnull final String owner,
                                          @Nonnull final String repositorySlug,
                                          @Nonnull final AsyncRequestCallback<BitbucketPullRequests> callback)
            throws IllegalArgumentException {
        checkArgument(owner != null, "owner");
        checkArgument(repositorySlug != null, "repositorySlug");
        checkArgument(callback != null, "callback");

        final String requestUrl = baseUrl + REPOSITORIES + "/" + owner + "/" + repositorySlug + "/pullrequests";
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /**
     * Open the given {@link org.eclipse.che.ide.ext.bitbucket.shared.BitbucketPullRequest}.
     *
     * @param owner
     *         the repository owner, cannot be {@code null}.
     * @param repositorySlug
     *         the repository name, cannot be {@code null}.
     * @param pullRequest
     *         the {@link org.eclipse.che.ide.ext.bitbucket.shared.BitbucketPullRequest} to open, cannot be {@code null}.
     * @param callback
     *         callback called when operation is done, cannot be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *         if one parameter is not valid.
     */
    public void openPullRequest(@Nonnull final String owner,
                                @Nonnull final String repositorySlug,
                                @Nonnull final BitbucketPullRequest pullRequest,
                                @Nonnull final AsyncRequestCallback<BitbucketPullRequest> callback) throws IllegalArgumentException {
        checkArgument(!isNullOrEmpty(owner), "owner");
        checkArgument(!isNullOrEmpty(repositorySlug), "repositorySlug");
        checkArgument(pullRequest != null, "pullRequest");
        checkArgument(callback != null, "callback");

        final String requestUrl = baseUrl + REPOSITORIES + "/" + owner + "/" + repositorySlug + "/pullrequests";
        asyncRequestFactory.createPostRequest(requestUrl, pullRequest).loader(loader).send(callback);
    }

    /**
     * Get owner Bitbucket repositories
     *
     * @param owner
     *         the repository owner, cannot be {@code null}.
     * @param callback
     *         callback called when operation is done, cannot be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *         if one parameter is not valid.
     */
    public void getRepositories(@Nonnull final String owner,
                                @Nonnull final AsyncRequestCallback<BitbucketRepositories> callback) throws IllegalArgumentException {
        checkArgument(owner != null, "owner");
        checkArgument(callback != null, "callback");

        final String requestUrl = baseUrl + REPOSITORIES + "/" + owner;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /**
     * Generate and upload new public key if not exist on bitbucket.org.
     *
     * @param callback
     *         callback called when operation is done, cannot be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *         if one parameter is not valid.
     */
    public void generateAndUploadSSHKey(@Nonnull AsyncRequestCallback<Void> callback) throws IllegalArgumentException {
        checkArgument(callback != null, "callback");

        final String requestUrl = baseUrl + SSH_KEYS;
        asyncRequestFactory.createPostRequest(requestUrl, null).loader(loader).send(callback);
    }
}