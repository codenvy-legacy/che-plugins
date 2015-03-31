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
package org.eclipse.che.ide.ext.git.client.remote.add;

import org.eclipse.che.ide.api.mvp.View;

import javax.annotation.Nonnull;

/**
 * The view of {@link AddRemoteRepositoryPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface AddRemoteRepositoryView extends View<AddRemoteRepositoryView.ActionDelegate> {
    /** Needs for delegate some function into AddRemoteRepository view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Ok button. */
        void onOkClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
    }

    /** @return repository name */
    @Nonnull
    String getName();

    /**
     * Set value of name field.
     *
     * @param name
     *         repository name
     */
    void setName(@Nonnull String name);

    /** @return repository url */
    @Nonnull
    String getUrl();

    /**
     * Set value of url field.
     *
     * @param url
     *         repository url
     */
    void setUrl(@Nonnull String url);

    /**
     * Change the enable state of the ok button.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableOkButton(boolean enable);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}