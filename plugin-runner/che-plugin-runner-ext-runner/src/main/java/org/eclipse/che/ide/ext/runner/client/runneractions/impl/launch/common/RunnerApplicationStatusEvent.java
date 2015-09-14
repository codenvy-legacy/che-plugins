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
import com.google.web.bindery.event.shared.Event;

import javax.validation.constraints.NotNull;

/**
 * Client event sent by the runner extension when running application status is updated.
 *
 * @author Sun Tan
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public class RunnerApplicationStatusEvent extends Event<RunnerApplicationStatusEventHandler> {

    public static final Type<RunnerApplicationStatusEventHandler> TYPE = new Type<>();

    private final Runner runner;

    public RunnerApplicationStatusEvent(@NotNull Runner runner) {
        this.runner = runner;
    }

    /** {@inheritDoc} */
    @Override
    public Type<RunnerApplicationStatusEventHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(RunnerApplicationStatusEventHandler handler) {
        handler.onRunnerStatusChanged(runner);
    }

}