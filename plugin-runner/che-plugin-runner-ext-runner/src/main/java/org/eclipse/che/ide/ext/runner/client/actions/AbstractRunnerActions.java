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
package org.eclipse.che.ide.ext.runner.client.actions;

import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ProjectAction;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * The class contains general actions business logic of runners.
 *
 * @author Dmitry Shnurenko
 */
public abstract class AbstractRunnerActions extends ProjectAction {

    private final AppContext appContext;

    public AbstractRunnerActions(@NotNull AppContext appContext,
                                 @NotNull String actionName,
                                 @NotNull String actionPrompt,
                                 @Nullable SVGResource image) {
        super(actionName, actionPrompt, image);

        this.appContext = appContext;
    }

    /** {@inheritDoc} */
    @Override
    protected void updateProjectAction(ActionEvent event) {
        CurrentProject currentProject = appContext.getCurrentProject();

        event.getPresentation().setEnabledAndVisible(currentProject != null);
    }
}