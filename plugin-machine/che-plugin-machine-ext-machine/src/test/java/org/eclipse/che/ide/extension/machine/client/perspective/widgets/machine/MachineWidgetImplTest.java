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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.MachineWidget.ActionDelegate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class MachineWidgetImplTest {

    private static final String SOME_TEXT = "someText";

    //additional mocks
    @Mock
    private Machine        machine;
    @Mock
    private ClickEvent     clickEvent;
    @Mock
    private ActionDelegate delegate;

    @InjectMocks
    private MachineWidgetImpl view;

    @Before
    public void setUp() {
        when(machine.getId()).thenReturn(SOME_TEXT);

        view.setDelegate(delegate);
    }

    @Test
    public void machineShouldBeUpdated() {
        view.update(machine);

        verify(machine).getId();
        verify(view.name).setText(SOME_TEXT);
    }

    @Test
    public void onMachineWidgetShouldBeClicked() {
        view.update(machine);

        view.onClick(clickEvent);

        verify(delegate).onMachineClicked(machine);
    }
}