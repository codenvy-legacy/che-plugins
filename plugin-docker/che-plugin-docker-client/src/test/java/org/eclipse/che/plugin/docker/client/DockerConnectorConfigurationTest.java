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
package org.eclipse.che.plugin.docker.client;

import com.google.common.io.Files;

import org.testng.annotations.Test;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.eclipse.che.plugin.docker.client.DockerConnector.DEFAULT_DOCKER_MACHINE_CERTS_DIR;
import static org.eclipse.che.plugin.docker.client.DockerConnector.DEFAULT_DOCKER_MACHINE_URI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Tests of the docker connector configuration
 *
 * @author Florent Benoit
 */
public class DockerConnectorConfigurationTest {

    /**
     * On Linux, no Docker Machine so expect direct connection
     */
    @Test
    public void testDockerOnLinux() {
        URI uri = DockerConnectorConfiguration.dockerDaemonUri(true, emptyMap());
        assertEquals(DockerConnector.UNIX_SOCKET_URI, uri);

        String path = DockerConnectorConfiguration.dockerMachineCertsDirectoryPath(true, emptyMap());
        assertNull(path, "On Linux, there is no need of certificates");
    }

    /**
     * On non-Linux, if Docker Machine properties are defined, it should use them
     * TLS enabled
     */
    @Test
    public void testDockerUriOnNonLinuxSecure() throws Exception {

        File tmpDirectory = Files.createTempDir();
        tmpDirectory.deleteOnExit();

        Map<String, String> env = new HashMap<>();
        env.put(DockerConnector.DOCKER_HOST_PROPERTY, "tcp://192.168.59.104:2376");
        env.put(DockerConnector.DOCKER_TLS_VERIFY_PROPERTY, "1");
        env.put(DockerConnector.DOCKER_CERT_PATH_PROPERTY, tmpDirectory.getAbsolutePath());

        URI uri = DockerConnectorConfiguration.dockerDaemonUri(false, env);
        assertEquals(uri, new URI("https://192.168.59.104:2376"));

        String path = DockerConnectorConfiguration.dockerMachineCertsDirectoryPath(false, env);
        assertEquals(path, tmpDirectory.getAbsolutePath());

    }

    /**
     * On non-Linux, if Docker Machine properties are defined, it should use them
     * TLS disable
     */
    @Test
    public void testDockerUriOnNonLinuxNonSecure() throws Exception {

        File tmpDirectory = Files.createTempDir();
        tmpDirectory.deleteOnExit();

        Map<String, String> env = new HashMap<>();
        env.put(DockerConnector.DOCKER_HOST_PROPERTY, "tcp://192.168.59.104:2375");
        env.put(DockerConnector.DOCKER_CERT_PATH_PROPERTY, tmpDirectory.getAbsolutePath());

        URI uri = DockerConnectorConfiguration.dockerDaemonUri(false, env);
        assertEquals(uri, new URI("http://192.168.59.104:2375"));

        String path = DockerConnectorConfiguration.dockerMachineCertsDirectoryPath(false, env);
        assertEquals(path, tmpDirectory.getAbsolutePath());

    }


    /**
     * On non-Linux, if Docker Machine properties are defined, it should use them
     * TLS disable
     */
    @Test
    public void testDockerUriOnNonLinuxInvalidProperties() throws Exception {

        File tmpDirectory = Files.createTempDir();
        tmpDirectory.deleteOnExit();

        Map<String, String> env = new HashMap<>();
        env.put(DockerConnector.DOCKER_HOST_PROPERTY, "this is an invalid host");
        env.put(DockerConnector.DOCKER_CERT_PATH_PROPERTY, "invalid");

        URI uri = DockerConnectorConfiguration.dockerDaemonUri(false, env);
        assertEquals(uri, DEFAULT_DOCKER_MACHINE_URI);

        String path = DockerConnectorConfiguration.dockerMachineCertsDirectoryPath(false, env);
        assertEquals(path, DEFAULT_DOCKER_MACHINE_CERTS_DIR);

    }


    /**
     * On non-Linux, if Docker Machine properties are defined, it should use them
     * TLS disable
     */
    @Test
    public void testDockerUriOnNonLinuxMissingProperties() throws Exception {

        File tmpDirectory = Files.createTempDir();
        tmpDirectory.deleteOnExit();

        URI uri = DockerConnectorConfiguration.dockerDaemonUri(false, emptyMap());
        assertEquals(uri, DEFAULT_DOCKER_MACHINE_URI);

        String path = DockerConnectorConfiguration.dockerMachineCertsDirectoryPath(false, emptyMap());
        assertEquals(path, DEFAULT_DOCKER_MACHINE_CERTS_DIR);

    }


}
