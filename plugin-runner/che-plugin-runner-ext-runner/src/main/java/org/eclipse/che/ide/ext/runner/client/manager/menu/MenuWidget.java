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
package org.eclipse.che.ide.ext.runner.client.manager.menu;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.ext.runner.client.manager.menu.entry.MenuEntry;

import javax.validation.constraints.NotNull;

/**
 * The interface provides methods to control header menu.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(MenuWidgetImpl.class)
public interface MenuWidget extends IsWidget {

    /** Returns special span panel. It is needed to catch click events beside entry to hide this menu. */
    SimplePanel getSpan();

    /**
     * Adds entry to menu widget.
     *
     * @param entry
     *         entry which need add
     */
    void addEntry(@NotNull MenuEntry entry);
}
