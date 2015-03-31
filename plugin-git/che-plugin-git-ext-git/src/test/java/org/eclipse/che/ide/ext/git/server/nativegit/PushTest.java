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

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.ext.git.server.GitConnection;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.shared.CloneRequest;
import org.eclipse.che.ide.ext.git.shared.PushRequest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.eclipse.che.api.core.util.LineConsumerFactory.NULL;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugene Voevodin
 */
public class PushTest extends BaseTest {

    @Test
    public void testSimplePush() throws IOException, ServerException, URISyntaxException, UnauthorizedException {
        File pushTo = new File(getTarget().toAbsolutePath().toString(), "repo2");
        pushTo.mkdir();
        forClean.add(pushTo);
        GitConnection connection = connectionFactory.getConnection(pushTo, getUser(), NULL);
        connection.clone(newDTO(CloneRequest.class).withRemoteUri(getRepository().toAbsolutePath().toString()));
        addFile(pushTo.toPath(), "newfile", "content");
        NativeGit pushToGit = new NativeGit(pushTo);
        pushToGit.createAddCommand().setFilePattern(Arrays.asList(".")).execute();
        pushToGit.createCommitCommand().setMessage("Fake commit").setCommitter(getUser()).execute();
        //make push
        connection.push(newDTO(PushRequest.class)
                .withRefSpec(Arrays.asList("refs/heads/master:refs/heads/test"))
                .withRemote("origin")
                .withTimeout(-1));
        //check branches in origin repository
        NativeGit originGit = new NativeGit(getRepository().toFile());
        assertEquals(originGit.createBranchListCommand().execute().size(), 2);
        //checkout test branch
        originGit.createBranchCheckoutCommand().setBranchName("test").execute();
        assertTrue(new File(getRepository().toFile(), "newfile").exists());
    }

    @Test
    public void testPushRemote() throws GitException, IOException, URISyntaxException, UnauthorizedException {
        File remoteRepo = new File(getTarget().toAbsolutePath().toString(), "remoteRepo");
        remoteRepo.mkdir();
        forClean.add(remoteRepo);
        NativeGit remoteGit = new NativeGit(remoteRepo);
        remoteGit.createInitCommand().execute();
        addFile(remoteRepo.toPath(), "README", "README");
        remoteGit.createAddCommand().setFilePattern(Arrays.asList(".")).execute();
        remoteGit.createCommitCommand().setMessage("Init commit.").setCommitter(getUser()).execute();
        //make push
        int branchesBefore = remoteGit.createBranchListCommand().execute().size();
        getConnection().push(newDTO(PushRequest.class).withRefSpec(Arrays.asList("refs/heads/master:refs/heads/test"))
                .withRemote(remoteRepo.getAbsolutePath()).withTimeout(-1));
        int branchesAfter = remoteGit.createBranchListCommand().execute().size();
        assertEquals(branchesAfter - 1, branchesBefore);
    }
}
