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

import com.google.gson.reflect.TypeToken;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

import org.eclipse.che.api.core.ApiException;
import org.eclipse.che.api.core.rest.HttpJsonHelper;
import org.eclipse.che.api.user.shared.dto.ProfileDescriptor;
import org.eclipse.che.commons.json.JsonHelper;
import org.eclipse.che.commons.json.JsonParseException;
import org.eclipse.che.commons.lang.Pair;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages users ssh keys.
 *
 * @author andrew00x
 * @author Anton Korneta
 */
public class UserProfileSshKeyStore implements SshKeyStore {
    private static final int    PRIVATE                      = 0;
    private static final int    PUBLIC                       = 1;
    private static final String KEY_ATTRIBUTE_PREFIX         = "ssh.key.";
    // Prefix for attribute of user profile that store private SSH key.
    private static final String PRIVATE_KEY_ATTRIBUTE_PREFIX = KEY_ATTRIBUTE_PREFIX + "private.";
    // Prefix for attribute of user profile that store public SSH key.
    private static final String PUBLIC_KEY_ATTRIBUTE_PREFIX  = KEY_ATTRIBUTE_PREFIX + "public.";

    private final JSch   genJsch;
    private final String profileApiUrl;

    @Inject
    public UserProfileSshKeyStore(@Named("api.endpoint") String apiUrl) {
        this.genJsch = new JSch();
        this.profileApiUrl = apiUrl + "/profile";
    }

    /**
     * Adds the private key to the user preferences.
     *
     * @param host
     *         host name
     * @param key
     *         private key as byte array
     * @throws SshKeyStoreException
     *         occurs when a private key already exists,
     *         or problems with getting/updating existing keys
     */
    @Override
    public void addPrivateKey(String host, byte[] key) throws SshKeyStoreException {
        try {
            final String sshKeyAttributeName = sshKeyAttributeName(host, PRIVATE);
            final Map<String, String> keys = getSshKeys();
            if (keys.containsKey(sshKeyAttributeName)) {
                throw new SshKeyStoreException("Private key for host: '" + host + "' already exists.");
            }
            keys.put(sshKeyAttributeName, new String(key));
            updateSshKeys(keys);
        } catch (ApiException | IOException | JsonParseException e) {
            throw new SshKeyStoreException("Failed to add private key for host '" + host + "'. ", e);
        }
    }

    /**
     * Gets private key from the user preferences.
     *
     * @param host
     *         host name
     * @return private key
     * @throws SshKeyStoreException
     *         when any error occurs with getting key from user's preferences
     */
    @Override
    public SshKey getPrivateKey(String host) throws SshKeyStoreException {
        return getKey(host, PRIVATE);
    }

    /**
     * Gets public key from the user preferences.
     *
     * @param host
     *         host name
     * @return private key
     * @throws SshKeyStoreException
     *         when any error occurs with getting key from user's preferences
     */
    @Override
    public SshKey getPublicKey(String host) throws SshKeyStoreException {
        return getKey(host, PUBLIC);
    }

    /**
     * Generates a pair of keys.
     *
     * @param host
     *         host name
     * @param comment
     *         comment to add in public key
     * @param passPhrase
     *         optional pass-phrase to protect private key
     * @return pair of keys
     * @throws SshKeyStoreException
     *         either when a private/public key already exists,
     *         or when any problem related to generating keypair or getting/updating keys occurs
     */
    @Override
    public SshKeyPair genKeyPair(String host, String comment, String passPhrase) throws SshKeyStoreException {
        return genKeyPair(host, comment, passPhrase, null);
    }

    /**
     * Generates a pair of keys.
     *
     * @param host
     *         host name
     * @param comment
     *         comment to add in public key
     * @param passPhrase
     *         optional pass-phrase to protect private key
     * @param keyMail
     *         optional email for generated key
     * @return pair of keys
     * @throws SshKeyStoreException
     *         either when a private/public key already exists,
     *         or when any problem related to generating keypair or getting/updating keys occurs
     */
    @Override
    public SshKeyPair genKeyPair(String host, String comment, String passPhrase, String keyMail) throws SshKeyStoreException {
        try {
            final ProfileDescriptor userProfile = getUserProfile();
            final Map<String, String> keys = getSshKeys();

            if (keyMail == null) {
                keyMail = userProfile.getAttributes().getOrDefault("email", userProfile.getId());
            }
            final String sshPrivateKeyAttributeName = sshKeyAttributeName(host, PRIVATE);
            final String sshPublicKeyAttributeName = sshKeyAttributeName(host, PUBLIC);
            // Be sure keys are not created yet.
            if (keys.containsKey(sshPrivateKeyAttributeName)) {
                throw new SshKeyStoreException("Private key for host: '" + host + "' already exists.");
            }
            if (keys.containsKey(sshPublicKeyAttributeName)) {
                throw new SshKeyStoreException("Public key for host: '" + host + "' already exists.");
            }
            // Gen key pair.
            KeyPair keyPair = KeyPair.genKeyPair(genJsch, 2, 2048);
            ByteArrayOutputStream buff = new ByteArrayOutputStream();

            keyPair.writePrivateKey(buff, passPhrase == null ? null : passPhrase.getBytes(Charset.forName("UTF-8")));
            final SshKey privateKey = new SshKey(sshPrivateKeyAttributeName, buff.toByteArray());
            keys.put(sshPrivateKeyAttributeName, buff.toString("UTF-8"));

            buff.reset();
            comment = comment != null ? comment : (keyMail.indexOf('@') > 0 ? keyMail : (keyMail + "@ide.codenvy.local"));
            keyPair.writePublicKey(buff, comment);
            final SshKey publicKey = new SshKey(sshPublicKeyAttributeName, buff.toByteArray());
            keys.put(sshPublicKeyAttributeName, new String(buff.toByteArray()));

            updateSshKeys(keys);
            return new SshKeyPair(publicKey, privateKey);
        } catch (ApiException | IOException | JsonParseException | JSchException e) {
            throw new SshKeyStoreException("Failed to generate keys for'" + host + "'. ", e);
        }
    }

