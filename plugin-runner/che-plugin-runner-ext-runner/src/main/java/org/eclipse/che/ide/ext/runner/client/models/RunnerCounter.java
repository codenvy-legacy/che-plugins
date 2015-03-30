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
package org.eclipse.che.ide.ext.runner.client.models;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The class is counter for runner widgets.
 *
 * @author Valeriy Svydenko
 */
@Singleton
public class RunnerCounter {
    private int runnerNumber;

    @Inject
    public RunnerCounter() {
        reset();
    }

    /** @return next number of the new runner */
    public int getRunnerNumber() {
        return runnerNumber++;
    }

    /** Reset count of the runners */
    public void reset() {
        runnerNumber = 1;
    }
}