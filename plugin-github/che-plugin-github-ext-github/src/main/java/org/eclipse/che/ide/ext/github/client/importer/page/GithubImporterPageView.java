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
package org.eclipse.che.ide.ext.github.client.importer.page;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.ext.github.client.load.ProjectData;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * @author Roman Nikitenko
 */
@ImplementedBy(GithubImporterPageViewImpl.class)
public interface GithubImporterPageView extends View<GithubImporterPageView.ActionDelegate> {

    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having changed the project's name. */
        void projectNameChanged(@Nonnull String name);

        /** Performs any actions appropriate in response to the user having changed the project's URL. */
        void projectUrlChanged(@Nonnull String url);

        /** Performs any actions appropriate in response to the user having changed the project's description. */
        void projectDescriptionChanged(@Nonnull String projectDescriptionValue);

        /** Performs any actions appropriate in response to the user having changed the project's visibility. */
        void projectVisibilityChanged(boolean aPublic);

        /** Performs any actions appropriate in response to the user having clicked the 'LoadRepo' key. */
        void onLoadRepoClicked();

        /**
         * Performs any actions appropriate in response to the user having selected a repository.
         *
         * @param repository
         *         selected repository
         */
        void onRepositorySelected(@Nonnull ProjectData repository);

        /** Performs any actions appropriate in response to the user having changed account field. */
        void onAccountChanged();
    }

    /** Show the name error. */
    void showNameError();

    /** Hide the name error. */
    void hideNameError();

    /** Show URL error. */
    void showUrlError(@Nonnull String message);

    /** Hide URL error. */
    void hideUrlError();

    /**
     * Set the project's URL.
     *
     * @param url
     *         the project's URL to set
     */
    void setProjectUrl(@Nonnull String url);

    void setVisibility(boolean visible);

    /**
     * Get the project's name value.
     *
     * @return {@link String} project's name
     */
    @Nonnull
    String getProjectName();

    /**
     * Set the project's name value.
     *
     * @param projectName
     *         project's name to set
     */
    void setProjectName(@Nonnull String projectName);

    /**
     * Set the project's description value.
     *
     * @param projectDescription
     *         project's description to set
     */
    void setProjectDescription(@Nonnull String projectDescription);

    /** Give focus to project's URL input. */
    void focusInUrlInput();

    /**
     * Set the enable state of the inputs.
     *
     * @param isEnabled
     *         <code>true</code> if enabled, <code>false</code> if disabled
     */
    void setInputsEnableState(boolean isEnabled);

    /**
     * Set available repositories for account.
     *
     * @param repositories
     *         available repositories
     */
    void setRepositories(@Nonnull Array<ProjectData> repositories);

    /** @return account name */
    @Nonnull
    String getAccountName();

    /**
     * Set available account names.
     *
     * @param names
     *         available names
     */
    void setAccountNames(@Nonnull Array<String> names);

    /** Close github panel. */
    void closeGithubPanel();

    /** Show github panel. */
    void showGithubPanel();

    /** Reset the page. */
    void reset();

    /**
     * Set the visibility state of the loader.
     *
     * @param isVisible
     *         <code>true</code> if visible.
     */
    void setLoaderVisibility(boolean isVisible);

}
