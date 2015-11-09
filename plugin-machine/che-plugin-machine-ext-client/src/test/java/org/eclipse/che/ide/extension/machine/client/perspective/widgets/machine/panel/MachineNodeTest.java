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

import org.eclipse.che.api.machine.shared.dto.MachineStateDto;
import org.eclipse.che.ide.ui.smartTree.presentation.NodePresentation;
import org.eclipse.che.ide.ui.tree.TreeNodeElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/**
 * @author Dmitry Shnurenko
 * @author Alexander Andrienko
 */
@RunWith(MockitoJUnitRunner.class)
public class MachineNodeTest {

    private final static String SOME_TEXT = "someText";

    @Mock
    private MachineNode                  parent;
    @Mock
    private MachineStateDto              data;
    @Mock
    private Collection<MachineNode>      children;
    @Mock
    private TreeNodeElement<MachineNode> treeNodeElement;

    private MachineNode treeNode;

    @Before
    public void setUp() {
        when(data.getId()).thenReturn(SOME_TEXT);
        when(data.getName()).thenReturn(SOME_TEXT);
        
        treeNode = new MachineNode(data);
    }

    @Test
    public void constructorShouldBeVerified() {
        verify(data).getId();
        verify(data).getName();

        assertThat(treeNode.getId(), equalTo(SOME_TEXT));
        assertThat(treeNode.getName(), equalTo(SOME_TEXT));
    }

    @Test
    public void nodeParametersShouldBeReturned() {
        assertThat(treeNode.getId(), equalTo(SOME_TEXT));
        assertThat(treeNode.getName(), equalTo(SOME_TEXT));
        assertThat(treeNode.getParent(), is(nullValue()));
        assertThat(treeNode.getData().equals(data), is(true));
        assertThat(treeNode.isLeaf(), is(true));
    }
    
    @Test
    public void dataShouldBeChanged() {
        assertThat(treeNode.getData(), is(data));
        MachineStateDto mock = mock(MachineStateDto.class);
        treeNode.setData(mock);
        assertThat(treeNode.getData(), is(mock));
    }
    
    @Test
    public void presentationShouldBeUpdated() {
        NodePresentation presentation = mock(NodePresentation.class);
        treeNode.updatePresentation(presentation);
        
        verify(presentation).setPresentableText(SOME_TEXT);
    }
    
    @Test
    public void presentationShouldBeCreated1() {
        assertThat(treeNode.getPresentation(true), any(NodePresentation.class));
    }
    
    @Test
    public void presentationShouldBeCreated2() {
        assertThat(treeNode.getPresentation(false), any(NodePresentation.class));
    }
}