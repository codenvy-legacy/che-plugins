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
package org.eclipse.che.ide.ext.runner.client.tabs.container;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ext.runner.client.tabs.common.Tab;

import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * The abstract representation of tab container UI part.
 *
 * @author Andrey Plotnikov
 */
@ImplementedBy(TabContainerViewImpl.class)
public interface TabContainerView extends View<TabContainerView.ActionDelegate> {

    /**
     * Insert a given tab in the special container for it.
     *
     * @param tab
     *         tab that needs to be shown
     */
    void showTab(@Nonnull Tab tab);

    /**
     * Change visibility state of tab's titles.
     *
     * @param tabVisibilities
     *         visibility states for all tabs
     */
    void setVisibleTitle(@Nonnull Map<String, Boolean> tabVisibilities);

    /**
     * Add tab's title in the special container for it.
     *
     * @param tab
     *         tab that needs to be added
     */
    void addTab(@Nonnull Tab tab);

    /**
     * Select a given tab.
     *
     * @param tab
     *         tab that needs to be selected
     */
    void selectTab(@Nonnull Tab tab);

    interface ActionDelegate {
        /**
         * Performs any actions in response of user's clicking.
         *
         * @param title
         *         title of clicked tab
         */
        void onTabClicked(@Nonnull String title);
    }

}