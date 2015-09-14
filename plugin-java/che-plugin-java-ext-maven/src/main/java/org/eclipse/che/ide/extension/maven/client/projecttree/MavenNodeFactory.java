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
package org.eclipse.che.ide.extension.maven.client.projecttree;

import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.ext.java.client.projecttree.JavaNodeFactory;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * Factory that helps to create nodes for {@link MavenProjectTreeStructure}.
 *
 * @author Artem Zatsarynnyy
 * @see JavaNodeFactory
 */
public interface MavenNodeFactory extends JavaNodeFactory {
    /**
     * Creates a new {@link MavenProjectNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ProjectDescriptor}
     * @param treeStructure
     *         the {@link MavenProjectTreeStructure} to create the node for
     * @return a new {@link MavenProjectNode}
     */
    MavenProjectNode newMavenProjectNode(@Nullable TreeNode<?> parent,
                                         @NotNull ProjectDescriptor data,
                                         @NotNull MavenProjectTreeStructure treeStructure);

    /**
     * Creates a new {@link MavenFolderNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ItemReference}
     * @param treeStructure
     *         the {@link MavenProjectTreeStructure} to create the node for
     * @return a new {@link MavenFolderNode}
     */
    MavenFolderNode newMavenFolderNode(@NotNull TreeNode<?> parent,
                                       @NotNull ItemReference data,
                                       @NotNull MavenProjectTreeStructure treeStructure);

    /**
     * Creates a new {@link ModuleNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ProjectDescriptor}
     * @param treeStructure
     *         the {@link MavenProjectTreeStructure} to create the node for
     * @return a new {@link ModuleNode}
     */
    ModuleNode newModuleNode(@NotNull TreeNode<?> parent,
                             @NotNull ProjectDescriptor data,
                             @NotNull MavenProjectTreeStructure treeStructure);
}
