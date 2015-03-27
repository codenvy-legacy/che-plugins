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
import org.eclipse.che.ide.ext.git.shared.GitUser;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Eugene Voevodin
 */
public class GetCommitersTest extends BaseTest {

    @Test
    public void testGetCommitters() throws IOException, GitException {
        //given
        NativeGit defGit = new NativeGit(getRepository().toFile());
        addFile(getRepository(), "newfile", "newfile content");
        defGit.createAddCommand().setFilePattern(Arrays.asList(".")).execute();
        defGit.createCommitCommand().setMessage("test commit").setCommitter(
                newDTO(GitUser.class).withName("Chuck Norris").withEmail("gmail@chucknorris.com")
        ).execute();
        //when
        List<GitUser> committers = getConnection().getCommiters();
        //then
        assertEquals(committers.size(), 2, "There is 2 committers, repository owner and Chuck");
        assertEquals(committers.get(0).getEmail(), "gmail@chucknorris.com");
        assertEquals(committers.get(0).getName(), "Chuck Norris");
    }
}
