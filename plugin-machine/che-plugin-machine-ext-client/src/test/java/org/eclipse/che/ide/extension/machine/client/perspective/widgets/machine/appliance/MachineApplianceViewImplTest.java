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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance;

import com.google.gwt.user.client.ui.Label;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.container.TabContainerView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class MachineApplianceViewImplTest {

    private static final String SOME_TEXT = "someText";

    //constructor mocks
    @Mock
    private TabContainerView            tabContainerView;
    @Mock
    private Label                       unavailableLabel;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MachineResources            resources;
    @Mock
    private MachineLocalizationConstant locale;

    private MachineApplianceViewImpl view;

    @Before
    public void setUp() {
        when(resources.getCss().unavailableLabel()).thenReturn(SOME_TEXT);
        when(locale.unavailableMachineInfo()).thenReturn(SOME_TEXT);

        view = new MachineApplianceViewImpl(resources, unavailableLabel, locale);
    }

    @Test
    public void constructorShouldBeVerified() {
        verify(resources.getCss()).unavailableLabel();
        verify(unavailableLabel).addStyleName(SOME_TEXT);
        verify(locale).unavailableMachineInfo();
        verify(unavailableLabel).setText(SOME_TEXT);
    }

    @Test
    public void tabContainerShouldBeAddedWhenViewIsNotNull() {
        view.showContainer(tabContainerView);

        verify(view.container).setWidget(tabContainerView);
        verify(view.container, never()).setWidget(unavailableLabel);
    }

    @Test
    public void tabContainerShouldBeAddedWhenViewIsNull() {
        view.showContainer(null);

        verify(view.container, never()).setWidget(tabContainerView);
    }
}