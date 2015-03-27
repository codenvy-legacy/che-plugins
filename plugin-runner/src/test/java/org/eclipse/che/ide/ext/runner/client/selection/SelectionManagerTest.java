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

import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.models.Runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * @author Andrienk Alexander
 */
@RunWith(MockitoJUnitRunner.class)
public class SelectionManagerTest {

    @Mock
    private SelectionManager.SelectionChangeListener listener1;
    @Mock
    private SelectionManager.SelectionChangeListener listener2;
    @Mock
    private Runner                                   runner;
    @Mock
    private Environment                              environment;

    @InjectMocks
    private SelectionManager selectionManager;

    @Test
    public void shouldSetRunner() {
        selectionManager.addListener(listener1);
        selectionManager.addListener(listener2);

        selectionManager.setRunner(runner);

        verify(listener1).onSelectionChanged(Selection.RUNNER);
        verify(listener2).onSelectionChanged(Selection.RUNNER);
    }

    @Test
    public void shouldSetEnvironment() {
        selectionManager.addListener(listener1);
        selectionManager.addListener(listener2);

        selectionManager.setEnvironment(environment);

        verify(listener1).onSelectionChanged(Selection.ENVIRONMENT);
        verify(listener2).onSelectionChanged(Selection.ENVIRONMENT);
    }

    @Test
    public void shouldGetRunner() {
        selectionManager.setRunner(runner);
        assertThat(runner, is(equalTo(selectionManager.getRunner())));
    }

    @Test
    public void shouldGetEnvironment() {
        selectionManager.setEnvironment(environment);
        assertThat(environment, is(equalTo(selectionManager.getEnvironment())));
    }

}