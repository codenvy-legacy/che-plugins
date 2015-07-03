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
package org.eclipse.che.ide.extension.machine.client.machine.create;

import com.google.gwt.regexp.shared.RegExp;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.machine.gwt.client.RecipeServiceClient;
import org.eclipse.che.api.machine.shared.dto.recipe.RecipeDescriptor;
import org.eclipse.che.api.project.gwt.client.ProjectTypeServiceClient;
import org.eclipse.che.api.project.shared.dto.ProjectTypeDefinition;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Presenter for creating machine.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CreateMachinePresenter implements CreateMachineView.ActionDelegate {

    private static final String URL_PATTERN =
            "(https?|ftp)://(www\\.)?(((([a-zA-Z0-9.-]+\\.){1,}[a-zA-Z]{2,4}|localhost))|((\\d{1,3}\\.){3}(\\d{1,3})))(:(\\d+))?(/([a-zA-Z0-9-._~!$&'()*+,;=:@/]|%[0-9A-F]{2})*)?(\\?([a-zA-Z0-9-._~!$&'()*+,;=:/?@]|%[0-9A-F]{2})*)?(#([a-zA-Z0-9._-]|%[0-9A-F]{2})*)?";
    private static final RegExp URL         = RegExp.compile(URL_PATTERN);

    private final CreateMachineView        view;
    private final MachineManager           machineManager;
    private final AppContext               appContext;
    private final ProjectTypeServiceClient projectTypeServiceClient;
    private final RecipeServiceClient      recipeServiceClient;

    @Inject
    public CreateMachinePresenter(CreateMachineView view,
                                  MachineManager machineManager,
                                  AppContext appContext,
                                  ProjectTypeServiceClient projectTypeServiceClient,
                                  RecipeServiceClient recipeServiceClient) {
        this.view = view;
        this.machineManager = machineManager;
        this.appContext = appContext;
        this.projectTypeServiceClient = projectTypeServiceClient;
        this.recipeServiceClient = recipeServiceClient;

        view.setDelegate(this);
    }

    public void showDialog() {
        view.show();

        view.setCreateButtonState(false);
        view.setReplaceButtonState(false);
        view.setMachineName("");
        view.setRecipeURL("");
        view.setErrorHint(false);
        view.setTags("");

        getRecipeURL().then(new Operation<String>() {
            @Override
            public void apply(String recipeURL) throws OperationException {
                view.setRecipeURL(recipeURL);
            }
        });
    }

    /** Returns project's recipe URL or project type's recipe URL. */
    private Promise<String> getRecipeURL() {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return Promises.resolve("");
        }

        final String recipeURL = currentProject.getRootProject().getRecipe();
        if (recipeURL != null) {
            return Promises.resolve(recipeURL);
        } else {
            final String projectTypeID = currentProject.getRootProject().getType();
            return projectTypeServiceClient.getProjectType(projectTypeID).then(new Function<ProjectTypeDefinition, String>() {
                @Override
                public String apply(ProjectTypeDefinition arg) throws FunctionException {
                    return arg.getDefaultRecipe();
                }
            });
        }
    }

    @Override
    public void onNameChanged() {
        checkButtons();
    }

    @Override
    public void onRecipeUrlChanged() {
        checkButtons();
    }

    @Override
    public void onTagsChanged() {
        recipeServiceClient.searchRecipes(view.getTags(), "docker", 0, 100).then(new Operation<List<RecipeDescriptor>>() {
            @Override
            public void apply(List<RecipeDescriptor> arg) throws OperationException {
                view.setRecipes(arg);
            }
        });
    }

    @Override
    public void onRecipeSelected(RecipeDescriptor recipe) {
        view.setRecipeURL(recipe.getLink("get recipe script").getHref());
    }

    private void checkButtons() {
        final String recipeURL = view.getRecipeURL();
        final boolean urlValid = URL.test(recipeURL);

        view.setErrorHint(!urlValid);

        final boolean allowCreation = urlValid && !view.getMachineName().isEmpty();

        view.setCreateButtonState(allowCreation);
        view.setReplaceButtonState(allowCreation);
    }

    @Override
    public void onCreateClicked() {
        final String machineName = view.getMachineName();
        final String recipeURL = view.getRecipeURL();
        machineManager.startMachine(recipeURL, machineName);

        view.close();
    }

    @Override
    public void onReplaceDevMachineClicked() {
        final String machineName = view.getMachineName();
        final String recipeURL = view.getRecipeURL();
        machineManager.startAndBindMachine(recipeURL, machineName);

        view.close();
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }
}
