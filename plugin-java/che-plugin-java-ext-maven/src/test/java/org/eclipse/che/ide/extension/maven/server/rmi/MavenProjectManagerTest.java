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
package org.eclipse.che.ide.extension.maven.server.rmi;

import com.google.inject.Provider;

import org.eclipse.che.ide.extension.maven.server.MavenServerManager;
import org.eclipse.che.ide.extension.maven.server.core.MavenNotifier;
import org.eclipse.che.ide.extension.maven.server.core.MavenProjectListener;
import org.eclipse.che.ide.extension.maven.server.core.MavenProjectManager;
import org.eclipse.che.ide.extension.maven.server.core.MavenTerminalImpl;
import org.eclipse.che.ide.extension.maven.server.core.project.MavenProject;
import org.eclipse.che.maven.data.MavenConstants;
import org.eclipse.che.maven.data.MavenKey;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Evgen Vidolob
 */
@RunWith(MockitoJUnitRunner.class)
public class MavenProjectManagerTest {

    private final String mavenServerPath = MavenProjectManagerTest.class.getResource("/maven-server").getPath();

    private MavenServerManager manager = new MavenServerManager(mavenServerPath);

    private MavenProjectManager projectManager;

    @Mock
    private IProject project;

    @Mock
    private IFile pom;

    @Mock
    private MavenProjectListener listener;

    @Mock
    private Provider<IWorkspace> workspaceProvider;

    @Mock
    private IWorkspace workspace;

    @Mock
    private IWorkspaceRoot workspaceRoot;

    @Before
    public void setUp() throws Exception {
        projectManager = new MavenProjectManager(manager, new MavenTerminalImpl(), new MavenNotifier(), workspaceProvider);
        when(workspaceProvider.get()).thenReturn(workspace);
        when(workspace.getRoot()).thenReturn(workspaceRoot);
    }

    @Test
    public void testResolveProject() throws Exception {
        when(project.getFile(MavenConstants.POM_FILE_NAME)).thenReturn(pom);
        when(pom.getLocation()).thenReturn(new Path(MavenProjectManagerTest.class.getResource("/FirstProject/pom.xml").getFile()));

        projectManager.addListener(listener);
        MavenProject mavenProject = new MavenProject(project, workspace);
        mavenProject.read(project, manager);
        MavenKey mavenKey = mavenProject.getMavenKey();
        assertThat(mavenKey).isNotNull();

        projectManager.resolveMavenProject(project, mavenProject);
        verify(listener).projectResolved(any(), any());
    }

    @Test
    public void testNotValidResolveProject() throws Exception {
        when(project.getFile(MavenConstants.POM_FILE_NAME)).thenReturn(pom);
        when(pom.getLocation()).thenReturn(new Path(MavenProjectManagerTest.class.getResource("/BadProject/pom.xml").getFile()));
        when(pom.getFullPath()).thenReturn(new Path("/BadProject/pom.xml"));
        when(project.getFullPath()).thenReturn(new Path("/BadProject"));

        projectManager.addListener(listener);
        MavenProject mavenProject = new MavenProject(project, workspace);
        mavenProject.read(project, manager);
        MavenKey mavenKey = mavenProject.getMavenKey();
        assertThat(mavenKey).isNotNull();

        projectManager.resolveMavenProject(project, mavenProject);
        verify(listener).projectResolved(any(), any());
        assertThat(mavenProject.getProblems()).isNotNull().isNotEmpty();
    }
}
