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
package org.eclipse.che.ide.ext.github.server;

import org.eclipse.che.api.auth.oauth.OAuthTokenProvider;
import org.eclipse.che.api.auth.shared.dto.OAuthToken;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.json.JsonHelper;
import org.eclipse.che.commons.json.JsonNameConventions;
import org.eclipse.che.commons.json.JsonParseException;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.commons.ParsingResponseException;
import org.eclipse.che.ide.ext.github.shared.Collaborators;
import org.eclipse.che.ide.ext.github.shared.GitHubIssueComment;
import org.eclipse.che.ide.ext.github.shared.GitHubIssueCommentInput;
import org.eclipse.che.ide.ext.github.shared.GitHubPullRequest;
import org.eclipse.che.ide.ext.github.shared.GitHubPullRequestCreationInput;
import org.eclipse.che.ide.ext.github.shared.GitHubPullRequestList;
import org.eclipse.che.ide.ext.github.shared.GitHubRepository;
import org.eclipse.che.ide.ext.github.shared.GitHubRepositoryList;
import org.eclipse.che.ide.ext.github.shared.GitHubUser;

import org.everrest.core.impl.provider.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains methods for retrieving data from GitHub and processing it before sending to client side.
 *
 * @author Oksana Vereshchaka
 */
public class GitHub {

    private final static Logger LOG = LoggerFactory.getLogger(GitHub.class);


    private final OAuthTokenProvider oauthTokenProvider;

    /** Pattern to parse Link header from GitHub response. */
    private final Pattern linkPattern = Pattern.compile("<(.+)>;\\srel=\"(\\w+)\"");

    /** Links' delimiter. */
    private static final String DELIM_LINKS = ",";

    /** Name of the Link header from GitHub response. */
    private static final String HEADER_LINK = "Link";

    /** Name of the link for the first page. */
    private static final String META_FIRST = "first";

    /** Name of the link for the last page. */
    private static final String META_LAST = "last";

    /** Name of the link for the previous page. */
    private static final String META_PREV = "prev";

    /** Name of the link for the next page. */
    private static final String META_NEXT = "next";

    @Inject
    public GitHub(OAuthTokenProvider oauthTokenProvider) {
        this.oauthTokenProvider = oauthTokenProvider;
    }

    /**
     * Get the given user repository.
     *
     * @param user
     *         name of the user.
     * @param repository
     *         name of repository.
     * @return the given user repository
     */
    public GitHubRepository getUserRepository(String user, String repository)
            throws IOException, GitHubException, ParsingResponseException {
        final String method = "GET";
        final String oauthToken = getToken(getUserId());
        String url = "https://api.github.com/repos/" + user + "/" + repository ;
        if (oauthToken != null) {
            url += "?access_token=" + oauthToken;
        }

        final String response = doJsonRequest(url, method, 200);

        return parseJsonResponse(response, GitHubRepository.class, null);
    }

