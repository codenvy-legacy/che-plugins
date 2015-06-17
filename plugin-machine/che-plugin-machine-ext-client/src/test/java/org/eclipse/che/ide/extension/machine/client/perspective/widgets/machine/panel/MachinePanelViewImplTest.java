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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel.MachinePanelView.ActionDelegate;
import org.eclipse.che.ide.ui.tree.Tree;
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
    private PartStackUIResources          partStackResources;
    @Mock
    private org.eclipse.che.ide.Resources resources;
    @Mock
    private MachineDataAdapter            adapter;
    @Mock
    private MachineTreeRenderer           renderer;
    @Mock
    private ActionDelegate                delegate;
    @Mock
    private Tree.Css                      css;
    @Mock
    private ClickEvent                    clickEvent;

    private MachinePanelViewImpl view;

    @Before
    public void setUp() {
        when(partStackResources.partStackCss().ideBasePartToolbar()).thenReturn(SOME_TEXT);
        when(partStackResources.partStackCss().ideBasePartTitleLabel()).thenReturn(SOME_TEXT);

        when(resources.treeCss()).thenReturn(css);

        view = new MachinePanelViewImpl(resources, partStackResources, adapter, renderer);

        view.setDelegate(delegate);
    }

    @Test
    public void onCreateMachineShouldBeClicked() {
        view.onCreateMachineClicked(clickEvent);

        verify(delegate).onCreateMachineButtonClicked();
    }

    @Test
    public void onDestroyMachineShouldBeClicked() {
        view.onDestroyMachineClicked(clickEvent);

        verify(delegate).onDestroyMachineButtonClicked();
    }

}