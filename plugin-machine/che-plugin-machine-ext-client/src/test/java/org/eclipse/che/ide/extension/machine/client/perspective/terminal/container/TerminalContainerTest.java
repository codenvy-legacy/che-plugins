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
package org.eclipse.che.ide.extension.machine.client.perspective.terminal.container;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.eclipse.che.ide.extension.machine.client.inject.factories.TerminalFactory;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStateEvent;
import org.eclipse.che.ide.extension.machine.client.perspective.terminal.TerminalPresenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class TerminalContainerTest {

    //constructor mocks
    @Mock
    private TerminalContainerView view;
    @Mock
    private TerminalFactory       terminalFactory;

    @Mock
    private Machine           machine;
    @Mock
    private TerminalPresenter terminal;
    @Mock
    private AcceptsOneWidget  oneWidget;
    @Mock
    private MachineStateEvent machineStateEvent;

    @InjectMocks
    private TerminalContainer container;

    @Test
    public void terminalShouldBeAdded() {
        when(terminalFactory.create(machine)).thenReturn(terminal);

        container.addOrShowTerminal(machine);

        verify(terminalFactory).create(machine);
        verify(view).addTerminal(terminal);

        verify(terminal, never()).connect();
        verify(view, never()).showTerminal(terminal);
    }

    @Test
    public void terminalShouldBeShown() {
        when(terminalFactory.create(machine)).thenReturn(terminal);

        container.addOrShowTerminal(machine);
        reset(view, terminalFactory);

        container.addOrShowTerminal(machine);

        verify(terminal).connect();
        verify(view).showTerminal(terminal);

        verify(terminalFactory, never()).create(machine);
        verify(view, never()).addTerminal(terminal);
    }

    @Test
    public void visibilityShouldBeChanged() {
        container.setVisible(true);

        verify(view).setVisible(true);
    }

    @Test
    public void widgetShouldBeSetToContainer() {
        container.go(oneWidget);

        verify(oneWidget).setWidget(view);
    }

    @Test
    public void onMachineShouldBeDestroyed() {
        when(terminalFactory.create(machine)).thenReturn(terminal);
        when(machineStateEvent.getMachine()).thenReturn(machine);

        container.addOrShowTerminal(machine);

        verify(terminalFactory).create(machine);
        reset(terminalFactory);

        when(terminalFactory.create(machine)).thenReturn(terminal);

        container.onMachineDestroyed(machineStateEvent);

        container.addOrShowTerminal(machine);

        verify(terminalFactory).create(machine);
    }
}