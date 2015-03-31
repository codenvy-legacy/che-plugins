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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Commons class for Git and nested modules.
 *
 * @author Vladyslav Zhukovskii
 * @author Kevin Pollet
 */
public class Util {
    public static final Pattern GIT_SSH_URL_PATTERN =
            Pattern.compile("((((git|ssh)://)(([^\\\\/@:]+@)??)[^\\\\/@:]+)|([^\\\\/@:]+@[^\\\\/@:]+))(:|/)[^\\\\@:]+");

    public static final Pattern GITHUB_URL_PATTERN = Pattern.compile(".*github\\.com.*");

    public static final Pattern BITBUCKET_URL_PATTERN = Pattern.compile(".*bitbucket\\.org.*");

    public static String getCodenvyTimeStampKeyLabel() {
        return "Codenvy SSH Key (" + new SimpleDateFormat().format(new Date()) + ")";
    }

    public static boolean isSSH(String url) {
        return GIT_SSH_URL_PATTERN.matcher(url).matches();
    }

    public static boolean isGitHub(String url) {
        return GITHUB_URL_PATTERN.matcher(url).matches();
    }

    public static boolean isBitbucket(String url) {
        return BITBUCKET_URL_PATTERN.matcher(url).matches();
    }

    /**
     * Parses URL and get host from it, if it is possible
     *
     * @param url
     *         URL
     * @return host if it exists in URL or <code>null</code> if it doesn't.
     */
    public static String getHost(String url) {
        if (isSSH(url)) {
            int start;
            if ((start = url.indexOf("://")) != -1) {
                /*
                    Host between ("://" or "@") and (":" or "/")
                    for ssh or git Schema uri.
                    ssh://user@host.com/some/path
                    ssh://host.com/some/path
                    git://host.com/user/repo
                    can be with port
                    ssh://host.com:port/some/path
                 */
                int endPoint = url.lastIndexOf(":") != start ? url.lastIndexOf(":") : url.indexOf("/", start + 3);
                int startPoint = !url.contains("@") ? start + 3 : url.indexOf("@") + 1;
                return url.substring(startPoint, endPoint);
            } else {
                /*
                    Host between "@" and ":"
                    user@host.com:login/repo
                 */
                return url.substring(url.indexOf("@") + 1, url.indexOf(":"));
            }
        }
        return null;
    }

}
