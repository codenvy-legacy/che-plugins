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
import org.eclipse.che.api.core.util.LineConsumerFactory;
import org.eclipse.che.ide.ext.git.server.GitConnection;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.shared.CloneRequest;
import org.eclipse.che.ide.ext.git.shared.PullRequest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugene Voevodin
 */
public class PullTest extends BaseTest {

    @Test
    public void testSimplePull() throws IOException, ServerException, URISyntaxException, UnauthorizedException {
        //given
        //create new repository clone of default
        File repo2 = new File(getTarget().toAbsolutePath().toString(), "repo2");
        repo2.mkdir();
        forClean.add(repo2);
        GitConnection connection = connectionFactory.getConnection(repo2, getUser(), LineConsumerFactory.NULL);
        connection.clone(newDTO(CloneRequest.class)
                .withRemoteUri(getRepository().toAbsolutePath().toString()));
        addFile(getRepository(), "newfile1", "new file1 content");
        NativeGit nGit = new NativeGit(getRepository().toFile());
        nGit.createAddCommand().setFilePattern(Arrays.asList(".")).execute();
        nGit.createCommitCommand().setMessage("Test commit").setCommitter(getUser()).execute();
        //when
        connection.pull(newDTO(PullRequest.class).withRemote("origin").withTimeout(-1));
        //then
        assertTrue(new File(repo2.getAbsolutePath(), "newfile1").exists());
    }


    @Test
    public void testPullWithRefSpec()
            throws ServerException, URISyntaxException, IOException, UnauthorizedException {
        //given
        //create new repository clone of default
        File repo2 = new File(getTarget().toAbsolutePath().toString(), "repo2");
        repo2.mkdir();
        forClean.add(repo2);
        GitConnection connection = connectionFactory.getConnection(repo2, getUser(), LineConsumerFactory.NULL);
        connection.clone(newDTO(CloneRequest.class)
                .withRemoteUri(getRepository().toAbsolutePath().toString())
                .withWorkingDir(repo2.getAbsolutePath()));
        //add new branch
        NativeGit nGit = new NativeGit(getRepository().toFile());
        nGit.createBranchCheckoutCommand().setBranchName("b1").setCreateNew(true).execute();
        addFile(getRepository(), "newfile1", "new file1 content");
        nGit.createAddCommand().setFilePattern(Arrays.asList(".")).execute();
        nGit.createCommitCommand().setMessage("Test commit").setCommitter(getUser()).execute();
        int branchesBefore = new NativeGit(repo2).createBranchListCommand().execute().size();
        //when
        connection.pull(newDTO(PullRequest.class)
                        .withRemote("origin")
                        .withRefSpec("refs/heads/b1:refs/heads/b1")
                        .withTimeout(-1));
        int branchesAfter = new NativeGit(repo2).createBranchListCommand().execute().size();
        assertEquals(branchesAfter, branchesBefore + 1);
    }

    @Test
    public void testPullRemote()
            throws GitException, IOException, URISyntaxException, UnauthorizedException {
        //given
        String branchName = "remoteBranch";
        NativeGit sourceGit = new NativeGit(getRepository().toFile());
        sourceGit.createBranchCheckoutCommand().setCreateNew(true).setBranchName(branchName).execute();
        addFile(getRepository(), "remoteFile", "");
        sourceGit.createAddCommand().setFilePattern(Arrays.asList(".")).execute();
        sourceGit.createCommitCommand().setMessage("remote test").setCommitter(getUser()).execute();

        File newRepo = new File(getTarget().toAbsolutePath().toString(), "newRepo");
        newRepo.mkdir();
        forClean.add(newRepo);
        NativeGit newRepoGit = new NativeGit(newRepo);
        newRepoGit.createInitCommand().execute();
        addFile(newRepo.toPath(), "EMPTY", "");
        newRepoGit.createAddCommand().setFilePattern(Arrays.asList(".")).execute();
        newRepoGit.createCommitCommand().setMessage("init").setCommitter(getUser()).execute();

        //when
        PullRequest request = newDTO(PullRequest.class);
        request.setRemote(getRepository().toAbsolutePath().toString());
        request.setRefSpec(branchName);
        connectionFactory.getConnection(newRepo, getUser(), LineConsumerFactory.NULL).pull(request);
        //then
        assertTrue(new File(newRepo.getAbsolutePath(), "remoteFile").exists());
    }
}