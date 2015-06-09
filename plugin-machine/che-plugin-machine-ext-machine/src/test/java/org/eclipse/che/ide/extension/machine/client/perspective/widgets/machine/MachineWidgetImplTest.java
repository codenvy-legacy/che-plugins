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

import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.MachineWidget.ActionDelegate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGResource;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.never;
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
    private Machine         machine;
    @Mock
    private ClickEvent      clickEvent;
    @Mock
    private ActionDelegate  delegate;
    @Mock
    private SVGResource     svgResource;
    @Mock
    private OMSVGSVGElement icon;

    //constructor mock
    @Mock(answer = RETURNS_DEEP_STUBS)
    private MachineResources resources;

    private MachineWidgetImpl view;

    @Before
    public void setUp() {
        when(resources.tick()).thenReturn(svgResource);
        when(svgResource.getSvg()).thenReturn(icon);
        when(machine.getId()).thenReturn(SOME_TEXT);

        view = new MachineWidgetImpl(resources);
        view.setDelegate(delegate);
    }

    @Test
    public void constructorShouldBeVerified() {
        verify(resources).tick();
    }

    @Test
    public void machineShouldBeUpdated() {
        when(machine.getDisplayName()).thenReturn(SOME_TEXT);

        view.update(machine);

        verify(view.rightIcon, never()).getElement();
        verify(machine).getDisplayName();
        verify(view.name).setText(SOME_TEXT);
    }

    @Test
    public void machineShouldBeUpdatedWhenNoDisplayName() {
        view.update(machine);

        verify(view.rightIcon, never()).getElement();
        verify(machine).getId();
        verify(view.name).setText(SOME_TEXT);
    }

    @Test
    public void rightIconShouldBeShown() {
        when(machine.isWorkspaceBound()).thenReturn(true);

        view.update(machine);

        verify(machine).isWorkspaceBound();
        verify(view.rightIcon).getElement();
        verify(view.name).setText(SOME_TEXT);
    }

    @Test
    public void widgetShouldBeSelected() {
        view.select();

        verify(resources.getCss()).selectMachine();
    }

    @Test
    public void widgetShouldBeUnSelected() {
        view.unSelect();

        verify(resources.getCss()).selectMachine();
    }

    @Test
    public void onMachineWidgetShouldBeClicked() {
        view.update(machine);

        view.onClick(clickEvent);

        verify(delegate).onMachineClicked(machine);
    }
}