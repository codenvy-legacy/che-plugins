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
package org.eclipse.che.ide.extension.machine.client.command.valueproviders;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Artem Zatsarynnyy */
@RunWith(MockitoJUnitRunner.class)
public class CurrentProjectNameProviderTest {

    @Mock
    private AppContext appContext;

    @InjectMocks
    private CurrentProjectNameProvider currentProjectNameProvider;

    @Test
    public void shouldReturnValue() throws Exception {
        String projectName = "project";

        ProjectDescriptor projectDescriptor = mock(ProjectDescriptor.class);
        CurrentProject currentProject = mock(CurrentProject.class);
        when(projectDescriptor.getName()).thenReturn(projectName);
        when(currentProject.getProjectDescription()).thenReturn(projectDescriptor);
        when(appContext.getCurrentProject()).thenReturn(currentProject);

        assertThat(currentProjectNameProvider.getValue(), equalTo(projectName));

        verify(appContext).getCurrentProject();
        verify(currentProject).getProjectDescription();
        verify(projectDescriptor).getName();
    }
}
