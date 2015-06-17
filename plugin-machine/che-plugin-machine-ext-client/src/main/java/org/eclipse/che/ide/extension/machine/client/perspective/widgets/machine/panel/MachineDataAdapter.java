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

import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.ui.tree.NodeDataAdapter;
import org.eclipse.che.ide.ui.tree.TreeNodeElement;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Dmitry Shnurenko
 */
public class MachineDataAdapter implements NodeDataAdapter<MachineTreeNode> {

    /** {@inheritDoc} */
    @Override
    public int compare(MachineTreeNode current, MachineTreeNode other) {
        return current.getId().compareTo(other.getId());
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildren(MachineTreeNode data) {
        Collection<MachineTreeNode> children = data.getChildren();

        return children != null && !children.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public Array<MachineTreeNode> getChildren(MachineTreeNode data) {
        Array<MachineTreeNode> children = Collections.createArray();

        Collection<MachineTreeNode> nodes = data.getChildren();

        if (nodes == null) {
            return children;
        }

        for (MachineTreeNode node : nodes) {
            children.add(node);
        }

        return children;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public String getNodeId(MachineTreeNode data) {
        return data.getId();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public String getNodeName(MachineTreeNode data) {
        return data.getName();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public MachineTreeNode getParent(MachineTreeNode data) {
        return data.getParent();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public TreeNodeElement<MachineTreeNode> getRenderedTreeNode(MachineTreeNode data) {
        return data.getTreeNodeElement();
    }

    /** {@inheritDoc} */
    @Override
    public void setNodeName(MachineTreeNode data, String name) {
        throw new UnsupportedOperationException("The method isn't supported in this mode...");
    }

    /** {@inheritDoc} */
    @Override
    public void setRenderedTreeNode(MachineTreeNode data, TreeNodeElement<MachineTreeNode> renderedNode) {
        data.setTreeNodeElement(renderedNode);
    }

    /** {@inheritDoc} */
    @Override
    public MachineTreeNode getDragDropTarget(MachineTreeNode data) {
        throw new UnsupportedOperationException("The method isn't supported in this mode...");
    }

    /** {@inheritDoc} */
    @Override
    public Array<String> getNodePath(MachineTreeNode data) {
        throw new UnsupportedOperationException("The method isn't supported in this mode...");
    }

    /** {@inheritDoc} */
    @Override
    public MachineTreeNode getNodeByPath(MachineTreeNode root, Array<String> relativeNodePath) {
        throw new UnsupportedOperationException("The method isn't supported in this mode...");
    }
}
