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
package org.eclipse.che.ide.ext.runner.client.tabs.common.item;

import com.google.gwt.user.client.ui.IsWidget;

import javax.validation.constraints.NotNull;

/**
 * Provides methods which are general for runner and environment widget.
 *
 * @author Dmitry Shnurenko
 */
public interface RunnerItems<T> extends IsWidget {

    /** Performs some action when widget is selected. */
    void select();

    /** Performs some action when widget is unselected. */
    void unSelect();

    /**
     * Updates state of runner or environment widget.
     *
     * @param item
     *         runner or environment item which need update
     */
    void update(@NotNull T item);
}
