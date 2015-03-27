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
package org.eclipse.che.ide.extension.ant.server.project.type;

/** @author Vladyslav Zhukovskii */
public class AntProjectTypeResolverTest {
//    private static final String workspace = "my_ws";
//    private AntProjectTypeResolver antProjectTypeResolver;
//
//    private static final String      vfsUser       = "dev";
//    private static final Set<String> vfsUserGroups = new LinkedHashSet<>(Arrays.asList("workspace/developer"));
//
//    private ProjectManager projectManager;
//
//    private String buildXML =
//            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
//            "<project basedir=\".\" default=\"build\" name=\"antproj\">\n" +
//            "    <target description=\"Builds the application\" name=\"build\">\n" +
//            "        <echo message=\"Hello, world\"/>\n" +
//            "    </target>\n" +
//            "</project>";
//
//
//    @Before
//    public void setUp() throws Exception {
//        final String vfsUser = "dev";
//        final Set<String> vfsUserGroups = new LinkedHashSet<>(Arrays.asList("workspace/developer"));
//        Set<ProjectType> pts = new HashSet<>();
//        final ProjectType pt = new ProjectType("ant", "ant") {
//            {
//                setDefaultBuilder("ant");
//            }
//        };
//
//
//
//        pts.add(pt);
//
//
//        final ProjectTypeRegistry projectTypeRegistry = new ProjectTypeRegistry(pts);
//
//        final EventService eventService = new EventService();
//        final VirtualFileSystemRegistry vfsRegistry = new VirtualFileSystemRegistry();
//        final MemoryFileSystemProvider memoryFileSystemProvider =
//                new MemoryFileSystemProvider(workspace, eventService, new VirtualFileSystemUserContext() {
//                    @Override
//                    public VirtualFileSystemUser getVirtualFileSystemUser() {
//                        return new VirtualFileSystemUser(vfsUser, vfsUserGroups);
//                    }
//                }, vfsRegistry);
//        vfsRegistry.registerProvider(workspace, memoryFileSystemProvider);
//        projectManager = new DefaultProjectManager(vfsRegistry, eventService, projectTypeRegistry);
//        projectManager.createProject(workspace, "my_project", new ProjectConfig("", pt.getId()));
//
//        MockitoAnnotations.initMocks(this);
//        // Bind components
//        Injector injector = Guice.createInjector(new AbstractModule() {
//            @Override
//            protected void configure() {
//                Multibinder<ProjectTypeResolver> projectTypeResolverMultibinder =
//                        Multibinder.newSetBinder(binder(), ProjectTypeResolver.class);
//                projectTypeResolverMultibinder.addBinding().to(AntProjectTypeResolver.class);
//                bind(ProjectManager.class).toInstance(projectManager);
//            }
//        });
//        antProjectTypeResolver = injector.getInstance(AntProjectTypeResolver.class);
//        projectManager = injector.getInstance(ProjectManager.class);
//    }
//
//    @Test
//    public void withoutBuildXml() throws Exception {
//        FolderEntry test = projectManager.getProjectsRoot(workspace).createFolder("test");
//        boolean resolve = antProjectTypeResolver.resolve(test);
//        Assert.assertFalse(resolve);
//        Assert.assertNull(projectManager.getProject(workspace, "test"));
//    }
//
//    @Test
//    public void withBuildXml() throws Exception {
//        FolderEntry test = projectManager.getProjectsRoot(workspace).createFolder("test");
//        test.createFile("build.xml", buildXML.getBytes(), "text/xml");
//        boolean resolve = antProjectTypeResolver.resolve(test);
//        Assert.assertTrue(resolve);
//        Assert.assertNotNull(projectManager.getProject(workspace, "test"));
//        ;
//        Assert.assertNotNull(projectManager.getProject(workspace, "test").getConfig());
//        Assert.assertNotNull(projectManager.getProject(workspace, "test").getConfig().getTypeId());
//        Assert.assertEquals("ant", projectManager.getProject(workspace, "test").getConfig().getTypeId());
//        Assert.assertNotNull(projectManager.getProject(workspace, "test").getConfig().getBuilders());
//        Assert.assertEquals("ant", projectManager.getProject(workspace, "test").getConfig().getBuilders().getDefault());
//    }
//
//    @Test
//    public void withBuildXmlWithFolders() throws Exception {
//        FolderEntry test = projectManager.getProjectsRoot(workspace).createFolder("test");
//        test.createFile("build.xml", buildXML.getBytes(), "text/xml");
//
//        FolderEntry folder = test.createFolder("folder1");
//        folder.createFile("build.xml", buildXML.getBytes(), "text/xml");
//
//        FolderEntry folder1 = test.createFolder("folder2");
//        folder1.createFile("build.xml", buildXML.getBytes(), "text/xml");
//
//
//        boolean resolve = antProjectTypeResolver.resolve(test);
//        Assert.assertTrue(resolve);
//        Assert.assertNotNull(projectManager.getProject(workspace, "test"));
//        Assert.assertNull(projectManager.getProject(workspace, "test/folder1"));
//        Assert.assertNull(projectManager.getProject(workspace, "test/folder2"));
//    }
}
