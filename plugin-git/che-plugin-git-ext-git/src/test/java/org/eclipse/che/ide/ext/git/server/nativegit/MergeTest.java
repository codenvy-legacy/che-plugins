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


import org.eclipse.che.ide.ext.git.shared.AddRequest;
import org.eclipse.che.ide.ext.git.shared.BranchCreateRequest;
import org.eclipse.che.ide.ext.git.shared.CommitRequest;
import org.eclipse.che.ide.ext.git.shared.MergeRequest;
import org.eclipse.che.ide.ext.git.shared.MergeResult;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugene Voevodin
 */
public class MergeTest extends BaseTest {
    private String branchName = "MergeTestBranch";

    @Test
    public void testMergeNoChanges() throws Exception {
        //given
        getConnection().branchCreate(newDTO(BranchCreateRequest.class).withName(branchName));
        //when
        MergeResult mergeResult = getConnection().merge(newDTO(MergeRequest.class).withCommit(branchName));
        //then
        assertEquals(mergeResult.getMergeStatus(), MergeResult.MergeStatus.ALREADY_UP_TO_DATE);
    }

    @Test
    public void testMerge() throws Exception {
        //given
        NativeGit git = new NativeGit(getRepository().toFile());
        git.createBranchCheckoutCommand().setBranchName(branchName).setCreateNew(true).execute();
        File file = addFile(getRepository(), "t-merge", "aaa\n");

        getConnection().add(newDTO(AddRequest.class).withFilepattern(new ArrayList<>(Arrays.asList("."))));
        getConnection().commit(newDTO(CommitRequest.class).withMessage("add file in new branch"));
        git.createBranchCheckoutCommand().setBranchName("master").execute();
        //when
        MergeResult mergeResult = getConnection().merge(newDTO(MergeRequest.class).withCommit(branchName));
        //then
        assertEquals(mergeResult.getMergeStatus(), MergeResult.MergeStatus.FAST_FORWARD);
        assertTrue(file.exists());
        assertEquals(readFile(file), "aaa\n");
        assertEquals(git.createLogCommand().setCount(1).execute().get(0).getMessage(), "add file in new branch");
    }

    @Test
    public void testMergeConflict() throws Exception {
        //given
        NativeGit git = new NativeGit(getRepository().toFile());

        git.createBranchCheckoutCommand().setBranchName(branchName).setCreateNew(true).execute();
        addFile(getRepository(), "t-merge-conflict", "aaa\n");
        getConnection().add(newDTO(AddRequest.class).withFilepattern(new ArrayList<>(Arrays.asList("."))));
        getConnection().commit(newDTO(CommitRequest.class).withMessage("add file in new branch"));

        git.createBranchCheckoutCommand().setBranchName("master").execute();
        addFile(getRepository(), "t-merge-conflict", "bbb\n");
        getConnection().add(newDTO(AddRequest.class).withFilepattern(new ArrayList<>(Arrays.asList("."))));
        getConnection().commit(newDTO(CommitRequest.class).withMessage("add file in new branch"));
        //when
        MergeResult mergeResult = getConnection().merge(newDTO(MergeRequest.class).withCommit(branchName));
        //then
        List<String> conflicts = mergeResult.getConflicts();
        assertEquals(conflicts.size(), 1);
        assertEquals(conflicts.get(0), "t-merge-conflict");

        assertEquals(mergeResult.getMergeStatus(), MergeResult.MergeStatus.CONFLICTING);

        String expContent = "<<<<<<< HEAD\n" //
                + "bbb\n" //
                + "=======\n" //
                + "aaa\n" //
                + ">>>>>>> MergeTestBranch\n";
        String actual = readFile(new File(getRepository().toFile(), "t-merge-conflict"));
        assertEquals(actual, expContent);
    }
//        TODO Uncomment as soon as IDEX-1776 is fixed
//    @Test
//    public void testFailed() throws GitException, IOException {
//        //given
//        NativeGit git = new NativeGit(getRepository().toFile());
//        git.createBranchCheckoutCommand().setBranchName(branchName).setCreateNew(true).execute();
//        addFile(getRepository(), "t-merge-failed", "aaa\n");
//        getConnection().add(newDTO(AddRequest.class).withFilepattern(new ArrayList<>(Arrays.asList("."))));
//        getConnection().commit(newDTO(CommitRequest.class).withMessage("add file in new branch"));
//        git.createBranchCheckoutCommand().setBranchName("master").execute();
//        addFile(getRepository(), "t-merge-failed", "bbb\n");
//        //when
//        MergeResult mergeResult = getConnection().merge(newDTO(MergeRequest.class).withCommit(branchName));
//        //then
//        assertEquals(mergeResult.getMergeStatus(), MergeResult.MergeStatus.FAILED);
//        assertEquals(mergeResult.getFailed().size(), 1);
//        assertEquals(mergeResult.getFailed().get(0), "t-merge-failed");
//    }
}
