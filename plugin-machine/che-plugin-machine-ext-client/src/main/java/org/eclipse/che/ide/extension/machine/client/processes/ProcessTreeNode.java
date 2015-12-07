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
package org.eclipse.che.ide.extension.machine.client.processes;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.ui.tree.TreeNodeElement;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * The class describes node in process tree (it can be machine, process or terminal).
 *
 * @author Anna Shumilova
 */
public class ProcessTreeNode {

    public final static String ROOT = "root";

    private final String                      id;
    private final String                      name;
    private final ProcessTreeNode             parent;
    private final Object                      data;
    private final Collection<ProcessTreeNode> children;
    private TreeNodeElement<ProcessTreeNode> treeNodeElement;

    @Inject
    public ProcessTreeNode(@Assisted ProcessTreeNode parent,
                           @Assisted("data") Object data,
                           @Assisted Collection<ProcessTreeNode> children) {
        this.parent = parent;
        this.data = data;
        this.children = children;

        if (data instanceof MachineDto) {
            id = ((MachineDto)data).getId();
            name = ((MachineDto)data).getName();
        } else if (data instanceof CommandConfiguration) {
            id = ((CommandConfiguration)data).getName();
            name = ((CommandConfiguration)data).getName();
        } else {
            id = ROOT;
            name = ROOT;
        }
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public ProcessTreeNode getParent() {
        return parent;
    }

    @NotNull
    public Object getData() {
        return data;
    }

    @Nullable
    public Collection<ProcessTreeNode> getChildren() {
        return children;
    }

    @NotNull
    public TreeNodeElement<ProcessTreeNode> getTreeNodeElement() {
        return treeNodeElement;
    }

    public void setTreeNodeElement(@NotNull TreeNodeElement<ProcessTreeNode> treeNodeElement) {
        this.treeNodeElement = treeNodeElement;
    }
}
