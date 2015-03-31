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

import org.eclipse.che.ide.ext.git.server.GitException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author Eugene Voevodin
 */
public class RemoteDeleteTest extends BaseTest {

    @Test
    public void testRemoteDelete() throws GitException {
        NativeGit nativeGit = new NativeGit(getRepository().toFile());
        nativeGit.createRemoteAddCommand()
                .setName("origin")
                .setUrl("host.com:username/Repo.git")
                .execute();
        //now it is 1 remote
        assertEquals(nativeGit.createRemoteListCommand().execute().size(), 1);
        //try delete not existing remote
        try {
            getConnection().remoteDelete("donotexists");
            fail("should be exception");
        } catch (GitException ignored) {
        }
        getConnection().remoteDelete("origin");
        //now it is 0 remotes
        assertEquals(nativeGit.createRemoteListCommand().execute().size(), 0);
    }
}
