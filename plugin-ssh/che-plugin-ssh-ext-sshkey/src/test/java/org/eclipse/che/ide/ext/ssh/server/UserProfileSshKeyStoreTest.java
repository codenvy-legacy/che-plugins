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
package org.eclipse.che.ide.ext.ssh.server;

import com.google.common.collect.ImmutableMap;

import org.eclipse.che.api.user.shared.dto.ProfileDescriptor;
import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Anton Korneta
 */
@RunWith(MockitoJUnitRunner.class)
public class UserProfileSshKeyStoreTest {

    private static final String API_ENDPOINT = "http://0.0.0.0:2375";

    private UserProfileSshKeyStore sshKeyStore;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldGetThePrivateKeyFromUsersPreference() throws Exception {
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        final String privateKey = "private key";
        final String publicKey = "public key";
        doReturn(ImmutableMap.of("ssh.key.private." + privateKey, "ssh.key.public." + publicKey)).when(sshKeyStore).getSshKeys();
        doNothing().when(sshKeyStore).updateSshKeys(any());

        Set<String> result = sshKeyStore.getAll();

        assertTrue(result.contains(privateKey));
        assertTrue(result.size() == 1);
    }

    @Test
    public void shouldThrowExceptionWhenGettingKeysFromUsersPreferences() throws Exception {
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        doThrow(IOException.class).when(sshKeyStore).getSshKeys();

        thrown.expect(SshKeyStoreException.class);
        thrown.expectMessage(new StringContains("Failed to get keys"));

        sshKeyStore.getAll();
    }

    @Test
    public void shouldThrowExceptionWhenNoKeysInUsersPreferences() throws Exception {
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        doThrow(IOException.class).when(sshKeyStore).getSshKeys();

        thrown.expect(SshKeyStoreException.class);
        thrown.expectMessage(new StringContains("Failed to get keys "));

        sshKeyStore.getAll();
    }

    @Test
    public void shouldGenerateSshKeysAndPutItInUserPreferences() throws Exception {
        final String host = "github.com";
        final String emailAttr = "email";
        Map<String, String> keys = new HashMap<>();
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        ProfileDescriptor profile = mock(ProfileDescriptor.class);
        doReturn(profile).when(sshKeyStore).getUserProfile();
        doReturn(keys).when(sshKeyStore).getSshKeys();
        when(profile.getAttributes()).thenReturn(ImmutableMap.of(emailAttr, emailAttr));
        doNothing().when(sshKeyStore).updateSshKeys(any());

        SshKeyPair result = sshKeyStore.genKeyPair(host, null, null);

        assertEquals(result.getPrivateKey().getIdentifier(), "ssh.key.private." + host);
        assertEquals(result.getPublicKey().getIdentifier(), "ssh.key.public." + host);
    }

    @Test
    public void shouldFailWhenImpossibleGetUsersProfileDescription() throws Exception {
        final String host = "github.com";
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        doThrow(IOException.class).when(sshKeyStore).getUserProfile();

        thrown.expect(SshKeyStoreException.class);
        thrown.expectMessage(new StringContains("Failed to generate keys for'" + host + "'."));

        sshKeyStore.genKeyPair(host, null, null);
    }

    @Test
    public void shouldFailWhenPrivateKeyAlreadyExist() throws Exception {
        final String host = "github.com";
        final String privateKey = "private key";
        Map<String, String> keys = new HashMap<>();
        keys.put("ssh.key.private.github.com", privateKey);
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        ProfileDescriptor profile = mock(ProfileDescriptor.class);
        doReturn(profile).when(sshKeyStore).getUserProfile();
        doReturn(keys).when(sshKeyStore).getSshKeys();

        thrown.expect(SshKeyStoreException.class);
        thrown.expectMessage(new StringContains("Private ssh key for host: '" + host + "' already exists."));

        sshKeyStore.genKeyPair(host, null, null);
    }

    @Test
    public void shouldFailWhenPublicKeyAlreadyExist() throws Exception {
        final String host = "github.com";
        final String publicKey = "private key";
        Map<String, String> keys = new HashMap<>();
        keys.put("ssh.key.public.github.com", publicKey);
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        ProfileDescriptor profile = mock(ProfileDescriptor.class);
        doReturn(profile).when(sshKeyStore).getUserProfile();
        doReturn(keys).when(sshKeyStore).getSshKeys();

        thrown.expect(SshKeyStoreException.class);
        thrown.expectMessage(new StringContains("Public ssh key for host: '" + host + "' already exists."));

        sshKeyStore.genKeyPair(host, null, null);
    }

    @Test
    public void shouldFailWhenUpdatingKeysInUsersPreferences() throws Exception {
        final String host = "github.com";
        final String emailAttr = "email";
        Map<String, String> keys = new HashMap<>();
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        ProfileDescriptor profile = mock(ProfileDescriptor.class);
        doReturn(profile).when(sshKeyStore).getUserProfile();
        doReturn(keys).when(sshKeyStore).getSshKeys();
        when(profile.getAttributes()).thenReturn(ImmutableMap.of(emailAttr, emailAttr));
        doThrow(IOException.class).when(sshKeyStore).updateSshKeys(any());

        thrown.expect(SshKeyStoreException.class);
        thrown.expectMessage(new StringContains("Failed to generate keys for'" + host + "'."));

        sshKeyStore.genKeyPair(host, null, null);
    }

    @Test
    public void shouldRemoveKeysFromUsersPreferences() throws Exception {
        final String host = "github.com";
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        doNothing().when(sshKeyStore).removeSshKeys(any());

        sshKeyStore.removeKeys(host);

        verify(sshKeyStore, times(1)).removeSshKeys(any());
    }

    @Test
    public void shouldFailRemovedKeysWhenImpossibleUpdateUsersPreference() throws Exception {
        final String host = "github.com";
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        doReturn(Collections.emptyMap()).when(sshKeyStore).getSshKeys();
        doThrow(IOException.class).when(sshKeyStore).updateSshKeys(any());

        thrown.expect(SshKeyStoreException.class);
        thrown.expectMessage(new StringContains("Failed to remove ssh keys for host '" + host + "'"));

        sshKeyStore.removeKeys(host);
    }

    @Test
    public void shouldAddPrivateKeyInUsersPreferences() throws Exception {
        final String host = "github.com";
        final String sshKey = "private ssh key";
        @SuppressWarnings("unchecked")
        Map<String, String> keys = mock(Map.class);
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        doReturn(keys).when(sshKeyStore).getSshKeys();
        doNothing().when(sshKeyStore).updateSshKeys(any());

        sshKeyStore.addPrivateKey(host, sshKey.getBytes());

        verify(keys, times(1)).put(anyString(), anyString());
    }

    @Test
    public void shouldFailWhenUpdatingPrivateSshKey() throws Exception {
        final String host = "github.com";
        final String sshKey = "private ssh key";
        sshKeyStore = spy(new UserProfileSshKeyStore(API_ENDPOINT));
        doReturn(new HashMap<>()).when(sshKeyStore).getSshKeys();
        doThrow(IOException.class).when(sshKeyStore).updateSshKeys(any());

        thrown.expect(SshKeyStoreException.class);
        thrown.expectMessage(new StringContains("Failed to add private key for host '" + host + "'."));

        sshKeyStore.addPrivateKey(host, sshKey.getBytes());
    }
}
