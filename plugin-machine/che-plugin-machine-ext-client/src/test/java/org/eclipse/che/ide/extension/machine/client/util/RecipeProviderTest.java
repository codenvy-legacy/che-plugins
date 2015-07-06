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
package org.eclipse.che.ide.extension.machine.client.util;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.project.shared.dto.ProjectTypeDefinition;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.project.type.ProjectTypeRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class RecipeProviderTest {

    private static final String SOME_TEXT = "someText";

    //constructor mocks
    @Mock
    private AppContext          appContext;
    @Mock
    private ProjectTypeRegistry typeRegistry;

    //additional mocks
    @Mock
    private CurrentProject        currentProject;
    @Mock
    private ProjectDescriptor     root;
    @Mock
    private ProjectTypeDefinition definition;

    @InjectMocks
    private RecipeProvider provider;

    @Before
    public void setUp() {
        when(appContext.getCurrentProject()).thenReturn(currentProject);
        when(currentProject.getRootProject()).thenReturn(root);
        when(root.getRecipe()).thenReturn(SOME_TEXT);
    }

    @Test
    public void emptyStringShouldBeReturnedWhenCurrentProjectIsNull() {
        when(appContext.getCurrentProject()).thenReturn(null);

        String url = provider.getRecipeUrl();

        verify(appContext).getCurrentProject();
        verify(currentProject, never()).getRootProject();

        assertThat(url, equalTo(""));
    }

    @Test
    public void recipeFromRootProjectShouldBeReturned() {
        assertThat(provider.getRecipeUrl(), equalTo(SOME_TEXT));

        verify(currentProject).getRootProject();
        verify(root).getRecipe();
    }

    @Test
    public void recipeShouldBeReturnedFromProjectTypeIfDefinitionIsExist() {
        when(root.getRecipe()).thenReturn(null);
        when(root.getType()).thenReturn(SOME_TEXT);
        when(typeRegistry.getProjectType(SOME_TEXT)).thenReturn(definition);
        when(definition.getDefaultRecipe()).thenReturn(SOME_TEXT);

        assertThat(provider.getRecipeUrl(), equalTo(SOME_TEXT));

        verify(typeRegistry).getProjectType(SOME_TEXT);
        verify(definition).getDefaultRecipe();
    }

    @Test
    public void recipeShouldBeReturnedFromProjectTypeIfDefinitionIsNotExist() {
        when(root.getRecipe()).thenReturn(null);
        when(root.getType()).thenReturn(SOME_TEXT);
        when(typeRegistry.getProjectType(SOME_TEXT)).thenReturn(null);

        assertThat(provider.getRecipeUrl(), equalTo(""));

        verify(typeRegistry).getProjectType(SOME_TEXT);
    }

}