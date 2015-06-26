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
package org.eclipse.che.ide.extension.machine.client.machine.create;

import org.eclipse.che.ide.api.mvp.View;

/**
 * The view of {@link CreateMachinePresenter}.
 *
 * @author Artem Zatsarynnyy
 */
public interface CreateMachineView extends View<CreateMachineView.ActionDelegate> {

    /** Show view. */
    void show();

    /** Close view. */
    void close();

    /** Returns machine name. */
    String getMachineName();

    /**
     * Sets whether 'Create' button is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the button,
     *         <code>false</code> to disable it
     */
    void setCreateButtonState(boolean enabled);

    /**
     * Sets whether 'Replace' button is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the button,
     *         <code>false</code> to disable it
     */
    void setReplaceButtonState(boolean enabled);

    /** Action handler for the view actions/controls. */
    interface ActionDelegate {

        /** Called when machines name has been changed. */
        void onNameChanged();

        /** Called when 'Create' button has been clicked. */
        void onCreateClicked();

        /** Called when 'Replace Dev Machine' button has been clicked. */
        void onReplaceDevMachineClicked();

        /** Called when 'Cancel' button has been clicked. */
        void onCancelClicked();
    }
}
