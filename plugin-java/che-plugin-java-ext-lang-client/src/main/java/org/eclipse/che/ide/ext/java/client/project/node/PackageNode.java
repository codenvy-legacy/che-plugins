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
package org.eclipse.che.ide.ext.java.client.project.node;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.project.node.HasStorablePath;
import org.eclipse.che.ide.api.project.node.Node;
import org.eclipse.che.ide.api.project.node.resource.RenameProcessor;
import org.eclipse.che.ide.ext.java.client.project.settings.JavaNodeSettings;
import org.eclipse.che.ide.project.node.FolderReferenceNode;
import org.eclipse.che.ide.project.node.resource.ItemReferenceProcessor;
import org.eclipse.che.ide.ui.smartTree.presentation.NodePresentation;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Vlad Zhukovskiy
 */
public class PackageNode extends FolderReferenceNode {

    private final JavaNodeManager nodeManager;

    @Inject
    public PackageNode(@Assisted ItemReference itemReference,
                       @Assisted ProjectDescriptor projectDescriptor,
                       @Assisted JavaNodeSettings nodeSettings,
                       @NotNull EventBus eventBus,
                       @NotNull JavaNodeManager nodeManager,
                       @NotNull ItemReferenceProcessor resourceProcessor) {
        super(itemReference, projectDescriptor, nodeSettings, eventBus, nodeManager, resourceProcessor);
        this.nodeManager = nodeManager;
    }

    @NotNull
    @Override
    protected Promise<List<Node>> getChildrenImpl() {
        return nodeManager.getChildren(getStorablePath(),
                                       getProjectDescriptor(),
                                       getSettings());
    }

    @Override
    public void updatePresentation(@NotNull NodePresentation presentation) {
        presentation.setPresentableText(getDisplayFqn());
        presentation.setPresentableIcon(nodeManager.getJavaNodesResources().packageIcon());
    }

    @Override
    public String getName() {
        return getDisplayFqn();
    }

    public String getDisplayFqn() {
        Node parent = getParent();

        if (parent == null) {
            return getQualifiedName();
        }

        if (parent instanceof PackageNode) {
            String parentFQN = ((PackageNode)parent).getQualifiedName();

            return getQualifiedName().startsWith(parentFQN) ? getQualifiedName().substring(parentFQN.length() + 1) : getQualifiedName();
        } else if (parent instanceof SourceFolderNode) {
            return getQualifiedName();
        }

        return "";
    }

    public String getQualifiedName() {
        Node parent = getParent();

        SourceFolderNode srcFolderNode = null;

        while (parent != null) {
            if (parent instanceof SourceFolderNode) {
                srcFolderNode = (SourceFolderNode)parent;
                break;
            }

            parent = parent.getParent();
        }

        if (srcFolderNode == null) {
            return "";
        }

        String rawFQN = getData().getPath();

        if (!rawFQN.contains(srcFolderNode.getName())) {
            return "";
        }

        rawFQN = rawFQN.substring(rawFQN.lastIndexOf(srcFolderNode.getName()) + srcFolderNode.getName().length());

        if (rawFQN.startsWith("/")) {
            rawFQN = rawFQN.substring(1);
        }

        return rawFQN.replace('/', '.');
    }

    @Override
    public RenameProcessor<ItemReference> getRenameProcessor() {
        return null;
    }

    @Override
    public String getStorablePath() {
        if (getParent() == null || !(getParent() instanceof HasStorablePath)) {
            return getData().getPath();
        }

        return ((HasStorablePath)getParent()).getStorablePath() + "/" + getDisplayFqn().replace(".", "/");
    }
}
