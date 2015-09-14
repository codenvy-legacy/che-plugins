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
package org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.common;

import org.eclipse.che.ide.ext.runner.client.models.Runner;

import javax.validation.constraints.NotNull;

/**
 * Handler to listen to runner extension {@link RunnerApplicationStatusEvent} events.
 *
 * @author Sun Tan
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public interface RunnerApplicationStatusEventHandler {
    /**
     * Performs any actions when a runner is changed its state.
     *
     * @param runner
     *         current runner
     */
    void onRunnerStatusChanged(@NotNull Runner runner);
}