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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class RunnerCounterTest {
    @InjectMocks
    private RunnerCounter runnerCounter;

    @Test
    public void prepareActionShouldBePerformed() {
        assertThat(runnerCounter.getRunnerNumber(), is(1));
    }

    @Test
    public void shouldGetRunnerNumberFewTimes() {
        assertThat(runnerCounter.getRunnerNumber(), is (1));
        assertThat(runnerCounter.getRunnerNumber(), is (2));
        assertThat(runnerCounter.getRunnerNumber(), is (3));
    }

    @Test
    public void shouldReset() {
        runnerCounter.getRunnerNumber();
        runnerCounter.getRunnerNumber();
        runnerCounter.getRunnerNumber();

        runnerCounter.reset();

        assertThat(runnerCounter.getRunnerNumber(), is(1));
    }

}