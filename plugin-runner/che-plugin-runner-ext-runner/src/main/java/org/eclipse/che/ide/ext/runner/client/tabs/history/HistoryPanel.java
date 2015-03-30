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

import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.tabs.common.TabPresenter;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * Provides methods which allow work with history panel.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(HistoryPresenter.class)
public interface HistoryPanel extends TabPresenter {

    /**
     * The method creates special widget for current runner and adds it on history panel.
     *
     * @param runner
     *         runner which need add
     */
    void addRunner(@Nonnull Runner runner);

    /**
     * The method update state of current runner.
     *
     * @param runner
     *         runner which need update
     */
    void update(@Nonnull Runner runner);

    /**
     * Selects runner widget using current runner.
     *
     * @param runner
     *         runner which was selected
     */
    void selectRunner(@Nonnull Runner runner);

    /** Clears runner widgets. */
    void clear();
}