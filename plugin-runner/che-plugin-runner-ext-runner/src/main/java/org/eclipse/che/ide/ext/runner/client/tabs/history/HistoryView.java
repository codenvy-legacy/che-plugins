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
package org.eclipse.che.ide.ext.runner.client.tabs.history;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ext.runner.client.tabs.history.runner.RunnerWidget;

import javax.validation.constraints.NotNull;

/**
 * Provides methods which allow change history panel.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@ImplementedBy(HistoryViewImpl.class)
public interface HistoryView extends View<HistoryView.ActionDelegate> {

    /**
     * Adds runner on panel and update runner's state.
     *
     * @param runnerWidget
     *         runner which was added
     */
    void addRunner(@NotNull RunnerWidget runnerWidget);

    /**
     * Removes runner from panel.
     *
     * @param runnerWidget
     *         widget which need remove
     */
    void removeRunner(@NotNull RunnerWidget runnerWidget);

    /**
     * Sets visibility state to panel.
     *
     * @param isVisible
     *         <code>true</code> panel is visible, <code>false</code> panel is un visible
     */
    void setVisible(boolean isVisible);

    /** Clears runner widgets. */
    void clear();

    interface ActionDelegate {
        /** Cleans all inactive runners. */
        void cleanInactiveRunners();
    }
}