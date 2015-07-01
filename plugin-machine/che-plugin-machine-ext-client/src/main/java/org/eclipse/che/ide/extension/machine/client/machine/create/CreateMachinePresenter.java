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

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;

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

    private final CreateMachineView view;
    private final MachineManager    machineManager;
    private final AppContext        appContext;

    @Inject
    public CreateMachinePresenter(CreateMachineView view, MachineManager machineManager, AppContext appContext) {
        this.view = view;
        this.machineManager = machineManager;
        this.appContext = appContext;

        view.setDelegate(this);
    }

    public void showDialog() {
        view.show();

        view.setCreateButtonState(false);
        view.setReplaceButtonState(false);
        view.setMachineName("");
        view.setRecipeURL("");

        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            final String recipeURL = currentProject.getRootProject().getRecipe();
            if (recipeURL != null) {
                view.setRecipeURL(recipeURL);
            }
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
