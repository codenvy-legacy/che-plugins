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
package org.kohsuke.github;

import java.io.IOException;

/**
 *
 * Getting GitHub repo with mirror url original lib not support this
 *
 * https://github.com/kohsuke/github-api/pull/233
 * @author Vitalii Parfonov
 */
public class CheGitHubClient {

    public static GHRepositoryWithMirrorUrl getRepository(GitHub gitHub, String user, String repo) throws IOException {
        return gitHub.retrieve().to("/repos/" + user + '/' + repo, GHRepositoryWithMirrorUrl.class);
    }
}
