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
package org.eclipse.che.ide.extension.machine.client.actions;

import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel.MachinePanelPresenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class DestroyMachineActionTest {
    @Mock
    private MachineLocalizationConstant locale;
    @Mock
    private MachinePanelPresenter       panelPresenter;
    @Mock
    private ActionEvent                 event;
    @Mock
    private MachineManager              machineManager;

    @InjectMocks
    private DestroyMachineAction action;

    @Test
    public void constructorShouldBeVerified() {
        verify(locale).machineDestroyTitle();
        verify(locale).machineDestroyDescription();
    }

    @Test
    public void actionShouldBePerformed() {
        final Machine machine = mock(Machine.class);
        final String id = "ID";
        when(machine.getId()).thenReturn(id);
        when(panelPresenter.getSelectedMachine()).thenReturn(machine);

        action.actionPerformed(event);

        verify(machineManager).destroyMachine(eq(id));
    }
}