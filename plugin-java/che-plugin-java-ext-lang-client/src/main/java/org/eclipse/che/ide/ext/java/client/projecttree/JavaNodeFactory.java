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
package org.eclipse.che.ide.ext.java.client.projecttree;

import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.generic.NodeFactory;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.ExternalLibrariesNode;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarClassNode;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarContainerNode;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarFileNode;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarNode;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.JavaFolderNode;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.JavaProjectNode;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.PackageNode;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.SourceFileNode;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.SourceFolderNode;
import org.eclipse.che.ide.ext.java.shared.Jar;
import org.eclipse.che.ide.ext.java.shared.JarEntry;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * Factory that helps to create nodes for {@link JavaTreeStructure}.
 *
 * @author Artem Zatsarynnyy
 * @see NodeFactory
 */
public interface JavaNodeFactory extends NodeFactory {
    /**
     * Creates a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JavaProjectNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ProjectDescriptor}
     * @param treeStructure
     *         the {@link JavaTreeStructure} to create the node for
     * @return a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JavaProjectNode}
     */
    JavaProjectNode newJavaProjectNode(@Nullable TreeNode<?> parent,
                                       @NotNull ProjectDescriptor data,
                                       @NotNull JavaTreeStructure treeStructure);

    /**
     * Creates a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JavaFolderNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ItemReference}
     * @param treeStructure
     *         the {@link JavaTreeStructure} to create the node for
     * @return a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JavaFolderNode}
     */
    JavaFolderNode newJavaFolderNode(@NotNull TreeNode<?> parent,
                                     @NotNull ItemReference data,
                                     @NotNull JavaTreeStructure treeStructure);

    /**
     * Creates a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.SourceFolderNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ItemReference}
     * @param treeStructure
     *         the {@link JavaTreeStructure} to create the node for
     * @return a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.SourceFolderNode}
     */
    SourceFolderNode newSourceFolderNode(@NotNull TreeNode<?> parent,
                                         @NotNull ItemReference data,
                                         @NotNull JavaTreeStructure treeStructure);

    /**
     * Creates a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.PackageNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ItemReference}
     * @param treeStructure
     *         the {@link JavaTreeStructure} to create the node for
     * @return a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.PackageNode}
     */
    PackageNode newPackageNode(@NotNull TreeNode<?> parent,
                               @NotNull ItemReference data,
                               @NotNull JavaTreeStructure treeStructure);

    /**
     * Creates a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.SourceFileNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ItemReference}
     * @param treeStructure
     *         the {@link JavaTreeStructure} to create the node for
     * @return a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.SourceFileNode}
     */
    SourceFileNode newSourceFileNode(@NotNull TreeNode<?> parent,
                                     @NotNull ItemReference data,
                                     @NotNull JavaTreeStructure treeStructure);

    /**
     * Creates a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.ExternalLibrariesNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated data
     * @param treeStructure
     *         the {@link JavaTreeStructure} to create the node for
     * @return a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.ExternalLibrariesNode}
     */
    ExternalLibrariesNode newExternalLibrariesNode(@NotNull JavaProjectNode parent,
                                                   @NotNull Object data,
                                                   @NotNull JavaTreeStructure treeStructure);

    /**
     * Creates a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link Jar}
     * @param treeStructure
     *         the {@link JavaTreeStructure} to create the node for
     * @return a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarNode}
     */
    JarNode newJarNode(@NotNull ExternalLibrariesNode parent,
                       @NotNull Jar data,
                       @NotNull JavaTreeStructure treeStructure);

    /**
     * Creates a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarContainerNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent}, associated {@code data} and {@code libId}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link JarEntry}
     * @param treeStructure
     *         the {@link JavaTreeStructure} to create the node for
     * @param libId
     *         lib ID
     * @return a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarContainerNode}
     */
    JarContainerNode newJarContainerNode(@NotNull TreeNode<?> parent,
                                         @NotNull JarEntry data,
                                         @NotNull JavaTreeStructure treeStructure,
                                         int libId);

    /**
     * Creates a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarFileNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent}, associated {@code data} and {@code libId}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link JarEntry}
     * @param treeStructure
     *         the {@link JavaTreeStructure} to create the node for
     * @param libId
     *         lib ID
     * @return a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarFileNode}
     */
    JarFileNode newJarFileNode(@NotNull TreeNode<?> parent,
                               @NotNull JarEntry data,
                               @NotNull JavaTreeStructure treeStructure,
                               int libId);

    /**
     * Creates a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarClassNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent}, associated {@code data} and {@code libId}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link JarEntry}
     * @param treeStructure
     *         the {@link JavaTreeStructure} to create the node for
     * @param libId
     *         lib ID
     * @return a new {@link org.eclipse.che.ide.ext.java.client.projecttree.nodes.JarClassNode}
     */
    JarClassNode newJarClassNode(@NotNull TreeNode<?> parent,
                                 @NotNull JarEntry data,
                                 @NotNull JavaTreeStructure treeStructure,
                                 int libId);
}
