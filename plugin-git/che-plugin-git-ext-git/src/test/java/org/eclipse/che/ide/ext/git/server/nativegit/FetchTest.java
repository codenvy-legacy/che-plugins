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
import org.eclipse.che.api.core.util.LineConsumerFactory;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.LogCommand;
import org.eclipse.che.ide.ext.git.shared.FetchRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugene Voevodin
 */
public class FetchTest extends BaseTest {

    private File fetchTestRepo;

    @BeforeMethod
    protected void cloneRepo() throws Exception {
        fetchTestRepo = new File(getTarget().toFile(), "fetchTestRepo");
        fetchTestRepo.mkdir();
        forClean.add(fetchTestRepo);
        //clone default repo into fetchRepo
        new NativeGit(fetchTestRepo).createCloneCommand()
                .setUri(getRepository().toAbsolutePath().toString())
                .execute();
        //add new File into defaultRepository
        addFile(getRepository(), "newfile1", "newfile1 content");
        //add file to index and make commit
        NativeGit defaultGit = new NativeGit(getRepository().toFile());
        defaultGit.createAddCommand().setFilePattern(Arrays.asList(".")).execute();
        defaultGit.createCommitCommand().setMessage("fetch test").setCommitter(getUser()).execute();
    }

    @Test
    public void testSimpleFetch() throws IOException, GitException, UnauthorizedException {
        //given
        FetchRequest request = newDTO(FetchRequest.class);
        request.setRemote(getRepository().toAbsolutePath().toString());
        //when
        connectionFactory.getConnection(fetchTestRepo, getUser(), LineConsumerFactory.NULL).fetch(request);
        //then
        //make merge with FETCH_HEAD
        new NativeGit(fetchTestRepo).createMergeCommand()
                .setCommit("FETCH_HEAD").setCommitter(getUser()).execute();
        assertTrue(new File(fetchTestRepo, "newfile1").exists());
    }

    @Test
    public void testFetchBranch() throws GitException, IOException, UnauthorizedException {
        //given
        String branchName = "branch";
        NativeGit defaultGit = new NativeGit(getRepository().toFile());
        defaultGit.createBranchCheckoutCommand()
                .setCreateNew(true)
                .setBranchName(branchName)
                .execute();
        addFile(getRepository(), "otherfile1", "otherfile1 content");
        addFile(getRepository(), "otherfile2", "otherfile2 content");
        defaultGit.createAddCommand().setFilePattern(Arrays.asList(".")).execute();
        defaultGit.createCommitCommand().setMessage("fetch branch test").setCommitter(getUser()).execute();

        FetchRequest request = newDTO(FetchRequest.class);
        request.setRemote(getRepository().toAbsolutePath().toString());
        request.setRefSpec(Arrays.asList(branchName));
        //when
        connectionFactory.getConnection(fetchTestRepo, getUser(), LineConsumerFactory.NULL).fetch(request);
        //then
        //make merge with FETCH_HEAD
        new NativeGit(fetchTestRepo).createMergeCommand()
                .setCommit("FETCH_HEAD").setCommitter(getUser()).execute();
        assertTrue(new File(fetchTestRepo, "otherfile1").exists());
        assertTrue(new File(fetchTestRepo, "otherfile2").exists());
        assertEquals(
                new LogCommand(fetchTestRepo).setCount(1).execute().get(0).getMessage(),
                "fetch branch test");
    }
}
