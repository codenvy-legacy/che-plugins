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
package org.eclipse.che.ide.ext.runner.client.runneractions;

import org.eclipse.che.ide.ext.runner.client.models.Runner;

import javax.validation.constraints.NotNull;

/**
 * The general representation of runner manager action. It provides different actions which were bound to this action.
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public interface RunnerAction {

    /**
     * Perform any actions which were bound to this action.
     *
     * @param runner
     *         runner that execute this action
     */
    void perform(@NotNull Runner runner);

    /** Perform any actions which were bound to this action. */
    void perform();

    /** Stop all actions which were started for this action. Unsubscribe for all events and etc. */
    void stop();

    /**
     * Add a listener for detecting stop process of the current action.
     *
     * @param listener
     *         listener that has to detect stop process
     */
    void setListener(@NotNull StopActionListener listener);

    /** Remove a listener that detects a stop process of action. */
    void removeListener();

    interface StopActionListener {
        /** Perform any actions when an action was stopped. */
        void onStopAction();
    }

}