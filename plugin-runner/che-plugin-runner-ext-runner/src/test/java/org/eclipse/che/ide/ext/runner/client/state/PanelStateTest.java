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
public class PanelStateTest {

    @Mock
    private PanelState.StateChangeListener listener1;
    @Mock
    private PanelState.StateChangeListener listener2;

    private PanelState panelState;

    @Before
    public void setUp() {
        panelState = new PanelState();

        panelState.addListener(listener1);
        panelState.addListener(listener2);
    }

    @Test
    public void shouldDefaultGetState() {
        assertThat(panelState.getState(), is(State.RUNNERS));
    }

    @Test
    public void shouldGetStateTemplate() {
        panelState.setState(State.TEMPLATE);
        assertThat(panelState.getState(), is(State.TEMPLATE));
    }

    @Test
    public void shouldGetStateHistory() {
        panelState.setState(State.RUNNERS);
        assertThat(panelState.getState(), is(State.RUNNERS));
    }

    @Test
    public void shouldSetStateHistory() {
        panelState.setState(State.RUNNERS);

        assertThat(panelState.getState(), is(State.RUNNERS));
        verify(listener1).onStateChanged();
        verify(listener2).onStateChanged();
    }

    @Test
    public void shouldSetStateTemplate() {
        panelState.setState(State.TEMPLATE);

        assertThat(panelState.getState(), is(State.TEMPLATE));
        verify(listener1).onStateChanged();
        verify(listener2).onStateChanged();
    }

}