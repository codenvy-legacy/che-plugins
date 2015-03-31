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

import static org.testng.Assert.assertEquals;

import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.shared.AddRequest;
import org.eclipse.che.ide.ext.git.shared.CommitRequest;
import org.eclipse.che.ide.ext.git.shared.Revision;
import com.google.common.collect.ImmutableList;

import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @author Eugene Voevodin
 */
public class CommitTest extends BaseTest {

    @Test
    public void testSimpleCommit() throws GitException, IOException {
        //add new File
        addFile(getRepository(), "DONTREADME", "secret");
        //add changes
        getConnection().add(newDTO(AddRequest.class).withFilepattern(AddRequest.DEFAULT_PATTERN));
        CommitRequest commitRequest = newDTO(CommitRequest.class)
                .withMessage("Commit message").withAmend(false).withAll(false);
        Revision revision = getConnection().commit(commitRequest);
        assertEquals(revision.getMessage(), commitRequest.getMessage());
    }

    @Test
    public void testCommitWithAddAll() throws GitException, IOException {
        //given
        addFile(getRepository(), "README.txt", CONTENT);
        getConnection().add(newDTO(AddRequest.class).withFilepattern(ImmutableList.of("README.txt")));
        getConnection().commit(newDTO(CommitRequest.class).withMessage("Initial addd"));
        //when
        //change existing README
        addFile(getRepository(), "README.txt", "not secret");
        //then
        CommitRequest commitRequest = newDTO(CommitRequest.class)
                .withMessage("Other commit message").withAmend(false).withAll(true);
        Revision revision = getConnection().commit(commitRequest);
        assertEquals(revision.getMessage(), commitRequest.getMessage());
    }

    @Test
    public void testAmendCommit() throws GitException, IOException {
        //given
        addFile(getRepository(), "README.txt", CONTENT);
        getConnection().add(newDTO(AddRequest.class).withFilepattern(ImmutableList.of("README.txt")));
        getConnection().commit(newDTO(CommitRequest.class).withMessage("Initial addd"));
        int beforeCount = getCountOfCommitsInCurrentBranch(getRepository().toFile());
        //when

        //change existing README
        addFile(getRepository(), "README.txt", "some new content");
        CommitRequest commitRequest = newDTO(CommitRequest.class)
                .withMessage("Amend commit").withAmend(true).withAll(true);

        //then
        Revision revision = getConnection().commit(commitRequest);
        int afterCount = getCountOfCommitsInCurrentBranch(getRepository().toFile());
        assertEquals(revision.getMessage(), commitRequest.getMessage());
        assertEquals(beforeCount, afterCount);
    }
}
