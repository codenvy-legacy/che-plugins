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
import org.eclipse.che.ide.ext.git.shared.CommitRequest;
import org.eclipse.che.ide.ext.git.shared.ResetRequest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugene Voevodin
 */
public class ResetTest extends BaseTest {
    @Test
    public void testResetHard() throws Exception {
        //given
        NativeGit git = new NativeGit(getRepository().toFile());
        File aaa = addFile(getRepository(), "aaa", "aaa\n");
        FileOutputStream fos = new FileOutputStream(new File(getRepository().toFile(), "README.txt"));
        fos.write("MODIFIED\n".getBytes());
        fos.flush();
        fos.close();
        String initMessage = git.createLogCommand().setCount(1).execute().get(0).getMessage();
        getConnection().add(newDTO(AddRequest.class).withFilepattern(new ArrayList<>(Arrays.asList("."))));
        getConnection().commit(newDTO(CommitRequest.class).withMessage("add file"));
        //when
        ResetRequest resetRequest = newDTO(ResetRequest.class).withCommit("HEAD^");
        resetRequest.setType(ResetRequest.ResetType.HARD);
        getConnection().reset(resetRequest);
        //then
        assertEquals(git.createLogCommand().setCount(1).execute().get(0).getMessage(), initMessage);
        assertFalse(aaa.exists());
        checkNotCached(getRepository().toFile(), "aaa");
        assertEquals(CONTENT, readFile(new File(getRepository().toFile(), "README.txt")));
    }

    @Test
    public void testResetSoft() throws Exception {
        NativeGit git = new NativeGit(getRepository().toFile());
        File aaa = addFile(getRepository(), "aaa", "aaa\n");
        FileOutputStream fos = new FileOutputStream(new File(getRepository().toFile(), "README.txt"));
        fos.write("MODIFIED\n".getBytes());
        fos.flush();
        fos.close();
        String initMessage = git.createLogCommand().setCount(1).execute().get(0).getMessage();
        getConnection().add(newDTO(AddRequest.class).withFilepattern(new ArrayList<>(Arrays.asList("."))));
        getConnection().commit(newDTO(CommitRequest.class).withMessage("add file"));
        //when
        ResetRequest resetRequest = newDTO(ResetRequest.class).withCommit("HEAD^");
        resetRequest.setType(ResetRequest.ResetType.SOFT);
        getConnection().reset(resetRequest);
        //then
        assertEquals(git.createLogCommand().setCount(1).execute().get(0).getMessage(), initMessage);
        assertTrue(aaa.exists());
        checkCached(getRepository().toFile(), "aaa");
        assertEquals(readFile(new File(getRepository().toFile(), "README.txt")), "MODIFIED\n");
    }

    @Test
    public void testResetMixed() throws Exception {
        //given
        NativeGit git = new NativeGit(getRepository().toFile());
        File aaa = addFile(getRepository(), "aaa", "aaa\n");
        FileOutputStream fos = new FileOutputStream(new File(getRepository().toFile(), "README.txt"));
        fos.write("MODIFIED\n".getBytes());
        fos.flush();
        fos.close();
        String initMessage = git.createLogCommand().setCount(1).execute().get(0).getMessage();
        getConnection().add(newDTO(AddRequest.class).withFilepattern(new ArrayList<>(Arrays.asList("."))));
        getConnection().commit(newDTO(CommitRequest.class).withMessage("add file"));
        //when
        ResetRequest resetRequest = newDTO(ResetRequest.class).withCommit("HEAD^");
        resetRequest.setType(ResetRequest.ResetType.MIXED);
        getConnection().reset(resetRequest);
        //then
        assertEquals(git.createLogCommand().setCount(1).execute().get(0).getMessage(), initMessage);
        assertTrue(aaa.exists());
        checkNotCached(getRepository().toFile(), "aaa");
        assertEquals(readFile(new File(getRepository().toFile(), "README.txt")), "MODIFIED\n");
    }
}
