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

package org.eclipse.che.ide.ext.python.server.project.type;

import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.project.server.DefaultProjectManager;
import org.eclipse.che.api.project.server.FolderEntry;
import org.eclipse.che.api.project.server.Project;
import org.eclipse.che.api.project.server.ProjectConfig;
import org.eclipse.che.api.project.server.ProjectManager;
import org.eclipse.che.api.project.server.handlers.ProjectHandler;
import org.eclipse.che.api.project.server.handlers.ProjectHandlerRegistry;
import org.eclipse.che.api.project.server.type.ProjectType;
import org.eclipse.che.api.project.server.type.ProjectTypeRegistry;
import org.eclipse.che.api.vfs.server.VirtualFileSystemRegistry;
import org.eclipse.che.api.vfs.server.VirtualFileSystemUser;
import org.eclipse.che.api.vfs.server.VirtualFileSystemUserContext;
import org.eclipse.che.api.vfs.server.impl.memory.MemoryFileSystemProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.eclipse.che.ide.ext.python.shared.ProjectAttributes.HAS_PYTHON_FILES;

/**
 * Testing {@link PythonValueProviderFactory} functionality.
 *
 * @author Roman Nikitenko.
 */
public class PythonValueProviderFactoryTest {
    private static final String workspace = "my_ws";

    private ProjectManager             pm;
    private PythonValueProviderFactory pythonValueProviderFactory;

    @Before
    public void setUp() throws Exception {
        final String vfsUser = "dev";
        final Set<String> vfsUserGroups = new LinkedHashSet<>(Arrays.asList("workspace/developer"));

        final EventService eventService = new EventService();

        VirtualFileSystemRegistry vfsRegistry = new VirtualFileSystemRegistry();
        final MemoryFileSystemProvider memoryFileSystemProvider =
                new MemoryFileSystemProvider(workspace, eventService, new VirtualFileSystemUserContext() {
                    @Override
                    public VirtualFileSystemUser getVirtualFileSystemUser() {
                        return new VirtualFileSystemUser(vfsUser, vfsUserGroups);
                    }
                }, vfsRegistry);
        vfsRegistry.registerProvider(workspace, memoryFileSystemProvider);


        Set<ProjectType> projectTypes = new HashSet<>();
        pythonValueProviderFactory = new PythonValueProviderFactory();
        projectTypes.add(new PythonProjectType(pythonValueProviderFactory));

        ProjectTypeRegistry ptRegistry = new ProjectTypeRegistry(projectTypes);
        Set<ProjectHandler> handlers = new HashSet<>();
        ProjectHandlerRegistry handlerRegistry = new ProjectHandlerRegistry(handlers);

        pm = new DefaultProjectManager(vfsRegistry, eventService, ptRegistry, handlerRegistry);
    }

    @Test
    public void testGetValuesWhenProjectContainsPythonFiles() throws Exception {
        Project pythonProject = pm.createProject(workspace, "pythonProject",
                                                 new ProjectConfig("test project", "python", new HashMap<>(), null, null, null), null, null);
        FolderEntry baseFolder = pythonProject.getBaseFolder();
        baseFolder.createFile("test.py", "test".getBytes(), MediaType.TEXT_PLAIN);

        List<String> values = pythonValueProviderFactory.newInstance(baseFolder).getValues(HAS_PYTHON_FILES);

        Assert.assertNotNull(values);
        Assert.assertFalse(values.isEmpty());
        assertEquals(1, values.size());
    }

    @Test
    public void testGetValuesWhenProjectNotContainsPythonFiles() throws Exception {
        Project pythonProject = pm.createProject(workspace, "pythonProject",
                                                 new ProjectConfig("test project", "python", new HashMap<>(), null, null, null), null, null);
        FolderEntry baseFolder = pythonProject.getBaseFolder();
        FolderEntry firstFolder = baseFolder.createFolder("firstFolder");
        FolderEntry secondFolder = baseFolder.createFolder("secondFolder");
        firstFolder.createFile("test.txt", "test".getBytes(), MediaType.TEXT_PLAIN);
        secondFolder.createFile("test.java", "test".getBytes(), MediaType.TEXT_PLAIN);

        List<String> values = pythonValueProviderFactory.newInstance(baseFolder).getValues(HAS_PYTHON_FILES);

        Assert.assertNotNull(values);
        Assert.assertTrue(values.isEmpty());
    }
}
