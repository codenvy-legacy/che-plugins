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

import org.eclipse.che.api.machine.shared.dto.MachineStateDto;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.project.node.interceptor.NodeInterceptor;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel.MachinePanelView.ActionDelegate;
import org.eclipse.che.ide.ui.tree.Tree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class MachinePanelViewImplTest {

    private static final String SOME_TEXT = "someText";
    private Set<NodeInterceptor> nodeInterceptorSet;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private PartStackUIResources          partStackResources;

//    @Mock
//    private ActionDelegate                delegate;
    @Mock
    private Tree.Css                      css;
    @Mock
    private MachineStateDto               machineState;
    @Mock
    private MachineNode                   machineNode;

    private MachinePanelViewImpl view;

    @Before
    public void setUp() {
        nodeInterceptorSet = new HashSet<>();
        NodeInterceptor nodeInterceptor = mock(NodeInterceptor.class);
        nodeInterceptorSet.add(nodeInterceptor);
//        when(partStackResources.partStackCss().ideBasePartToolbar()).thenReturn(SOME_TEXT);
//        when(partStackResources.partStackCss().ideBasePartTitleLabel()).thenReturn(SOME_TEXT);

//        when(resources.treeCss()).thenReturn(css);

        view = new MachinePanelViewImpl(partStackResources, nodeInterceptorSet);

        //view.setDelegate(delegate);
    }

    @Test
    public void nodeShouldBeSelected() {
//        when(machineNode.getData()).thenReturn(machineState);
//
//        view.selectNode(machineNode);
//
//        verify(machineNode).getData();
    }

    @Test
    public void nodeShouldNotBeSelected() {
//        view.selectNode(null);
//
//        verify(machineNode, never()).getData();
    }
}