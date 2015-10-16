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
package org.eclipse.che.ide.ext.openshift.client.deploy;

import com.google.inject.ImplementedBy;

import org.eclipse.che.api.git.shared.Remote;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ext.openshift.client.project.wizard.CreateProjectViewImpl;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;

import java.util.List;
import java.util.Map;

/**
 * The view of {@link LinkProjectWithExistingApplicationPresenter}.
 *
 * @author Anna Shumilova
 */
@ImplementedBy(LinkProjectWithExistingApplicationViewImpl.class)
public interface LinkProjectWithExistingApplicationView extends View<LinkProjectWithExistingApplicationView.ActionDelegate> {

    /** Show view. */
    void showView();

    /** Close view. */
    void closeView();

    /**
     * Set build configs to be displayed (grouped by project(namespace)).
     * @param buildConfigs
     */
    void setBuildConfigs(Map<String, List<BuildConfig>> buildConfigs);

    /**
     * Set build config source URL.
     * @param url build config sources location
     */
    void setBuildConfigGitUrl(String url);

    /**
     * Set the enabled state of the Link button.
     * @param isEnabled enabled state
     */
    void enableLinkButton(boolean isEnabled);

    /**
     * Set the content of the warning message.
     * @param message warning content
     */
    void setReplaceWarningMessage(String message);

    /**
     * Set build configs to be displayed (grouped by project(namespace)).
     * @param remotes list of Git remotes
     */
    void setGitRemotes(List<Remote> remotes);

    String getGitRemoteUrl();

    /** Action handler for */
    interface ActionDelegate {

        /**
         * Handle Link application button clicked event.
         */
        void onLinkApplicationClicked();

        /**
         * Handle Cancel button clicked event.
         */
        void onCancelClicked();

        /**
         * Handle Build Configuration selected event.
         */
        void onBuildConfigSelected(BuildConfig buildConfig);
    }
}
