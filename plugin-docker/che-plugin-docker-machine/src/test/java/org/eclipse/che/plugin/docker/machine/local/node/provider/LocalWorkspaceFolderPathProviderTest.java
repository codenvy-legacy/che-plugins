/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.docker.machine.local.node.provider;


import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;


@Listeners(value = {MockitoTestNGListener.class})
public class LocalWorkspaceFolderPathProviderTest  {


    @Test(expectedExceptions=IOException.class)
    public void shouldFailWithBothNullPrams() throws IOException {
        new  LocalWorkspaceFolderPathProvider(null, null);
    }


    @Test(expectedExceptions=IOException.class)
    public void shouldFailWithParamsToWorkspacesFolderLocationAsFile() throws IOException {
        final String tempFile = Files.createTempFile(null,null).toString();
        new  LocalWorkspaceFolderPathProvider(tempFile, null);
    }

    @Test(expectedExceptions=IOException.class)
    public void shouldFailWithParamsToProjectLocationAsFile() throws IOException {
        final String tempFile = Files.createTempFile(null, null).toString();
        new  LocalWorkspaceFolderPathProvider(null, tempFile);
    }


    @Test
    public void shouldReturnSameLocationWithUsedHostedProjectParams() throws IOException {
        final String hostProjectsFile = Files.createTempDirectory("my-projects").toString();
        final LocalWorkspaceFolderPathProvider provider = new LocalWorkspaceFolderPathProvider(null, hostProjectsFile);
        final String pathToWs = provider.getPath(UUID.randomUUID().toString());
        final String pathToWs2 = provider.getPath(UUID.randomUUID().toString());
        assertEquals(pathToWs, pathToWs2);

    }

    @Test
    public void shouldReturnLocationDependOnWorkspaceId() throws IOException {
        final String workspacesPath = Files.createTempDirectory("my-workspaces").toString();
        final LocalWorkspaceFolderPathProvider provider = new LocalWorkspaceFolderPathProvider(workspacesPath, null);
        final String workspaceId = UUID.randomUUID().toString();
        final String workspaceId2 = UUID.randomUUID().toString();
        final String pathToWs = provider.getPath(workspaceId);
        final String pathToWs2 = provider.getPath(workspaceId2);
        assertNotEquals(pathToWs, pathToWs2);
        assertEquals(pathToWs, Paths.get(workspacesPath).resolve(workspaceId).toString());
        assertEquals(pathToWs2, Paths.get(workspacesPath).resolve(workspaceId2).toString());
    }

    @Test
    public void shouldReturnLocationToHostProjectPathAlwaysIfItConfigure() throws IOException {
        final String workspacesPath = Files.createTempDirectory("my-workspaces").toString();
        final String hostProjectsPath = Files.createTempDirectory("my-projects").toString();
        final LocalWorkspaceFolderPathProvider provider = new LocalWorkspaceFolderPathProvider(workspacesPath, hostProjectsPath);
        final String workspaceId = UUID.randomUUID().toString();
        final String workspaceId2 = UUID.randomUUID().toString();
        final String pathToWs = provider.getPath(workspaceId);
        final String pathToWs2 = provider.getPath(workspaceId2);
        assertEquals(pathToWs, pathToWs2);
    }


    @Test
    public void test() {
        System.out.println(System.getProperty("user.home"));
    }
}