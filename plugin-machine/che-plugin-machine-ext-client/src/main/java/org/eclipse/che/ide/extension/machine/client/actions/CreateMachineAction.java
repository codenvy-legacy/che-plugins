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
package org.eclipse.che.ide.extension.machine.client.actions;

import com.google.inject.Inject;

import org.eclipse.che.ide.api.action.AbstractPerspectiveAction;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel.MachinePanelPresenter;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static org.eclipse.che.ide.extension.machine.client.perspective.MachinePerspective.MACHINE_PERSPECTIVE_ID;

/**
 * The action contains business logic which calls special method to create machine.
 *
 * @author Dmitry Shnurenko
 */
public class CreateMachineAction extends AbstractPerspectiveAction {

    private final MachinePanelPresenter panelPresenter;

    @Inject
    public CreateMachineAction(MachineLocalizationConstant locale,
                               MachinePanelPresenter panelPresenter) {
        super(Arrays.asList(MACHINE_PERSPECTIVE_ID), locale.machineCreate(), locale.machineCreate(), null, null);

        this.panelPresenter = panelPresenter;
    }

    /** {@inheritDoc} */
    @Override
    public void updateInPerspective(@Nonnull ActionEvent event) {
        //to do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(@Nonnull ActionEvent event) {
        panelPresenter.createMachine();
    }
}
