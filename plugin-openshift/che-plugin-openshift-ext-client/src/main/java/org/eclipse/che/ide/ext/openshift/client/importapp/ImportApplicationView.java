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
package org.eclipse.che.ide.ext.openshift.client.importapp;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;

import java.util.List;
import java.util.Map;

/**
 * The view of {@link ImportApplicationPresenter}.
 *
 * @author Anna Shumilova
 */
@ImplementedBy(ImportApplicationViewImpl.class)
public interface ImportApplicationView extends View<ImportApplicationView.ActionDelegate> {

    /** Show view. */
    void showView();

    /** Close view. */
    void closeView();

    /**
     * Set build configs to be displayed (grouped by project(namespace)).
     *
     * @param buildConfigs
     */
    void setBuildConfigs(Map<String, List<BuildConfig>> buildConfigs);


    /**
     * Set the enabled state of the Import button.
     *
     * @param isEnabled
     *         enabled state
     */
    void enableImportButton(boolean isEnabled);

    /**
     * Display the project's name.
     *
     * @param name
     *         project name
     */
    void setProjectName(String name);

    /**
     * Display the project's description.
     *
     * @param description
     *         project description
     */
    void setProjectDescription(String description);

    /**
     * Display application's info (source url, branch, context dir).
     *
     * @param buildConfig
     *         application
     */
    void setApplicationInfo(BuildConfig buildConfig);

    /**
     * Return project's name from input.
     *
     * @return project's name
     */
    String getProjecName();

    /**
     * Return project's description from input.
     *
     * @return project's description
     */
    String getProjectDescription();

    /**
     * Display the error message.
     *
     * @param message
     *         error message
     */
    void setErrorMessage(String message);

    /** Show invalid Che project name error message. */
    void showCheProjectNameError(String message);

    /** Hide invalid Che project name error message. */
    void hideCheProjectNameError();

    /** Action handler for */
    interface ActionDelegate {

        /**
         * Handle Import application button clicked event.
         */
        void onImportApplicationClicked();

        /**
         * Handle Cancel button clicked event.
         */
        void onCancelClicked();

        /**
         * Handle Build Configuration selected event.
         */
        void onBuildConfigSelected(BuildConfig buildConfig);

        /**
         * Handle project's name change event.
         */
        void onProjectNameChanged(String name);
    }
}
