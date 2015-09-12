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

import com.google.inject.Inject;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.project.shared.dto.ProjectTypeDefinition;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.project.type.ProjectTypeRegistry;

import javax.validation.constraints.NotNull;

/**
 * The class provides business logic which allows get recipe
 *
 * @author Dmitry Shnurenko
 */
public class RecipeProvider {

    private final AppContext          appContext;
    private final ProjectTypeRegistry typeRegistry;

    @Inject
    public RecipeProvider(AppContext appContext, ProjectTypeRegistry typeRegistry) {
        this.appContext = appContext;
        this.typeRegistry = typeRegistry;
    }

    /** Returns url through which we can get recipe. */
    @NotNull
    public String getRecipeUrl() {
        CurrentProject currentProject = appContext.getCurrentProject();

        if (currentProject == null) {
            return "";
        }

        ProjectDescriptor root = currentProject.getRootProject();

        String recipeUrl = root.getRecipe();

        if (recipeUrl == null) {
            String projectTypeId = root.getType();

            ProjectTypeDefinition definition = typeRegistry.getProjectType(projectTypeId);

            return definition == null ? "" : definition.getDefaultRecipe();
        }

        return recipeUrl;
    }
}
