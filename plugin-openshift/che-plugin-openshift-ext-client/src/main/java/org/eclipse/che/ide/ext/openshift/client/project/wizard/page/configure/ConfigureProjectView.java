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
package org.eclipse.che.ide.ext.openshift.client.project.wizard.page.configure;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;

import java.util.List;

/**
 * View for {@link ConfigureProjectPresenter}.
 *
 * @author Vlad Zhukovskiy
 */
@ImplementedBy(ConfigureProjectViewImpl.class)
public interface ConfigureProjectView extends View<ConfigureProjectView.ActionDelegate> {

    /** Reset input and controls state. */
    void resetControls();

    /** @return true if new OpenShift project radio button selected. */
    boolean isNewOpenShiftProjectSelected();

    /** Sets exist OpnShift projects into view. */
    void setExistOpenShiftProjects(List<Project> projects);

    /** @return new OpenShift project name. */
    String getOpenShiftNewProjectName();

    /** @return new Codenvy project name. */
    String getCodenvyNewProjectName();

    /** @return selected existed OpenShift project. */
    Project getExistedSelectedProject();

    /** Handles operations from the view. */
    interface ActionDelegate {
        /** Process operations when openshift project name changed. */
        void onOpenShiftNewProjectNameChanged();

        /** Process operations when codenvy project name changed. */
        void onCodenvyNewProjectNameChanged();

        /** Process operation when existed project selected. */
        void onExistProjectSelected();
    }
}
