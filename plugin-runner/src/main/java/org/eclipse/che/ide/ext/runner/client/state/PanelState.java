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
package org.eclipse.che.ide.ext.runner.client.state;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The manager for panel state. It provides an ability to change 'Multi panel' state.
 *
 * @author Andrey Plotnikov
 */
@Singleton
public class PanelState {

    private final List<StateChangeListener> listeners;
    private       State                     state;

    @Inject
    public PanelState() {
        listeners = new ArrayList<>();
        state = State.RUNNERS;
    }

    /** @return current state of the panel */
    @Nonnull
    public State getState() {
        return state;
    }

    /**
     * Changes state of the panel
     *
     * @param state
     *         sate that needs to be applied
     */
    public void setState(@Nonnull State state) {
        this.state = state;
        notifyListeners();
    }

    /**
     * Adds a listener for detecting state changing.
     *
     * @param listener
     *         listener that needs to be added
     */
    public void addListener(@Nonnull StateChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (StateChangeListener listener : listeners) {
            listener.onStateChanged();
        }
    }

    public interface StateChangeListener {
        /** Perform any actions when panel state is changed. */
        void onStateChanged();
    }

}