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
package org.eclipse.che.example.tree;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.workspace.perspectives.project.ProjectPerspective;


/**
 * @author Vlad Zhukovskiy
 */
@Singleton
public class TreePresenter extends BasePresenter {

    private TreeView view;

    @Inject
    public TreePresenter(TreeView view) {
        this.view = view;
        addRule(ProjectPerspective.PROJECT_PERSPECTIVE_ID);
    }

    @Override
    public String getTitle() {
        return "Tree";
    }

    @Override
    public void setVisible(boolean b) {

    }

    @Override
    public IsWidget getView() {
        return view;
    }

    @Nullable
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    @Nullable
    @Override
    public String getTitleToolTip() {
        return "Tree example";
    }

    @Override
    public void go(AcceptsOneWidget acceptsOneWidget) {
        acceptsOneWidget.setWidget(view);
    }
}
