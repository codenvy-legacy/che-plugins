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

import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.ext.git.server.GitConnection;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.shared.LsRemoteRequest;
import org.eclipse.che.ide.ext.git.shared.RemoteReference;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertTrue;

/**
 * @author Alexander Garagatyi
 */
public class LsRemoteTest extends BaseTest {

    @Test
    public void testShouldBeAbleToGetResultFromPublicRepo() throws GitException, UnauthorizedException {
        GitConnection connection = connectionFactory.getConnection("/tmp", getUser());
        Set<RemoteReference> remoteReferenceSet =
                new HashSet<>(connection.lsRemote(newDTO(LsRemoteRequest.class)
                        .withRemoteUrl("https://github.com/codenvy/everrest.git")
                        .withUseAuthorization(false)));
        assertTrue(remoteReferenceSet.contains(newDTO(RemoteReference.class)
                .withCommitId("259e24c83c8a122af858c8306c3286586404ef3f")
                .withReferenceName("refs/tags/1.1.9")));
    }

    @Test(expectedExceptions = GitException.class)
    public void testShouldThrowGitExceptionIfUserTryGetInfoAboutPrivateRepoAndUserIsUnauthorized() throws GitException, UnauthorizedException {
        GitConnection connection = connectionFactory.getConnection("/tmp", getUser());
        connection.lsRemote(newDTO(LsRemoteRequest.class)
                .withRemoteUrl("https://bitbucket.org/exoinvitemain/privater.git")
                .withUseAuthorization(false));
    }
}
