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
package org.eclipse.che.ide.ext.gwt.client.command;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.ext.gwt.client.GwtResources;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfigurationPage;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.che.ide.ext.gwt.client.command.GwtCommandType.COMMAND_TEMPLATE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Artem Zatsarynnyy */
@RunWith(MockitoJUnitRunner.class)
public class GwtCommandTypeTest {

    @Mock
    private GwtResources            gwtResources;
    @Mock
    private GwtCommandPagePresenter gwtCommandPagePresenter;
    @Mock
    private AppContext              appContext;
    @Mock
    private MachineManager          machineManager;

    @InjectMocks
    private GwtCommandType gwtCommandType;

    @Test
    public void shouldReturnId() throws Exception {
        assertThat(gwtCommandType.getId(), equalTo(GwtCommandType.ID));
    }

    @Test
    public void shouldReturnDisplayName() throws Exception {
        assertThat(gwtCommandType.getDisplayName(), equalTo(GwtCommandType.DISPLAY_NAME));
    }

    @Test
    public void shouldReturnIcon() throws Exception {
        gwtCommandType.getIcon();

        verify(gwtResources).gwtCommandType();
    }

    @Test
    public void shouldReturnPages() throws Exception {
        final Collection<CommandConfigurationPage<? extends CommandConfiguration>> pages = gwtCommandType.getConfigurationPages();

        assertTrue(pages.contains(gwtCommandPagePresenter));
    }

    @Test
    public void shouldReturnCommandTemplate() throws Exception {
        CurrentProject currentProject = mock(CurrentProject.class);
        ProjectDescriptor rootProject = mock(ProjectDescriptor.class);
        String projectName = "project_name";
        when(rootProject.getName()).thenReturn(projectName);
        when(currentProject.getRootProject()).thenReturn(rootProject);
        when(appContext.getCurrentProject()).thenReturn(currentProject);

        Map<String, String> metadata = new HashMap<>();
        String hostName = "host";
        metadata.put("config.hostname", hostName);
        Machine devMachine = mock(Machine.class);
        when(devMachine.getMetadata()).thenReturn(metadata);
        when(machineManager.getDeveloperMachine()).thenReturn(devMachine);

        final String commandTemplate = gwtCommandType.getCommandTemplate();

        assertEquals(COMMAND_TEMPLATE + " -f " + projectName + " -Dgwt.bindAddress=" + hostName, commandTemplate);
    }
}
