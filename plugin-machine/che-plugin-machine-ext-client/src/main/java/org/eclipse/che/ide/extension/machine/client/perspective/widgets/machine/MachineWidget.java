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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine;

import com.google.gwt.user.client.ui.IsWidget;

import org.eclipse.che.ide.extension.machine.client.machine.Machine;

import javax.annotation.Nonnull;

/**
 * Provides methods to control displaying of machine.
 *
 * @author Dmitry Shnurenko
 */
public interface MachineWidget extends IsWidget {

    /**
     * Updates machine widget.
     *
     * @param machine
     *         machine which need update
     */
    void update(@Nonnull Machine machine);

    /** Adds special css styles which defines that machine is selected. */
    void select();

    /** Removes special css styles which defines that machine is unselected. */
    void unSelect();

    /**
     * Sets special delegate to control by widget in response on user's actions.
     *
     * @param delegate
     *         delegate which need set
     */
    void setDelegate(@Nonnull ActionDelegate delegate);

    interface ActionDelegate {
        /**
         * Performs some actions when user click on machine widget.
         *
         * @param machine
         *         machine which was selected
         */
        void onMachineClicked(@Nonnull Machine machine);
    }
}
