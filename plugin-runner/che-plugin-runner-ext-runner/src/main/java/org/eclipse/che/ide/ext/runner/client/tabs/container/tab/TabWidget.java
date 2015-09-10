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
package org.eclipse.che.ide.ext.runner.client.tabs.container.tab;

import org.eclipse.che.ide.api.mvp.View;

import javax.validation.constraints.NotNull;

/**
 * Provides methods which allow change visual representation of tab.
 *
 * @author Dmitry Shnurenko
 */
public interface TabWidget extends View<TabWidget.ActionDelegate> {

    /**
     * Performs some actions when tab is selected.
     *
     * @param background
     *         parameter which need to set correct background color
     */
    void select(@NotNull Background background);

    /** Performs some actions when tab is unselected. */
    void unSelect();

    /**
     * Sets visibility of widget;
     *
     * @param visible
     *         <code>true</code> widget is shown, <code>false</code> widget isn't shown
     */
    void setVisible(boolean visible);

    interface ActionDelegate {
        /** Performs some actions in response to user's clicking on the tab. */
        void onMouseClicked();
    }

}