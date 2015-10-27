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
package org.eclipse.che.ide.ext.openshift.client.deploy._new;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;

import java.util.List;

/**
 * @author Vlad Zhukovskiy
 */
@ImplementedBy(NewApplicationViewImpl.class)
public interface NewApplicationView extends View<NewApplicationView.ActionDelegate> {

    void show();

    void hide();

    String getProjectName();

    String getDisplayName();

    String getDescriptionName();

    String getApplicationName();

    void setApplicationName(String name);

    void setProjectList(List<String> projectList);

    void setBuildImageList(List<String> buildImageList);

    interface ActionDelegate {
        void onCancelClicked();

        void onDeployClicked();

        void onProjectNameChanged();

        void onDisplayNameChanged();

        void onDescriptionChanged();

        void onApplicationNameChanged();
    }
}
