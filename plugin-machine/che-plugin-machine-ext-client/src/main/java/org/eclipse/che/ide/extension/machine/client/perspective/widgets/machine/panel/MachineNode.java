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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.machine.shared.dto.MachineStateDto;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.project.node.AbstractTreeNode;
import org.eclipse.che.ide.api.project.node.HasDataObject;
import org.eclipse.che.ide.api.project.node.Node;
import org.eclipse.che.ide.ui.smartTree.presentation.HasPresentation;
import org.eclipse.che.ide.ui.smartTree.presentation.NodePresentation;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The class describes node in machine tree and contains special information about node.
 *
 * @author Dmitry Shnurenko
 * @author Alexander Andrienko
 */
public class MachineNode extends AbstractTreeNode implements  HasPresentation, HasDataObject<MachineStateDto> {

    private final String            id;
    private final String            name;
    private MachineStateDto         data;
    private NodePresentation        nodePresentation;

    @Inject
    public MachineNode(@Assisted MachineStateDto data) {
        this.data = data;
        id = data.getId();
        name = data.getName();
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public MachineNode getParent() {
        return null;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    protected Promise<List<Node>> getChildrenImpl() {
        return Promises.<List<Node>>resolve(null);
    }

    @NotNull
    public MachineStateDto getData() {
        return data;
    }

    @Override
    public void setData(@NotNull MachineStateDto data) {
        this.data = data;
    }

    @Override
    public void updatePresentation(@NotNull NodePresentation presentation) {
        presentation.setPresentableText(getName());
        //add icons and styles here...
    }

    @Override
    public NodePresentation getPresentation(boolean update) {
        if (nodePresentation == null) {
            nodePresentation = new NodePresentation();
            updatePresentation(nodePresentation);
        }

        if (update) {
            updatePresentation(nodePresentation);
        }
        return nodePresentation;
    }
}
