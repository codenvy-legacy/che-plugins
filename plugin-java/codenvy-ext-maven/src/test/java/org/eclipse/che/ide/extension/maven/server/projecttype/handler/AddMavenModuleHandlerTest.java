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
package org.eclipse.che.ide.extension.maven.server.projecttype.handler;

import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.project.server.DefaultProjectManager;
import org.eclipse.che.api.project.server.Project;
import org.eclipse.che.api.project.server.ProjectConfig;
import org.eclipse.che.api.project.server.VirtualFileEntry;
import org.eclipse.che.api.project.server.handlers.ProjectHandler;
import org.eclipse.che.api.project.server.handlers.ProjectHandlerRegistry;
import org.eclipse.che.api.project.server.type.ProjectType;
import org.eclipse.che.api.project.server.type.ProjectTypeRegistry;
import org.eclipse.che.api.vfs.server.ContentStream;
import org.eclipse.che.api.vfs.server.VirtualFileSystemRegistry;
import org.eclipse.che.api.vfs.server.VirtualFileSystemUser;
import org.eclipse.che.api.vfs.server.VirtualFileSystemUserContext;
import org.eclipse.che.api.vfs.server.impl.memory.MemoryFileSystemProvider;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.ide.extension.maven.shared.MavenAttributes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * @author Vitaly Parfonov
 */
@RunWith(MockitoJUnitRunner.class)
public class AddMavenModuleHandlerTest {

    private static final String workspace = "my_ws";

    private static final String POM_XML_TEMPL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project><packaging>%s</packaging></project>";

    private AddMavenModuleHandler addMavenModuleHandler;
    private DefaultProjectManager projectManager;
    private ProjectTypeRegistry   projectTypeRegistry;


    @Before
    public void setUp() throws Exception {
        addMavenModuleHandler = new AddMavenModuleHandler();
        ProjectType mavenProjectType = Mockito.mock(ProjectType.class);
        Mockito.when(mavenProjectType.getId()).thenReturn(MavenAttributes.MAVEN_ID);
        Mockito.when(mavenProjectType.getDisplayName()).thenReturn(MavenAttributes.MAVEN_ID);
        Mockito.when(mavenProjectType.canBePrimary()).thenReturn(true);
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


        Set<ProjectType> projTypes = new HashSet<>();
        projTypes.add(mavenProjectType);

        projectTypeRegistry = new ProjectTypeRegistry(projTypes);

        Set<ProjectHandler> handlers = new HashSet<>();
        ProjectHandlerRegistry handlerRegistry = new ProjectHandlerRegistry(handlers);

        projectManager = new DefaultProjectManager(vfsRegistry, eventService,
                                                   projectTypeRegistry, handlerRegistry);
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAddModuleIfNotPomPackage() throws Exception {
        String parent = NameGenerator.generate("parent", 5);
        String module = NameGenerator.generate("module", 5);
        Project project =
                projectManager.createProject(workspace, parent, new ProjectConfig(null, MavenAttributes.MAVEN_ID), null, "public");
        project.getBaseFolder().createFile("pom.xml", String.format(POM_XML_TEMPL, "jar").getBytes(), "text/xml");
        addMavenModuleHandler
                .onCreateModule(project.getBaseFolder(), project.getPath() + "/" + module, new ProjectConfig(null, "maven"),
                                Collections.<String, String>emptyMap());
    }


    @Test(expected = IllegalArgumentException.class)
    public void pomNotFound() throws Exception {
        String parent = NameGenerator.generate("parent", 5);
        String module = NameGenerator.generate("module", 5);
        Project project =  projectManager.createProject(workspace, parent, new ProjectConfig(null, MavenAttributes.MAVEN_ID), null, "public");
        addMavenModuleHandler
                .onCreateModule(project.getBaseFolder(), project.getPath() + "/" + module, new ProjectConfig(null, "maven"),
                                Collections.<String, String>emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAddModuleIfModuleNotMaven() throws Exception {
        ProjectType notMaven = Mockito.mock(ProjectType.class);
        Mockito.when(notMaven.getId()).thenReturn("notMaven");
        Mockito.when(notMaven.getDisplayName()).thenReturn("notMaven");
        Mockito.when(notMaven.canBePrimary()).thenReturn(true);
        projectTypeRegistry.registerProjectType(notMaven);

        String parent = NameGenerator.generate("parent", 5);
        String module = NameGenerator.generate("module", 5);
        Project project =
                projectManager.createProject(workspace, parent, new ProjectConfig(null, MavenAttributes.MAVEN_ID), null, "public");
        project.getBaseFolder().createFile("pom.xml", String.format(POM_XML_TEMPL, "pom").getBytes(), "text/xml");
        addMavenModuleHandler.onCreateModule(project.getBaseFolder(), project.getPath() + "/" + module,
                                             new ProjectConfig(null, notMaven.getId()),
                                             Collections.<String, String>emptyMap());
    }

    @Test
    public void addModuleOk() throws Exception {
        String parent = NameGenerator.generate("parent", 5);
        String module = NameGenerator.generate("module", 5);
        Project project =
                projectManager.createProject(workspace, parent, new ProjectConfig(null, MavenAttributes.MAVEN_ID), null, "public");
        project.getBaseFolder().createFile("pom.xml", String.format(POM_XML_TEMPL, "pom").getBytes(), "text/xml");
        addMavenModuleHandler.onCreateModule(project.getBaseFolder(), project.getPath() + "/" + module,
                                             new ProjectConfig(null, MavenAttributes.MAVEN_ID),
                                             Collections.<String, String>emptyMap());

        VirtualFileEntry pom = project.getBaseFolder().getChild("pom.xml");
        Assert.assertNotNull(pom);
        ContentStream content = pom.getVirtualFile().getContent();
        Assert.assertNotNull(content);
        InputStream stream = content.getStream();
        String pomContent = IoUtil.readStream(stream);
        Assert.assertNotNull(pomContent);
        Assert.assertFalse(pomContent.isEmpty());
        String mavenModule = String.format("<module>%s</module>", module);
        Assert.assertTrue(pomContent.contains(mavenModule));
    }

}
