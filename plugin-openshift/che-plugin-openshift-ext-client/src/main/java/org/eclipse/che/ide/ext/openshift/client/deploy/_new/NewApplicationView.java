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
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * View for deploying Che project to new OpenShift application.
 * 
 * @author Vlad Zhukovskiy
 */
@ImplementedBy(NewApplicationViewImpl.class)
public interface NewApplicationView extends View<NewApplicationView.ActionDelegate> {

    enum Mode {
        CREATE_NEW_PROJECT,
        SELECT_EXISTING_PROJECT;
    }

    /**
     * Show view.
     */
    void show();

    /**
     * Hide view.
     */
    void hide();

    /**
     * Set create new OpenShift project mode or use existing.
     *
     * @param mode
     *         the project's mode
     */
    void setMode(Mode mode);

    /**
     * Get new OpenShift project's name.
     *
     * @return String returns OpenShift project name
     */
    String getOpenShiftProjectName();

    /**
     * Set name for OpenShift new project.
     *
     * @param name
     */
    void setOpenShiftProjectName(String name);

    /**
     * Get new OpenShift project's display name.
     *
     * @return String returns OpenShift project display name
     */
    String getOpenShiftProjectDisplayName();

    /**
     * Set display name for OpenShift new project.
     *
     * @param name
     *         display name
     */
    void setOpenShiftProjectDisplayName(String name);

    /**
     * Get new OpenShift project's description.
     *
     * @return String returns OpenShift project description
     */
    String getOpenShiftProjectDescription();

    /**
     * Set description for new OpenShift project.
     *
     * @param description
     */
    void setOpenShiftProjectDescription(String description);

    /**
     * Get selected existing OpenShift project.
     *
     * @return OpenShift selected project
     */
    Project getOpenShiftSelectedProject();

    /**
     * Get new OpenShift application name.
     *
     * @return application name
     */
    String getApplicationName();

    /**
     * Set name for new OpenShift application.
     *
     * @param name
     */
    void setApplicationName(String name);

    /**
     * Set the list of OpenShift projects to display.
     *
     * @param projects
     */
    void setProjects(List<Project> projects);

    /**
     * Set the list of OpenShift images to display.
     *
     * @param images
     */
    void setImages(List<String> images);

    /**
     * Get selected OpenShift image.
     *
     * @return selected image
     */
    String getActiveImage();

    /**
     * Get selected OpenShift mode: new or existing.
     *
     * @return mode
     */
    Mode getMode();

    /**
     * Set the list of OpenShift environment variables to display.
     *
     * @param variables
     *         environment variables
     */
    void setEnvironmentVariables(List<Pair<String, String>> variables);

    /**
     * Get the list of OpenShift environment variables.
     *
     * @return environment variables
     */
    List<Pair<String, String>> getEnvironmentVariables();

    /**
     * Set the list of OpenShift labels to display.
     *
     * @param labels
     */
    void setLabels(List<Pair<String, String>> labels);

    /**
     * Get the list of OpenShift application labels.
     *
     * @return labels
     */
    List<Pair<String, String>> getLabels();

    /**
     * Set the enabled state of Deploy button.
     *
     * @param enabled
     */
    void setDeployButtonEnabled(boolean enabled);

    /**
     * Set error message to display.
     *
     * @param error error message
     */
    void showError(String error);

    interface ActionDelegate {

        /**
         * Handle event, when cancel button is clicked.
         */
        void onCancelClicked();

        /**
         * Handler event, when deploy button is clicked.
         */
        void onDeployClicked();

        /**
         * Handle event, when project name is changed.
         *
         * @param name
         *         new project name
         */
        void onProjectNameChanged(String name);

        /**
         * Handler event, when application name is changed.
         *
         * @param name
         *         new application name
         */
        void onApplicationNameChanged(String name);

        /**
         * Handler event, when image stream is changed.
         *
         * @param stream
         *         new image stream
         */
        void onImageStreamChanged(String stream);

        /**
         * Handler event, when OpenShift project is selected.
         *
         * @param project
         *         selected project
         */
        void onActiveProjectChanged(Project project);

        /**
         * Handler event, when project mode is changed.
         *
         * @param mode
         *         project mode
         */
        void onModeChanged(Mode mode);
    }
}
