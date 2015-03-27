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
import org.eclipse.che.ide.ext.git.server.nativegit.commands.BranchListCommand;
import org.eclipse.che.ide.ext.git.shared.AddRequest;
import org.eclipse.che.ide.ext.git.shared.BranchCheckoutRequest;
import org.eclipse.che.ide.ext.git.shared.BranchCreateRequest;
import org.eclipse.che.ide.ext.git.shared.CommitRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

/**
 * @author Eugene Voevodin
 */
public class BranchCheckoutTest extends BaseTest {
    private static final String FIRST_BRANCH_NAME = "firstBranch";
    private static final String SECOND_BRANCH_NAME = "secondBranch";

    @BeforeMethod
    public void addBranchWithCommit() throws Exception {
        getConnection().branchCreate(newDTO(BranchCreateRequest.class).withName(FIRST_BRANCH_NAME));
        getConnection().branchCheckout(newDTO(BranchCheckoutRequest.class).withName(FIRST_BRANCH_NAME));
        addFile(getRepository(), "newfile", "new file content");
        getConnection().add(newDTO(AddRequest.class).withFilepattern(Arrays.asList(".")));
        getConnection().commit(newDTO(CommitRequest.class).withMessage("Commit message"));
        getConnection().branchCheckout(newDTO(BranchCheckoutRequest.class).withName("master"));
    }

    @Test
    public void testSimpleCheckout() throws GitException, IOException {
        assertFalse(new File(getRepository().toFile(), "newf3ile").exists());
        getConnection().branchCheckout(newDTO(BranchCheckoutRequest.class).withName(FIRST_BRANCH_NAME));
        assertTrue(new File(getRepository().toFile(), "newfile").exists());
    }

    @Test
    public void testCreateNewAndCheckout() throws GitException {
        BranchListCommand blc = new NativeGit(getRepository().toFile()).createBranchListCommand();
        assertEquals(blc.execute().size(), 2);
        getConnection().branchCheckout(newDTO(BranchCheckoutRequest.class)
                .withName("thirdBranch")
                .withCreateNew(true));
        assertEquals(blc.execute().size(), 3);
    }

    @Test
    public void testCheckoutFromStartPoint() throws GitException {
        //given
        BranchListCommand blc = new NativeGit(getRepository().toFile()).createBranchListCommand();
        assertEquals(blc.execute().size(), 2);
        //when
        getConnection().branchCheckout(newDTO(BranchCheckoutRequest.class)
                .withName(SECOND_BRANCH_NAME)
                .withStartPoint(FIRST_BRANCH_NAME)
                .withCreateNew(true));
        //then
        assertEquals(blc.execute().size(), 3);
        assertTrue(new File(getRepository().toFile(), "newfile").exists());
    }
}
