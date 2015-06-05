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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel;

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.MachineWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class MachinePanelViewImplTest {

    private static final String SOME_TEXT = "someText";

    @Mock(answer = RETURNS_DEEP_STUBS)
    private PartStackUIResources resources;

    @Mock
    private MachineWidget machineWidget;

    private MachinePanelViewImpl view;

    @Before
    public void setUp() {
        when(resources.partStackCss().ideBasePartToolbar()).thenReturn(SOME_TEXT);
        when(resources.partStackCss().ideBasePartTitleLabel()).thenReturn(SOME_TEXT);

        view = new MachinePanelViewImpl(resources);
    }

    @Test
    public void widgetShouldBeAdded() {
        view.add(machineWidget);

        verify(view.machines).add(machineWidget);
    }

    @Test
    public void panelShouldBeCleared() {
        view.clear();

        verify(view.machines).clear();
    }

}