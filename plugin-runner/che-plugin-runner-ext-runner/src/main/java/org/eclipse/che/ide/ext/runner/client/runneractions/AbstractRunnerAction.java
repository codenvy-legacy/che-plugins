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
import java.util.ArrayList;
import java.util.List;

/**
 * The abstract implementation of action. It provides general behaviour (e.g., add stop process listener, remove stop process listener,
 * default implementation of stop process etc).
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public abstract class AbstractRunnerAction implements RunnerAction, RunnerAction.StopActionListener {

    private final List<RunnerAction> actions;
    private       StopActionListener listener;

    protected AbstractRunnerAction() {
        this.actions = new ArrayList<>();
    }

    /**
     * Add a sub-action to this action. This means that this sub-action will be stopped when root action is stopping.
     *
     * @param action
     *         sub-action that needs to be added
     */
    protected void addAction(@NotNull RunnerAction action) {
        actions.add(action);
        action.setListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        for (RunnerAction action : actions) {
            action.removeListener();
            action.stop();
        }

        if (listener == null) {
            return;
        }

        listener.onStopAction();
    }

    /** {@inheritDoc} */
    @Override
    public void setListener(@NotNull StopActionListener listener) {
        this.listener = listener;
    }

    /** {@inheritDoc} */
    @Override
    public void removeListener() {
        listener = null;
    }

    /** {@inheritDoc} */
    @Override
    public void onStopAction() {
        stop();
    }

    /** {@inheritDoc} */
    @Override
    public void perform() {
        throw new UnsupportedOperationException("Not supported");
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@NotNull Runner runner) {
        throw new UnsupportedOperationException("Not supported");
    }

}