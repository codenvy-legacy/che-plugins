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
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class AbstractRunnerActionTest {

    @Mock
    private Runner runner;
    @Mock
    private RunnerAction action1;
    @Mock
    private RunnerAction action2;
    @Mock
    private RunnerAction.StopActionListener listener;

    private AbstractRunnerAction runnerAction;

    @Before
    public void setUp() {
        runnerAction = new DummyAction();
    }

    @Test
    public void shouldAddActionTest() {
        runnerAction.addAction(action1);
        verify(action1).setListener(runnerAction);
    }

    @Test
    public void shouldStopAllRunActionIfListenerIsNotNull() {
        runnerAction.setListener(listener);
        runnerAction.addAction(action1);
        runnerAction.addAction(action2);

        runnerAction.stop();

        verify(action1).removeListener();
        verify(action1).stop();

        verify(action2).removeListener();
        verify(action2).stop();

        verify(listener).onStopAction();
    }

    @Test
    public void shouldStopAllRunActionIfListenerIsNull() {
        runnerAction.setListener(isNull(RunnerAction.StopActionListener.class));
        runnerAction.addAction(action1);
        runnerAction.addAction(action2);

        runnerAction.stop();

        verify(action1).removeListener();
        verify(action1).stop();

        verify(action2).removeListener();
        verify(action2).stop();

        verify(listener, never()).onStopAction();
    }

    @Test
    public void shouldOnStopActionIfListenerIsNotNull() {
        runnerAction.setListener(listener);
        runnerAction.addAction(action1);
        runnerAction.addAction(action2);

        runnerAction.stop();

        verify(action1).removeListener();
        verify(action1).stop();

        verify(action2).removeListener();
        verify(action2).stop();

        verify(listener).onStopAction();
    }

    @Test
    public void shouldOnStopActionIfListenerIsNull() {
        runnerAction.setListener(isNull(RunnerAction.StopActionListener.class));
        runnerAction.addAction(action1);
        runnerAction.addAction(action2);

        runnerAction.onStopAction();

        verify(action1).removeListener();
        verify(action1).stop();

        verify(action2).removeListener();
        verify(action2).stop();

        verify(listener, never()).onStopAction();
    }

    @Test
    public void shouldSetAndRemoveListenerAndStopAction() {
        runnerAction.setListener(listener);
        runnerAction.addAction(action1);
        runnerAction.addAction(action2);
        runnerAction.removeListener();

        runnerAction.stop();

        verify(action1).removeListener();
        verify(action1).stop();

        verify(action2).removeListener();
        verify(action2).stop();

        verify(listener, never()).onStopAction();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldPerformTest() {
        runnerAction.perform();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldPerformWithRunnerTest() {
        runnerAction.perform(runner);
    }

    private class DummyAction extends AbstractRunnerAction {
        private DummyAction() {
        }
    }
}
