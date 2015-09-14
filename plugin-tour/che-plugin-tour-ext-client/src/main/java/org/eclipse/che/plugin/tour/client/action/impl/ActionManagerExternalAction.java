/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package org.eclipse.che.plugin.tour.client.action.impl;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.plugin.tour.client.action.ExternalAction;

import javax.validation.constraints.NotNull;
import javax.inject.Inject;

/**
 * External actions for action manager using trigger keyword
 * @author Florent Benoit
 */
public class ActionManagerExternalAction implements ExternalAction {

    /**
     * Action manager used to check and execute actions
     */
    @Inject
    private ActionManager actionManager;


    /**
     * Accepts only "trigger" category
     * @param category which should be "trigger" to be accepted
     * @return true if category is trigger
     */
    @Override
    public boolean accept(String category) {
        return "trigger".equals(category);
    }

    /**
     * Executes the given actionID on ActionManager
     * @param actionId the id of action
     */
    @Override
    public void execute(@NotNull String actionId) {
        Action action = actionManager.getAction(actionId);
        if (action != null) {
            ActionEvent e = new ActionEvent("", new Presentation(), actionManager, 0);
            action.actionPerformed(e);
        }
    }
}