    /**
     * Removes the keys from the user preferences.
     *
     * @param host
     *         host name
     * @throws SshKeyStoreException
     *         when any error occurs with getting/updating existing keys
     */
    @Override
    public void removeKeys(String host) throws SshKeyStoreException {
        try {
            final Map<String, String> keys = getSshKeys();
            keys.remove(sshKeyAttributeName(host, PRIVATE));
            keys.remove(sshKeyAttributeName(host, PUBLIC));
            updateSshKeys(keys);
        } catch (ApiException | JsonParseException | IOException e) {
            throw new SshKeyStoreException("Failed to remove keys for host '" + host + "'.");
        }
    }

    /**
     * Gets list of hosts for which keys are available.
     *
     * @return list of hosts. Even there is no keys for any host empty set returned never <code>null</code>
     * @throws SshKeyStoreException
     *         when any errors occurs with getting existing keys
     */
    @Override
    public Set<String> getAll() throws SshKeyStoreException {
        try {
            Map<String, String> keys = getSshKeys();
            // Check only for private keys.
            return keys.keySet()
                       .stream()
                       .filter(str -> str.startsWith(PRIVATE_KEY_ATTRIBUTE_PREFIX))
                       .map(str -> str.substring(PRIVATE_KEY_ATTRIBUTE_PREFIX.length()))
                       .collect(Collectors.toSet());
        } catch (ApiException | IOException | JsonParseException e) {
            throw new SshKeyStoreException("Failed to get keys ", e);
        }
    }

    /**
     * Gets key from user's preference.
     *
     * @param host
     *         host name
     * @param isPrivate
     *         value that needs for indicating the key, <code>0</code> if key is private and <code>1</code> if key is public
     * @return public/private key
     * @throws SshKeyStoreException
     *         when any errors occurs with getting existing key
     */
    private SshKey getKey(String host, int isPrivate) throws SshKeyStoreException {
        try {
            Map<String, String> keys = getSshKeys();
            String keyIdentifier = sshKeyAttributeName(host, isPrivate);
            String keyAsString = keys.get(keyIdentifier);
            if (keyAsString == null) {
                // Try to find key for parent domain. This is required for openshift integration but may be useful for others also.
                final String attributePrefix = isPrivate == PRIVATE ? PRIVATE_KEY_ATTRIBUTE_PREFIX : PUBLIC_KEY_ATTRIBUTE_PREFIX;
                for (Map.Entry<String, String> entry : keys.entrySet()) {
                    String attributeName = entry.getKey();
                    keyAsString = entry.getValue();
                    if (attributeName.startsWith(attributePrefix)
                        && host.endsWith(attributeName.substring(attributePrefix.length()))
                        && keyAsString != null) {
                        // Lets say we found attribute 'ssh.key.private.codenvy.com'
                        // and we are looking for key for host 'my-site.codenvy.com'.
                        // 1. Get domain name - remove prefix 'ssh.key.private.'
                        // 2. We found the key if host name ends with name we got above.
                        return new SshKey(keyIdentifier, keyAsString.getBytes());
                    }
                }
            }
            return (keyAsString != null) ? new SshKey(keyIdentifier, keyAsString.getBytes()) : null;
        } catch (ApiException | IOException | JsonParseException e) {
            throw new SshKeyStoreException("Failed to get key for host '" + host + "'.", e);
        }
    }

    /**
     * Name of attribute of user profile to store SSH key.
     *
     * @param host
     *         host name
     * @param isPrivate
     *         <code>0</code> if key is private and <code>1</code> if key is public
     * @return user's profile attribute name
     */
    private String sshKeyAttributeName(String host, int isPrivate) {
        // Returns something like: ssh.key.private.codenvy.com or ssh.key.public.codenvy.com
        return (isPrivate == PRIVATE ? PRIVATE_KEY_ATTRIBUTE_PREFIX : PUBLIC_KEY_ATTRIBUTE_PREFIX) + host;
    }

    @SuppressWarnings("unchecked")
    ProfileDescriptor getUserProfile() throws ApiException, IOException, JsonParseException {
        final String profileDescriptor = HttpJsonHelper.requestString(profileApiUrl, "GET", null);
        return JsonHelper.fromJson(profileDescriptor, ProfileDescriptor.class, new TypeToken<ProfileDescriptor>() {}.getType());
    }

    @SuppressWarnings("unchecked")
    Map<String, String> getSshKeys() throws ApiException, IOException, JsonParseException {
        final String preferencesJson = HttpJsonHelper.requestString(profileApiUrl + "/prefs?filter=ssh.key.*", "GET", null);
        return JsonHelper.fromJson(preferencesJson, Map.class, new TypeToken<Map<String, String>>() {}.getType());
    }

    @SuppressWarnings("unchecked")
    void updateSshKeys(Map<String, String> preferences) throws ApiException, IOException {
        HttpJsonHelper.requestString(profileApiUrl + "/prefs",
                                     "POST",
                                     JsonHelper.toJson(preferences),
                                     Pair.of("Content-Type", "application/json"));
    }
}
