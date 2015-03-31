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
package org.eclipse.che.ide.ext.git.server.commons;

import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.ext.git.server.GitConnection;
import org.eclipse.che.ide.ext.git.server.GitConnectionFactory;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.shared.LsRemoteRequest;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Check if repository is public and set private permission to vfs otherwise.
 *
 * @author Vladyslav Zhukovskii
 */
@Singleton
public class GitRepositoryPrivacyChecker {
    private static final Pattern SSH_URL = Pattern.compile("^(?:ssh://)?(?:\\w+@)([a-zA-Z-.]+)(?::(\\d+))?+(?:/|:)(.+)$");
    private final GitConnectionFactory gitConnectionFactory;

    @Inject
    public GitRepositoryPrivacyChecker(GitConnectionFactory gitConnectionFactory) {
        this.gitConnectionFactory = gitConnectionFactory;
    }

    /**
     * Check repository public or not with calling git ls-remote.
     * If url is SSH, it will be converted to https or http, because ssh can't be checked in such way.
     *
     * @param gitUrl
     *         repository url
     * @return <code>true</code> when repository is public
     */
    public boolean isRepositoryPublic(String gitUrl) {
        // Calls git ls-remote
        GitConnection gitConnection = null;
        try {
            try {
                // Any directory is OK for init GitConnection instance. Command 'git ls-remote <URL>' doesn't need git working directory.
                gitConnection = gitConnectionFactory.getConnection(System.getProperty("java.io.tmpdir"));
            } catch (GitException e) {
                // Can't continue
                throw new RuntimeException(e);
            }
            Matcher matcher = SSH_URL.matcher(gitUrl);
            DtoFactory dtoFactory = DtoFactory.getInstance();
            if (!matcher.matches()) {
                try {
                    gitConnection.lsRemote(dtoFactory.createDto(LsRemoteRequest.class).withRemoteUrl(gitUrl).withUseAuthorization(false));
                    return true;
                } catch (GitException | UnauthorizedException e) {
                    return false;
                }
            } else {
                // If url is ssh, use special check order is important
                String host = matcher.group(1);
                String port = matcher.group(2);
                String path = matcher.group(3);
                List<String> gitUrls = new ArrayList<>();
                gitUrls.add(createHttpsUrl(host, port, path));
                gitUrls.add(createHttpUrl(host, port, path));
                gitUrls.add(createHttpsWithoutPortUrl(host, path));
                gitUrls.add(createHttpWithoutPortUrl(host, path));
                LsRemoteRequest lsRemoteRequest = dtoFactory.createDto(LsRemoteRequest.class).withUseAuthorization(false);
                for (String repoUrl : gitUrls) {
                    try {
                        lsRemoteRequest.setRemoteUrl(repoUrl);
                        gitConnection.lsRemote(lsRemoteRequest);
                        return true;
                    } catch (GitException | UnauthorizedException ignored) {
                        // try another url to check for
                    }
                }
                return false;
            }
        } finally {
            if (gitConnection != null) {
                gitConnection.close();
            }
        }
    }

    private String createHttpsUrl(String host, String port, String path) {
        UriBuilder ub = UriBuilder.fromPath(path);
        ub.scheme("https").host(host);
        if (port != null && port.isEmpty()) {
            ub.port(Integer.parseInt(port));
        }
        return ub.build().toString();
    }

    private String createHttpUrl(String host, String port, String path) {
        UriBuilder ub = UriBuilder.fromPath(path);
        ub.scheme("http").host(host);
        if (port != null && port.isEmpty()) {
            ub.port(Integer.parseInt(port));
        }
        return ub.build().toString();
    }

    private String createHttpsWithoutPortUrl(String host, String path) {
        return UriBuilder.fromPath(path).host(host).scheme("https").build().toString();
    }

    private String createHttpWithoutPortUrl(String host, String path) {
        return UriBuilder.fromPath(path).host(host).scheme("http").build().toString();
    }
}
