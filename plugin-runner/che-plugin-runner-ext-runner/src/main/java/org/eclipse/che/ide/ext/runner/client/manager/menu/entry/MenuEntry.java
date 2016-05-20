/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.runner.client.manager.menu.entry;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;

/**
 * The interface which provides methods which allow change behaviour of the widget and answer on user actions(clicks). This widget
 * is added in special container {@link org.eclipse.che.ide.ext.runner.client.manager.menu.MenuWidget}
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(MenuEntryWidget.class)
public interface MenuEntry extends View<MenuEntry.ActionDelegate> {

    public interface ActionDelegate {
        /**
         * Performs some actions when user click on entry.
         *
         * @param isSplitterShow
         *         <code>true</code> splitter is shown,<code>false</code> splitter isn't shown
         */
        void onEntryClicked(boolean isSplitterShow);
    }
}