    /**
     * Get the list of public repositories by user's name.
     *
     * @param user
     *         name of user
     * @return {@link org.eclipse.che.ide.ext.github.shared.GitHubRepositoryList} list of GitHub repositories
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubRepositoryList listUserPublicRepositories(String user) throws IOException, GitHubException, ParsingResponseException {
        if (user == null) {
            LOG.error("Git user is not set.");
            throw new IllegalArgumentException("User's name must not be null.");
        }
        final String oauthToken = getToken(getUserId());
        String url = "https://api.github.com/users/" + user + "/repos";
        if (oauthToken != null) {
            url += "?access_token=" + oauthToken;
        }
        GitHubRepositoryList gitHubRepositoryList = DtoFactory.getInstance().createDto(GitHubRepositoryList.class);
        return getRepositories(url, gitHubRepositoryList);
    }

    /**
     * Get the list of all repositories by organization name.
     *
     * @param organization
     *         name of user
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubRepositoryList listAllOrganizationRepositories(String organization) throws IOException,
                                                                                            GitHubException,
                                                                                            ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = "https://api.github.com/orgs/" + organization + "/repos?access_token=" + oauthToken;
        GitHubRepositoryList gitHubRepositoryList = DtoFactory.getInstance().createDto(GitHubRepositoryList.class);
        return getRepositories(url, gitHubRepositoryList);
    }

    private GitHubRepositoryList getRepositories(String url, GitHubRepositoryList gitHubRepositoryList)
            throws IOException, GitHubException, ParsingResponseException {
        final String method = "GET";
        String response = doJsonRequest(url, method, 200, gitHubRepositoryList);
        GitHubRepository[] repositories = parseJsonResponse(response, GitHubRepository[].class, null);
        gitHubRepositoryList.getRepositories().addAll(Arrays.asList(repositories));

        String nextPage = gitHubRepositoryList.getNextPage();
        if (nextPage != null) {
            String oauthToken = getToken(getUserId());
            String nextPageUrl = nextPage + "&access_token=" + oauthToken;
            getRepositories(nextPageUrl, gitHubRepositoryList);
        }
        return gitHubRepositoryList;
    }

    private GitHubPullRequestList getPullRequests(String url, GitHubPullRequestList gitHubPullRequestList)
            throws IOException, GitHubException, ParsingResponseException {
        final String method = "GET";
        String response = doJsonRequest(url, method, 200, gitHubPullRequestList);
        GitHubPullRequest[] pullRequests = parseJsonResponse(response, GitHubPullRequest[].class, null);
        gitHubPullRequestList.getPullRequests().addAll(Arrays.asList(pullRequests));

        String nextPage = gitHubPullRequestList.getNextPage();
        if (nextPage != null) {
            String oauthToken = getToken(getUserId());
            String nextPageUrl = nextPage + "&access_token=" + oauthToken;
            getPullRequests(nextPageUrl, gitHubPullRequestList);
        }
        return gitHubPullRequestList;
    }

    /**
     * Get the page of GitHub repositories by it's link.
     *
     * @param url
     *         location of the page with repositories
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubRepositoryList getPage(String url) throws IOException,
                                                           GitHubException,
                                                           ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String method = "GET";
        url += "&access_token=" + oauthToken;
        GitHubRepositoryList gitHubRepositoryList = DtoFactory.getInstance().createDto(GitHubRepositoryList.class);
        final String response = doJsonRequest(url, method, 200, gitHubRepositoryList);
        GitHubRepository[] repositories = parseJsonResponse(response, GitHubRepository[].class, null);
        gitHubRepositoryList.setRepositories(Arrays.asList(repositories));
        return gitHubRepositoryList;
    }

    /**
     * Get the list of the repositories of the current authorized user.
     *
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubRepositoryList listCurrentUserRepositories() throws IOException, GitHubException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = "https://api.github.com/user/repos?access_token=" + oauthToken;
        GitHubRepositoryList gitHubRepositoryList = DtoFactory.getInstance().createDto(GitHubRepositoryList.class);
        return getRepositories(url, gitHubRepositoryList);
    }

    /**
     * Get the list of forks for a given repository.
     *
     * @param user
     *         name of owner
     * @param repository
     *         name of repository
     *
     * @return {@link GitHubRepositoryList} list of GitHub repositories
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubRepositoryList getForks(String user, String repository) throws IOException, GitHubException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = "https://api.github.com/repos/" + user + '/' + repository + "/forks?access_token=" + oauthToken;
        GitHubRepositoryList gitHubRepositoryList = DtoFactory.getInstance().createDto(GitHubRepositoryList.class);
        return getRepositories(url, gitHubRepositoryList);
    }

    /**
     * Fork a given repository.
     *
     * @param user
     *         name of owner
     * @param repository
     *         name of repository
     *
     * @return {@link GitHubRepository} GitHub repository to be created by forking
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubRepository fork(String user, String repository) throws IOException, GitHubException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = "https://api.github.com/repos/" + user + '/' + repository + "/forks?access_token=" + oauthToken;
        final String method = "POST";
        final String response = doJsonRequest(url, method, 202);
        GitHubRepository forkedRepository = parseJsonResponse(response, GitHubRepository.class, null);
        return forkedRepository;
    }

    /**
     * Comment an issue on given repository.
     *
     * @param user
     *         name of owner
     * @param repository
     *         name of repository
     * @param issue
     *         number of issue
     *
     * @return {@link org.eclipse.che.ide.ext.github.shared.GitHubIssueComment} Comment to be added on GitHub issue
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubIssueComment commentIssue(String user, String repository, String issue, GitHubIssueCommentInput input) throws IOException, GitHubException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = "https://api.github.com/repos/" + user + '/' + repository + "/issues/" + issue + "/comments?access_token=" + oauthToken;
        final String method = "POST";
        final String response = doJsonRequest(url, method, 201, DtoFactory.getInstance().toJson(input));
        GitHubIssueComment comment = parseJsonResponse(response, GitHubIssueComment.class, null);
        return comment;
    }

    /**
     * Get the list of pull requests for given user:repository
     *
     * @param user
     *         name of user
     * @param repository
     *         name of repository
     * @return {@link GitHubPullRequestList} list of GitHub pull requests
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubPullRequestList listPullRequestsByRepository(String user, String repository) throws IOException, GitHubException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = "https://api.github.com/repos/" + user + '/' + repository + "/pulls?access_token=" + oauthToken;
        GitHubPullRequestList gitHubPullRequestList = DtoFactory.getInstance().createDto(GitHubPullRequestList.class);
        return getPullRequests(url, gitHubPullRequestList);
    }

    /**
     * Get the pull request with given id in owner:repository.
     * @param owner
     *         the owner of the repository
     * @param repository
     *         the target repository
     * @param pullRequestId
     *         the pull request id
     * @return the pull request or null if it doesn't exist
     * @throws IOException
     * @throws GitHubException
     * @throws ParsingResponseException
     */
    public GitHubPullRequest getPullRequestById(final String owner, final String repository, final String pullRequestId) throws GitHubException, IOException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = MessageFormat.format("https://api.github.com/repos/{0}/{1}/pulls/{2}?access_token={3}",
                                                owner, repository, pullRequestId, oauthToken);
        final String method = "GET";
        final String response = doJsonRequest(url, method, 200);
        GitHubPullRequest pullRequest = parseJsonResponse(response, GitHubPullRequest.class, null);
        return pullRequest;
    }

    /**
     * Create a pull request on given repository.
     *
     * @param user
     *         name of owner
     * @param repository
     *         name of repository
     *
     * @return {@link GitHubRepository} GitHub repository to be created by forking
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public GitHubPullRequest createPullRequest(String user, String repository, GitHubPullRequestCreationInput input) throws IOException, GitHubException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = "https://api.github.com/repos/" + user + '/' + repository + "/pulls?access_token=" + oauthToken;
        final String method = "POST";
        final String response = doJsonRequest(url, method, 201, DtoFactory.getInstance().toJson(input));
        GitHubPullRequest pr = parseJsonResponse(response, GitHubPullRequest.class, null);
        return pr;
    }

    /**
     * Get the Map which contains available repositories in format Map<Organization name, List<Available repositories>>.
     *
     * @return ap which contains available repositories in format Map<Organization name, List<Available repositories>>
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public Map<String, List<GitHubRepository>> availableRepositoriesList() throws IOException, GitHubException,
                                                                                  ParsingResponseException {
        Map<String, List<GitHubRepository>> repoList = new HashMap<>();
        repoList.put(getGithubUser().getLogin(), listCurrentUserRepositories().getRepositories());
        for (String organizationId : this.listOrganizations()) {
            repoList.put(organizationId, listAllOrganizationRepositories(organizationId).getRepositories());
        }
        return repoList;
    }

    /**
     * Get the array of the organizations of the authorized user.
     *
     * @return list of organizations
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public List<String> listOrganizations() throws IOException, GitHubException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final List<String> result = new ArrayList<>();
        final String url = "https://api.github.com/user/orgs?access_token=" + oauthToken;
        final String method = "GET";
        final String response = doJsonRequest(url, method, 200);
        try {
            JsonValue rootEl = JsonHelper.parseJson(response);
            if (rootEl.isArray()) {
                Iterator<JsonValue> iter = rootEl.getElements();
                while (iter.hasNext()) {
                    result.add(iter.next().getElement("login").getStringValue());
                }
            }

        } catch (JsonParseException e) {
            LOG.error(e.getMessage(), e);
            throw new ParsingResponseException(e);
        }
        return result;
    }

    /**
     * Get authorized user's information.
     *
     * @return {@link GitHubUser} user information
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse
     */
    public GitHubUser getGithubUser() throws IOException, GitHubException, ParsingResponseException {
        final String oauthToken = getToken(getUserId());
        final String url = "https://api.github.com/user?access_token=" + oauthToken;
        final String method = "GET";
        final String response = doJsonRequest(url, method, 200);
        return parseJsonResponse(response, GitHubUser.class, null);
    }

    public Collaborators getCollaborators(String user, String repository)
            throws IOException, ParsingResponseException, GitHubException {
        final String oauthToken = getToken(getUserId());
        final Collaborators myCollaborators = DtoFactory.getInstance().createDto(Collaborators.class);
        if (oauthToken != null && oauthToken.length() != 0) {
            final String url = "https://api.github.com/repos/" + user + '/' + repository + "/collaborators?access_token=" + oauthToken;
            final String method = "GET";
            String response = doJsonRequest(url, method, 200);
            // It seems that collaborators response does not contains all required fields.
            // Iterate over list and request more info about each user.
            final GitHubUser[] collaborators = parseJsonResponse(response, GitHubUser[].class, null);
            final String userId = getUserId();
            for (GitHubUser collaborator : collaborators) {
                response = doJsonRequest(collaborator.getUrl() + "?access_token=" + oauthToken, method, 200);
                GitHubUser gitHubUser = parseJsonResponse(response, GitHubUser.class, null);
                String email = gitHubUser.getEmail();
                if (!(email == null || email.isEmpty() || email.equals(userId))) {
                    myCollaborators.getCollaborators().add(gitHubUser);
                }
            }
        }
        return myCollaborators;
    }


    public String getToken(String user) throws GitHubException, IOException {
        OAuthToken token = oauthTokenProvider.getToken("github", user);
        String oauthToken = token != null ? token.getToken() : null;
        if (oauthToken == null || oauthToken.isEmpty()) {
            return "";
        }
        return oauthToken;
    }


    /**
     * Do json request (without authorization!)
     *
     * @param url
     *         the request url
     * @param method
     *         the request method
     * @param success
     *         expected success code of request
     * @return {@link String} response
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     */
    private String doJsonRequest(String url, String method, int success) throws IOException, GitHubException {
        return doJsonRequest(url, method, success, null, null);
    }

    /**
     * @param url
     *         the request url
     * @param method
     *         the request method
     * @param success
     *         expected success code of request
     * @param gitHubRepositoryList
     *         bean to fill pages info, if exists
     * @return {@link String} response
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     */
    private String doJsonRequest(String url, String method, int success, GitHubRepositoryList gitHubRepositoryList) throws IOException,
                                                                                                                           GitHubException {
        return doJsonRequest(url, method, success, null, gitHubRepositoryList);
    }

    private String doJsonRequest(String url, String method, int success, GitHubPullRequestList gitHubPullRequestList) throws IOException,
                                                                                                                             GitHubException {
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)new URL(url).openConnection();
            http.setInstanceFollowRedirects(false);
            http.setRequestMethod(method);
            http.setRequestProperty("Accept", "application/json");

            if (http.getResponseCode() != success) {
                throw fault(http);
            }

            String result;
            try (InputStream input = http.getInputStream()) {
                result = readBody(input, http.getContentLength());
                if (gitHubPullRequestList != null) {
                    parseLinkHeader(gitHubPullRequestList, http.getHeaderField(HEADER_LINK));
                }
            }
            return result;
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * @param url
     *         the request url
     * @param method
     *         the request method
     * @param success
     *         expected success code of request
     * @param postData
     *         post data represented by json string
     * @return {@link String} response
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     */
    private String doJsonRequest(String url, String method, int success, String postData) throws IOException, GitHubException {
        return doJsonRequest(url, method, success, postData, null);
    }

    /**
     * Do json request (without authorization!)
     *
     * @param url
     *         the request url
     * @param method
     *         the request method
     * @param success
     *         expected success code of request
     * @param postData
     *         post data represented by json string
     * @param gitHubRepositoryList
     *         bean to fill pages info, if exists
     * @return {@link String} response
     * @throws IOException
     *         if any i/o errors occurs
     * @throws GitHubException
     *         if GitHub server return unexpected or error status for request
     */
    private String doJsonRequest(String url, String method, int success, String postData, GitHubRepositoryList gitHubRepositoryList)
            throws IOException, GitHubException {
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)new URL(url).openConnection();
            http.setInstanceFollowRedirects(false);
            http.setRequestMethod(method);
            http.setRequestProperty("Accept", "application/json");
            if (postData != null && !postData.isEmpty()) {
                http.setRequestProperty("Content-Type", "application/json");
                http.setDoOutput(true);

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()))) {
                    writer.write(postData);
                }
            }

            if (http.getResponseCode() != success) {
                throw fault(http);
            }

            String result;
            try (InputStream input = http.getInputStream()) {
                result = readBody(input, http.getContentLength());
                if (gitHubRepositoryList != null) {
                    parseLinkHeader(gitHubRepositoryList, http.getHeaderField(HEADER_LINK));
                }
            }
            return result;
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * @param json
     *         json to parse
     * @param clazz
     *         class described in JSON
     * @param type
     * @return
     * @throws ParsingResponseException
     */
    private <O> O parseJsonResponse(String json, Class<O> clazz, Type type) throws ParsingResponseException {
        try {
            return JsonHelper.fromJson(json, clazz, type, JsonNameConventions.CAMEL_UNDERSCORE);
        } catch (JsonParseException e) {
            throw new ParsingResponseException(e.getMessage(), e);
        }
    }

    /**
     * Parse Link header to retrieve page location. Example of link header:
     * <code><https://api.github.com/organizations/259384/repos?page=3&access_token=123>; rel="next",
     * <https://api.github.com/organizations/259384/repos?page=3&access_token=123>; rel="last",
     * <https://api.github.com/organizations/259384/repos?page=1&access_token=123>; rel="first",
     * <https://api.github.com/organizations/259384/repos?page=1&access_token=123>; rel="prev"
     * </code>
     *
     * @param repositoryList
     * @param linkHeader
     *         the value of link header
     */
    private void parseLinkHeader(GitHubRepositoryList repositoryList, String linkHeader) {
        if (linkHeader == null || linkHeader.isEmpty()) {
            return;
        }
        resetPages(repositoryList);

        String[] links = linkHeader.split(DELIM_LINKS);
        for (String link : links) {
            Matcher matcher = linkPattern.matcher(link.trim());
            if (matcher.matches() && matcher.groupCount() >= 2) {
                // First group is the page's location:
                String value = matcher.group(1);
                // Remove the value of access_token parameter if exists, not to be send to client:
                value = value.replaceFirst("access_token=\\w+&?", "");
                // Second group is page's type
                String rel = matcher.group(2);
                switch (rel) {
                    case META_FIRST:
                        repositoryList.setFirstPage(value);
                        break;
                    case META_LAST:
                        repositoryList.setLastPage(value);
                        break;
                    case META_NEXT:
                        repositoryList.setNextPage(value);
                        break;
                    case META_PREV:
                        repositoryList.setPrevPage(value);
                        break;
                }
            }
        }
    }

    private void parseLinkHeader(GitHubPullRequestList pullRequestList, String linkHeader) {
        if (linkHeader == null || linkHeader.isEmpty()) {
            return;
        }
        resetPages(pullRequestList);

        String[] links = linkHeader.split(DELIM_LINKS);
        for (String link : links) {
            Matcher matcher = linkPattern.matcher(link.trim());
            if (matcher.matches() && matcher.groupCount() >= 2) {
                // First group is the page's location:
                String value = matcher.group(1);
                // Remove the value of access_token parameter if exists, not to be send to client:
                value = value.replaceFirst("access_token=\\w+&?", "");
                // Second group is page's type
                String rel = matcher.group(2);
                switch (rel) {
                    case META_FIRST:
                        pullRequestList.setFirstPage(value);
                        break;
                    case META_LAST:
                        pullRequestList.setLastPage(value);
                        break;
                    case META_NEXT:
                        pullRequestList.setNextPage(value);
                        break;
                    case META_PREV:
                        pullRequestList.setPrevPage(value);
                        break;
                }
            }
        }
    }

    private void resetPages(GitHubRepositoryList repositoryList) {
        repositoryList.setFirstPage(null);
        repositoryList.setLastPage(null);
        repositoryList.setNextPage(null);
        repositoryList.setPrevPage(null);
    }

    private void resetPages(GitHubPullRequestList pullRequestList) {
        pullRequestList.setFirstPage(null);
        pullRequestList.setLastPage(null);
        pullRequestList.setNextPage(null);
        pullRequestList.setPrevPage(null);
    }

    private GitHubException fault(HttpURLConnection http) throws IOException {
        final int responseCode = http.getResponseCode();

        try (final InputStream stream = (responseCode >= 400 ? http.getErrorStream() : http.getInputStream())) {

            String body = null;
            if (stream != null) {
                final int length = http.getContentLength();
                body = readBody(stream, length);
            }

            return new GitHubException(responseCode, body, http.getContentType());
        }
    }

    private static String readBody(InputStream input, int contentLength) throws IOException {
        String body = null;
        if (contentLength > 0) {
            byte[] b = new byte[contentLength];
            int off = 0;
            int i;
            while ((i = input.read(b, off, contentLength - off)) > 0) {
                off += i;
            }
            body = new String(b);
        } else if (contentLength < 0) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int point;
            while ((point = input.read(buf)) != -1) {
                bout.write(buf, 0, point);
            }
            body = bout.toString();
        }
        return body;
    }

    /**
     * ************************************************************************************
     * Common methods
     * *************************************************************************************
     */
    private String getUserId() {
        return EnvironmentContext.getCurrent().getUser().getId();
    }
}
