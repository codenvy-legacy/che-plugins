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
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class RunnerApplicationStatusEventTest {
    @Mock
    private Runner runner;

    @Mock
    private RunnerApplicationStatusEventHandler handler;

    private RunnerApplicationStatusEvent runnerApplicationStatusEvent;

    @Before
    public void setUp() {
        runnerApplicationStatusEvent = new RunnerApplicationStatusEvent(runner);
    }

    @Test
    public void shouldDispatch() {
        runnerApplicationStatusEvent.dispatch(handler);
        verify(handler).onRunnerStatusChanged(runner);
    }

    @Test
    public void shouldGetAssociatedType() throws Exception {
        assertThat(runnerApplicationStatusEvent.getAssociatedType(), is(RunnerApplicationStatusEvent.TYPE));
    }

}