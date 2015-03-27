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
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.shared.Branch;
import org.eclipse.che.ide.ext.git.shared.BranchDeleteRequest;
import org.eclipse.che.ide.ext.git.shared.BranchListRequest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.eclipse.che.ide.ext.git.shared.BranchListRequest.LIST_LOCAL;

/**
 * @author Eugene Voevodin
 * @author Mihail Kuznyetsov
 */
public class BranchDeleteTest extends BaseTest {

    @Test
    public void testSimpleDelete() throws GitException, UnauthorizedException {
        //given
        new NativeGit(getRepository().toFile()).createBranchCreateCommand()
                .setBranchName("newbranch")
                .execute();
        validateBranchList(
                getConnection().branchList(newDTO(BranchListRequest.class).withListMode(LIST_LOCAL)),
                Arrays.asList(
                        newDTO(Branch.class).withName("refs/heads/master")
                                .withDisplayName("master").withActive(true).withRemote(false),
                        newDTO(Branch.class).withName("refs/heads/newbranch")
                                .withDisplayName("newbranch").withActive(false).withRemote(false)
                )
        );
        //when
        getConnection().branchDelete(newDTO(BranchDeleteRequest.class).withName("newbranch").withForce(false));
        //then
        validateBranchList(
                getConnection().branchList(newDTO(BranchListRequest.class).withListMode(LIST_LOCAL)),
                Arrays.asList(
                        newDTO(Branch.class).withName("refs/heads/master")
                                .withDisplayName("master").withActive(true).withRemote(false)
                )
        );
    }

    @Test
    public void shouldDeleteNotFullyMergedBranchWithForce()
            throws GitException, IOException, UnauthorizedException {
        //given
        NativeGit defaultGit = new NativeGit(getRepository().toFile());
        defaultGit.createBranchCheckoutCommand().setCreateNew(true).setBranchName("newbranch").execute();
        addFile(getRepository(), "newfile", "new file content");
        defaultGit.createAddCommand().setFilePattern(Arrays.asList(".")).execute();
        defaultGit.createCommitCommand().setMessage("second commit").setCommitter(getUser()).execute();
        defaultGit.createBranchCheckoutCommand().setBranchName("master").execute();
        //when
        getConnection().branchDelete(newDTO(BranchDeleteRequest.class).withName("newbranch").withForce(true));
        //then
        validateBranchList(
                getConnection().branchList(newDTO(BranchListRequest.class).withListMode(LIST_LOCAL)),
                Arrays.asList(
                        newDTO(Branch.class).withName("refs/heads/master")
                                .withDisplayName("master").withActive(true).withRemote(false)
                )
        );
    }

    @Test(expectedExceptions = GitException.class)
    public void shouldThrowExceptionOnDeletingNotFullyMergedBranchWithoutForce()
            throws GitException, IOException, UnauthorizedException, NoSuchFieldException, IllegalAccessException {
        NativeGit defaultGit = new NativeGit(getRepository().toFile());
        defaultGit.createBranchCheckoutCommand().setCreateNew(true).setBranchName("newbranch").execute();
        addFile(getRepository(), "newfile", "new file content");
        defaultGit.createAddCommand().setFilePattern(Arrays.asList(".")).execute();
        defaultGit.createCommitCommand().setMessage("second commit").setCommitter(getUser()).execute();
        defaultGit.createBranchCheckoutCommand().setBranchName("master").execute();

        getConnection().branchDelete(newDTO(BranchDeleteRequest.class).withName("newbranch").withForce(false));
    }
}
