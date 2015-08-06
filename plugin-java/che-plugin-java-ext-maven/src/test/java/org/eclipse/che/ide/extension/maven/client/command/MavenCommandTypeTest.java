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
package org.eclipse.che.ide.extension.maven.client.command;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.ConfigurationPage;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
import org.eclipse.che.ide.extension.maven.client.MavenResources;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static org.eclipse.che.ide.extension.maven.client.command.MavenCommandType.COMMAND_TEMPLATE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Artem Zatsarynnyy */
@RunWith(MockitoJUnitRunner.class)
public class MavenCommandTypeTest {

    @Mock
    private MavenResources            mavenResources;
    @Mock
    private MavenCommandPagePresenter mavenCommandPagePresenter;
    @Mock
    private MachineManager            machineManager;
    @Mock
    private AppContext                appContext;

    @InjectMocks
    private MavenCommandType mavenCommandType;

    @Test
    public void shouldReturnId() throws Exception {
        assertThat(mavenCommandType.getId(), equalTo(MavenCommandType.ID));
    }

    @Test
    public void shouldReturnDisplayName() throws Exception {
        assertThat(mavenCommandType.getDisplayName(), equalTo(MavenCommandType.DISPLAY_NAME));
    }

    @Test
    public void shouldReturnIcon() throws Exception {
        mavenCommandType.getIcon();

        verify(mavenResources).mavenCommandType();
    }

    @Test
    public void shouldReturnPages() throws Exception {
        final Collection<ConfigurationPage<? extends CommandConfiguration>> pages = mavenCommandType.getConfigurationPages();

        assertTrue(pages.contains(mavenCommandPagePresenter));
    }

    @Test
    public void shouldReturnCommandTemplate() throws Exception {
        CurrentProject currentProject = mock(CurrentProject.class);
        ProjectDescriptor rootProject = mock(ProjectDescriptor.class);
        String projectName = "project_name";
        when(rootProject.getName()).thenReturn(projectName);
        when(currentProject.getRootProject()).thenReturn(rootProject);
        when(appContext.getCurrentProject()).thenReturn(currentProject);

        final String commandTemplate = mavenCommandType.getCommandTemplate();

        assertEquals(COMMAND_TEMPLATE + " -f " + projectName, commandTemplate);
    }
}
