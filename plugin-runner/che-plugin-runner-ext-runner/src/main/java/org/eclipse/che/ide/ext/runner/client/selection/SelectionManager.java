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
package org.eclipse.che.ide.ext.runner.client.selection;

import org.eclipse.che.api.project.shared.dto.RunnerEnvironment;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.models.Environment;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * The manager that manages different selection element in 'Multi runner' panel.
 *
 * @author Andrey Plotnikov
 */
@Singleton
public class SelectionManager {

    private final List<SelectionChangeListener> listeners;
    private       Runner                        selectedRunner;
    private       Environment                   selectedEnvironment;

    @Inject
    public SelectionManager() {
        listeners = new ArrayList<>();
    }

    /** @return an instance of {@link Runner} that is selected */
    @Nullable
    public Runner getRunner() {
        return selectedRunner;
    }

    /**
     * Select a new runner.
     *
     * @param runner
     *         runner that needs to be selected
     */
    public void setRunner(@Nullable Runner runner) {
        this.selectedRunner = runner;
        notifyListeners(Selection.RUNNER);
    }

    /** @return an instance of {@link RunnerEnvironment} that is selected */
    @Nullable
    public Environment getEnvironment() {
        return selectedEnvironment;
    }

    /**
     * Select a new environment.
     *
     * @param environment
     *         environment that needs to be selected
     */
    public void setEnvironment(@Nullable Environment environment) {
        this.selectedEnvironment = environment;
        notifyListeners(Selection.ENVIRONMENT);
    }

    /**
     * Adds a new listener for detecting changes in the manager.
     *
     * @param listener
     *         listener that needs to be added
     */
    public void addListener(@NotNull SelectionChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(@NotNull Selection selection) {
        for (SelectionChangeListener listener : listeners) {
            listener.onSelectionChanged(selection);
        }
    }

    public interface SelectionChangeListener {
        /**
         * Perform any actions when selection element is changed.
         *
         * @param selection
         *         type of element that is changed
         */
        void onSelectionChanged(@NotNull Selection selection);
    }

}