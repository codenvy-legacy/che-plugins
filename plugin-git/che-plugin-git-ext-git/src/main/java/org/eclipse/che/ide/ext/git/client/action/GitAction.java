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
package org.eclipse.che.ide.ext.git.client.action;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ProjectAction;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.project.tree.generic.StorableNode;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.api.selection.SelectionAgent;

import org.vectomatic.dom.svg.ui.SVGResource;

import java.util.List;

/**
 * @author Roman Nikitenko
 */
public abstract class GitAction extends ProjectAction {

    protected final AppContext     appContext;
    protected       SelectionAgent selectionAgent;

    public GitAction(String text, String description, SVGResource svgIcon, AppContext appContext,
                     SelectionAgent selectionAgent) {
        super(text, description, svgIcon);
        this.appContext = appContext;
        this.selectionAgent = selectionAgent;
    }

    protected boolean isGitRepository() {
        boolean isGitRepository = false;

        if (getActiveProject() != null) {
            ProjectDescriptor rootProjectDescriptor = getActiveProject().getRootProject();
            List<String> listVcsProvider = rootProjectDescriptor.getAttributes().get("vcs.provider.name");

            if (listVcsProvider != null && (!listVcsProvider.isEmpty()) && listVcsProvider.contains("git")) {
                isGitRepository = true;
            }
        }
        return isGitRepository;
    }

    protected boolean isItemSelected() {
        Selection<?> selection = selectionAgent.getSelection();
        return selection != null && selection.getFirstElement() != null && selection.getFirstElement() instanceof StorableNode;
    }

    protected CurrentProject getActiveProject() {
        return appContext.getCurrentProject();
    }

    @Override
    protected void updateProjectAction(ActionEvent e) {
        e.getPresentation().setVisible(getActiveProject() != null);
        e.getPresentation().setEnabled(isGitRepository());
    }
}
