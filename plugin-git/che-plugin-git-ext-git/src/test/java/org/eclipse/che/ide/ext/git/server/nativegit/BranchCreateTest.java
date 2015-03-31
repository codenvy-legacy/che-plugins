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
import org.eclipse.che.ide.ext.git.server.nativegit.commands.BranchCheckoutCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.BranchListCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.LogCommand;

import org.eclipse.che.ide.ext.git.shared.AddRequest;
import org.eclipse.che.ide.ext.git.shared.Branch;
import org.eclipse.che.ide.ext.git.shared.BranchCreateRequest;
import org.eclipse.che.ide.ext.git.shared.CommitRequest;
import org.eclipse.che.ide.ext.git.shared.Revision;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Eugene Voevodin
 */
public class BranchCreateTest extends BaseTest {

    @Test
    public void testSimpleBranchCreate() throws GitException {
        //given
        BranchListCommand branchListCommand = new BranchListCommand(getRepository().toFile());
        int beforeCountOfBranches = branchListCommand.execute().size();
        //when
        getConnection().branchCreate(newDTO(BranchCreateRequest.class).withName("new-branch"));
        //then
        int afterCountOfBranches = branchListCommand.execute().size();
        assertEquals(afterCountOfBranches, beforeCountOfBranches + 1);
    }

    @Test
    public void testBranchCreateWithStartPoint() throws IOException, GitException {
        //givenok
        //make 2 commits in default repository
        addFile(getRepository(), "newfile1", "file 1 content");
        getConnection().add(newDTO(AddRequest.class).withFilepattern(Arrays.asList(".")));
        getConnection().commit(newDTO(CommitRequest.class).withMessage("Commit message"));
        //change content
        addFile(getRepository(), "newfile1", "new file 1 content");
        getConnection().commit(newDTO(CommitRequest.class).withMessage("Commit message").withAll(true));
        //get list of master branch commits
        LogCommand log = new LogCommand(getRepository().toFile());
        List<Revision> revCommitList = log.execute();
        int beforeCheckoutCommitsCount = revCommitList.size();

        //when
        //create new branch to 2nd commit
        Branch branch = getConnection().branchCreate(newDTO(BranchCreateRequest.class)
                .withName("new-branch")
                .withStartPoint(revCommitList.get(1).getId()));
        //then
        BranchCheckoutCommand checkout = new BranchCheckoutCommand(getRepository().toFile());
        checkout.setBranchName(branch.getDisplayName()).execute();
        log.execute();
        int afterCheckoutCommitsCount = log.execute().size();
        assertEquals(afterCheckoutCommitsCount, beforeCheckoutCommitsCount - 1);
    }
}
