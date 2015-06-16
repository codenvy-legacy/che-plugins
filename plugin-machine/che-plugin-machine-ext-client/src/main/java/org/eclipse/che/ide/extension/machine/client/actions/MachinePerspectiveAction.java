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

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.parts.PerspectiveManager;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;

import static org.eclipse.che.ide.extension.machine.client.perspective.MachinePerspective.MACHINE_PERSPECTIVE_ID;

/**
 * Special action which allows set machine perspective.
 *
 * @author Dmitry Shnurenko
 */
public class MachinePerspectiveAction extends Action {

    private final PerspectiveManager perspectiveManager;

    @Inject
    public MachinePerspectiveAction(PerspectiveManager perspectiveManager, Resources resources, MachineLocalizationConstant locale) {
        //TODO need change icon
        super(locale.perspectiveActionDescription(), locale.perspectiveActionTooltip(), null, resources.closeProject());

        this.perspectiveManager = perspectiveManager;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent event) {
        String currentPerspectiveId = perspectiveManager.getPerspectiveId();

        boolean isMachinePerspective = currentPerspectiveId.equals(MACHINE_PERSPECTIVE_ID);

        event.getPresentation().setEnabled(!isMachinePerspective);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent event) {
        event.getPresentation().setEnabled(false);

        perspectiveManager.setPerspectiveId(MACHINE_PERSPECTIVE_ID);
    }
}
