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

import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.ide.ext.ssh.server.SshKey;
import org.eclipse.che.ide.ext.ssh.server.SshKeyPair;
import org.eclipse.che.ide.ext.ssh.server.SshKeyStore;

import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.exists;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Eugene Voevodin
 */
@Listeners(MockitoTestNGListener.class)
public class SshKeysManagerTest extends BaseTest {

    @Mock
    SshKeyStore keyStore;

    @BeforeMethod
    public void prepareSshKeysDir() throws Exception {
        final Path keysDir = getKeysRoot();
        createDirectory(keysDir);
        SshKeysManager.keyDirectoryPath = keysDir.toString();
    }

    @AfterMethod
    public void removeSshKeysDirectory() throws Exception {
        IoUtil.removeDirectory(getKeysRoot().toString());
    }

    @Test
    public void testSshKeysManager() throws Exception {
        //given
        SshKey publicKey = new SshKey("public_key", "publicsshkey".getBytes());
        SshKey privateKey = new SshKey("private_key", "privatesshkey".getBytes());

        when(keyStore.genKeyPair("host.com", "comment", "password")).thenReturn(new SshKeyPair(publicKey, privateKey));
        when(keyStore.getPrivateKey(eq("host.com"))).thenReturn(privateKey);
        when(keyStore.getPublicKey(eq("host.com"))).thenReturn(publicKey);
        //generating key
        keyStore.genKeyPair("host.com", "comment", "password");
        //creating SshKeyManager with no uploaders

        SshKeysManager manager = new SshKeysManager(keyStore, new HashSet<SshKeyUploader>());

        //when
        File key = manager.writeKeyFile(DEFAULT_URI);
        //then
        assertEquals(readFile(key).getBytes(),
                     keyStore.getPrivateKey("host.com").getBytes());
        forClean.add(key);
    }

    @Test
    public void shouldBeAbleToRemoveStoredSSHKey() throws Exception {
        //prepare ssh key
        final Path sshKey = getKeysRoot().resolve(EnvironmentContext.getCurrent().getUser().getName())
                                         .resolve("host.com")
                                         .resolve("identity");
        createDirectories(sshKey.getParent());
        createFile(sshKey);

        final SshKeysManager manager = new SshKeysManager(keyStore, Collections.<SshKeyUploader>emptySet());

        manager.removeKey("git@host.com:codenvy");

        assertFalse(exists(sshKey));
    }

    @Test
    public void shouldDoNothingIfSshKeyDoesNotExist() {
        final SshKeysManager manager = new SshKeysManager(keyStore, Collections.<SshKeyUploader>emptySet());

        manager.removeKey("git@host.com:codenvy");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGivenUrlIsNotSSH() {
        final SshKeysManager manager = new SshKeysManager(keyStore, Collections.<SshKeyUploader>emptySet());

        manager.removeKey("host.com");
    }

    private Path getKeysRoot() throws URISyntaxException {
        return getTarget().resolve("ssh-keys");
    }
}
