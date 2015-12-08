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

import org.eclipse.che.plugin.docker.client.helper.NetworkFinder;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static org.eclipse.che.plugin.docker.client.DockerConnectorConfiguration.BRIDGE_LINUX_INTERFACE_NAME;
import static org.eclipse.che.plugin.docker.client.DockerConnectorConfiguration.DEFAULT_DOCKER_MACHINE_DOCKER_HOST_IP;
import static org.eclipse.che.plugin.docker.client.DockerConnectorConfiguration.DEFAULT_LINUX_DOCKER_HOST_IP;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        assertEquals(DockerConnectorConfiguration.UNIX_SOCKET_URI, uri);

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
        env.put(DockerConnectorConfiguration.DOCKER_HOST_PROPERTY, "tcp://192.168.59.104:2376");
        env.put(DockerConnectorConfiguration.DOCKER_TLS_VERIFY_PROPERTY, "1");
        env.put(DockerConnectorConfiguration.DOCKER_CERT_PATH_PROPERTY, tmpDirectory.getAbsolutePath());

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
        env.put(DockerConnectorConfiguration.DOCKER_HOST_PROPERTY, "tcp://192.168.59.104:2375");
        env.put(DockerConnectorConfiguration.DOCKER_CERT_PATH_PROPERTY, tmpDirectory.getAbsolutePath());

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
        env.put(DockerConnectorConfiguration.DOCKER_HOST_PROPERTY, "this is an invalid host");
        env.put(DockerConnectorConfiguration.DOCKER_CERT_PATH_PROPERTY, "invalid");

        URI uri = DockerConnectorConfiguration.dockerDaemonUri(false, env);
        assertEquals(uri, DockerConnectorConfiguration.DEFAULT_DOCKER_MACHINE_URI);

        String path = DockerConnectorConfiguration.dockerMachineCertsDirectoryPath(false, env);
        assertEquals(path, DockerConnectorConfiguration.DEFAULT_DOCKER_MACHINE_CERTS_DIR);

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
        assertEquals(uri, DockerConnectorConfiguration.DEFAULT_DOCKER_MACHINE_URI);

        String path = DockerConnectorConfiguration.dockerMachineCertsDirectoryPath(false, emptyMap());
        assertEquals(path, DockerConnectorConfiguration.DEFAULT_DOCKER_MACHINE_CERTS_DIR);

    }

    /**
     * Check if docker host ip from container is DEFAULT_LINUX_DOCKER_HOST_IP when bridge is not defined
     */
    @Test
    public void testLinuxDefaultDockerHostWithoutBrige() throws Exception {
        Map<String, String> env = new HashMap<>();
        NetworkFinder networkFinder = Mockito.mock(NetworkFinder.class);
        doReturn(Optional.empty()).when(networkFinder).getIPAddress(BRIDGE_LINUX_INTERFACE_NAME);
        DockerConnectorConfiguration dockerConnectorConfiguration = new DockerConnectorConfiguration(null, null, null, networkFinder);

        String ip = dockerConnectorConfiguration.getDockerHostIp(true, env);
        assertEquals(ip, DEFAULT_LINUX_DOCKER_HOST_IP);
        verify(networkFinder).getIPAddress(BRIDGE_LINUX_INTERFACE_NAME);
    }


    /**
     * Check that on Linux, if we have a bridge named docker0, we should use that bridge
     */
    @Test
    public void testLinuxDefaultDockerHostWithBrige() throws Exception {
        Map<String, String> env = new HashMap<>();
        String myCustomIpAddress = "123.231.133.10";
        InetAddress inetAddress = Mockito.mock(InetAddress.class);
        doReturn(myCustomIpAddress).when(inetAddress).getHostAddress();
        NetworkFinder networkFinder = Mockito.mock(NetworkFinder.class);
        doReturn(Optional.of(inetAddress)).when(networkFinder).getIPAddress(BRIDGE_LINUX_INTERFACE_NAME);
        DockerConnectorConfiguration dockerConnectorConfiguration = new DockerConnectorConfiguration(null, null, null, networkFinder);

        String ip = dockerConnectorConfiguration.getDockerHostIp(true, env);
        assertEquals(ip, myCustomIpAddress);
        verify(networkFinder).getIPAddress(BRIDGE_LINUX_INTERFACE_NAME);
    }



    /**
     * With docker machine, if we don't have any address, check the default IP
     */
    @Test
    public void testMacDefaultDockerHost() throws Exception {
        NetworkFinder networkFinder = Mockito.mock(NetworkFinder.class);
        DockerConnectorConfiguration dockerConnectorConfiguration = new DockerConnectorConfiguration(null, null, null, networkFinder);

        String ip = dockerConnectorConfiguration.getDockerHostIp(false, Collections.emptyMap());
        assertEquals(ip, DEFAULT_DOCKER_MACHINE_DOCKER_HOST_IP);
        verify(networkFinder, times(0)).getIPAddress(anyString());
        verify(networkFinder, times(0)).getMatchingInetAddress(anyString());
    }



    /**
     * Use docker machine system env property if set
     */
    @Test
    public void testMacDefaultDockerHostWithDockerHostProperty() throws Exception {
        NetworkFinder networkFinder = Mockito.mock(NetworkFinder.class);
        String myCustomIpAddress = "192.168.59.104";
        InetAddress inetAddress = Mockito.mock(InetAddress.class);
        doReturn(myCustomIpAddress).when(inetAddress).getHostAddress();
        doReturn(Optional.of(inetAddress)).when(networkFinder).getMatchingInetAddress(anyString());
        DockerConnectorConfiguration dockerConnectorConfiguration = new DockerConnectorConfiguration(null, null, null, networkFinder);

        Map<String, String> env = Collections.singletonMap(DockerConnectorConfiguration.DOCKER_HOST_PROPERTY, "tcp://192.168.59.104:2375");

        String ip = dockerConnectorConfiguration.getDockerHostIp(false, env);
        assertEquals(ip, myCustomIpAddress);
        verify(networkFinder, times(0)).getIPAddress(anyString());
        verify(networkFinder).getMatchingInetAddress("192.168.59");
    }



    /**
     * With docker machine, if system env property is set, use it. And check value if no bridge matching ip is found
     */
    @Test
    public void testMacDefaultDockerHostWithDockerHostPropertyNoMatchingNetwork() throws Exception {
        NetworkFinder networkFinder = Mockito.mock(NetworkFinder.class);
        doReturn(Optional.empty()).when(networkFinder).getMatchingInetAddress(anyString());
        DockerConnectorConfiguration dockerConnectorConfiguration = new DockerConnectorConfiguration(null, null, null, networkFinder);

        Map<String, String> env = Collections.singletonMap(DockerConnectorConfiguration.DOCKER_HOST_PROPERTY, "tcp://192.168.59.104:2375");

        String ip = dockerConnectorConfiguration.getDockerHostIp(false, env);
        assertEquals(ip, DEFAULT_DOCKER_MACHINE_DOCKER_HOST_IP);
        verify(networkFinder, times(0)).getIPAddress(anyString());
        verify(networkFinder).getMatchingInetAddress("192.168.59");
    }
}
