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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.MachineWidget;

import javax.annotation.Nonnull;

/**
 * Provides methods to control view representation of machine panel.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(MachinePanelImpl.class)
public interface MachinePanel extends View<MachinePanel.ActionDelegate> {

    /**
     * Adds machine widget in special place on view.
     *
     * @param machineWidget
     *         widget which need add
     */
    void add(@Nonnull MachineWidget machineWidget);

    /** Clears machine panel. */
    void clear();

    interface ActionDelegate extends BaseActionDelegate {
        /** Performs some actions when user click on Create machine button. */
        void onCreateMachineButtonClicked();

        /** Performs some actions when user click on Delete machine button. */
        void onDestroyMachineButtonClicked();
    }
}
