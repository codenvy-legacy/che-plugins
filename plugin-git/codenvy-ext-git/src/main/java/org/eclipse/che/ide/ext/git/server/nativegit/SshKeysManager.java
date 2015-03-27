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
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.server.commons.Util;
import org.eclipse.che.ide.ext.ssh.server.SshKey;
import org.eclipse.che.ide.ext.ssh.server.SshKeyPair;
import org.eclipse.che.ide.ext.ssh.server.SshKeyStore;
import org.eclipse.che.ide.ext.ssh.server.SshKeyStoreException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

/**
 * Writes SSH key into file.
 *
 * @author Eugene Voevodin
 */
@Singleton
public class SshKeysManager {

    private static final Logger LOG = LoggerFactory.getLogger(SshKeysManager.class);

    private static final String DEFAULT_KEY_DIRECTORY_PATH = System.getProperty("java.io.tmpdir");
    private static final String DEFAULT_KEY_NAME           = "identity";

    //used in tests
    static String keyDirectoryPath; // TODO(GUICE): initialize

    private final SshKeyStore         sshKeyStore;
    private final Set<SshKeyUploader> sshKeyUploaders;

    @Inject
    public SshKeysManager(SshKeyStore sshKeyStore, Set<SshKeyUploader> sshKeyUploaders) {
        this.sshKeyStore = sshKeyStore;
        this.sshKeyUploaders = sshKeyUploaders;
    }

    public static String getKeyDirectoryPath() {
        return (keyDirectoryPath == null ? DEFAULT_KEY_DIRECTORY_PATH : keyDirectoryPath) + '/'
               + EnvironmentContext.getCurrent().getUser().getName();
    }

    /**
     * Writes SSH key into file.
     *
     * @param url
     *         SSH url to git repository
     * @return file that contains SSH key
     * @throws IllegalArgumentException
     *         if specified URL is not SSH url
     * @throws GitException
     *         if other error occurs
     */
    public File writeKeyFile(String url) throws GitException {
        final String host = Util.getHost(url);
        if (host == null) {
            throw new IllegalArgumentException(String.format("Unable get host name from %s. Probably isn't a SSH URL", url));
        }

        SshKey publicKey;
        SshKey privateKey;

        // check keys existence and generate if need
        try {
            if ((privateKey = sshKeyStore.getPrivateKey(host)) != null) {
                publicKey = sshKeyStore.getPublicKey(host);
                if (publicKey == null) {
                    sshKeyStore.removeKeys(host);
                    SshKeyPair sshKeyPair = sshKeyStore.genKeyPair(host, null, null);
                    publicKey = sshKeyPair.getPublicKey();
                    privateKey = sshKeyPair.getPrivateKey();
                }
            } else {
                SshKeyPair sshKeyPair = sshKeyStore.genKeyPair(host, null, null);
                publicKey = sshKeyPair.getPublicKey();
                privateKey = sshKeyPair.getPrivateKey();
            }
        } catch (SshKeyStoreException e) {
            throw new GitException(e.getMessage(), e);
        }

        // create directories if need
        final File keyDirectory = new File(getKeyDirectoryPath(), host);
        if (!keyDirectory.exists()) {
            keyDirectory.mkdirs();
        }

        // save private key in local file
        final File keyFile = new File(getKeyDirectoryPath() + '/' + host + '/' + DEFAULT_KEY_NAME);
        try (FileOutputStream fos = new FileOutputStream(keyFile)) {
            fos.write(privateKey.getBytes());
        } catch (IOException e) {
            LOG.error("Cant store key", e);
            throw new GitException("Cant store ssh key. ");
        }

        //set perm to -r--r--r--
        keyFile.setReadOnly();
        //set perm to ----------
        keyFile.setReadable(false, false);
        //set perm to -r--------
        keyFile.setReadable(true, true);
        //set perm to -rw-------
        keyFile.setWritable(true, true);

        SshKeyUploader uploader = null;

        for (Iterator<SshKeyUploader> itr = sshKeyUploaders.iterator(); uploader == null && itr.hasNext(); ) {
            SshKeyUploader next = itr.next();
            if (next.match(url)) {
                uploader = next;
            }
        }

        if (uploader != null) {
            // upload public key
            try {
                uploader.uploadKey(publicKey);
            } catch (IOException e) {
                throw new GitException(e.getMessage(), e);
            } catch (UnauthorizedException e) {
                // Git action might fail without uploaded public SSH key.
                LOG.warn(String.format("Unable upload public SSH key with %s", uploader.getClass().getSimpleName()), e);
            }
        } else {
            // Git action might fail without SSH key.
            LOG.warn(String.format("Not found ssh key uploader for %s", host));
        }

        return keyFile;
    }

    /**
     *
     * /home/jumper/code/plugin-git/codenvy-ext-git/target/ssh-keys/host.com/codenvy/identity
     * /home/jumper/code/plugin-git/codenvy-ext-git/target/ssh-keys/codenvy/host.com/identity
     *
     * Removes ssh key from file system.
     * If ssh key doesn't exist - nothing will be done.
     * <p/>
     * This method should be used with remote git commands
     * to clean up ssh keys from filesystem after it executions
     *
     * @param url
     *         url which is used to fetch host from it
     * @throws IllegalArgumentException
     *         when it is not possible to fetch host from {@code url}
     * @see Util#getHost(String)
     */
    public void removeKey(String url) {
        final String host = Util.getHost(url);
        if (host == null) {
            throw new IllegalArgumentException(String.format("Unable get host name from %s. Probably isn't a SSH URL", url));
        }
        final Path key = Paths.get(getKeyDirectoryPath())
                              .resolve(host)
                              .resolve(DEFAULT_KEY_NAME);

        if (!Files.exists(key)) return;

        try {
            Files.delete(key);
        } catch (IOException e) {
            LOG.error("It is not possible to remove ssh key {}", key);
        }
    }
}
