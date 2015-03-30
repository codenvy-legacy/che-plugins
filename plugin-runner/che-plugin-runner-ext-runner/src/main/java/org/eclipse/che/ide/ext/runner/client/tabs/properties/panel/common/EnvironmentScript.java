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
package org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.TreeStructure;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * The Class represents custom environment which is file node with special name which displayed on tab of docker.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
public class EnvironmentScript extends FileNode {

    private final String environmentName;

    public EnvironmentScript(TreeNode<?> parent,
                             ItemReference data,
                             TreeStructure treeStructure,
                             EventBus eventBus,
                             ProjectServiceClient projectServiceClient,
                             DtoUnmarshallerFactory dtoUnmarshallerFactory,
                             String environmentName) {

        super(parent, data, treeStructure, eventBus, projectServiceClient, dtoUnmarshallerFactory);

        this.environmentName = environmentName;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getDisplayName() {
        return '[' + environmentName + "] " + getData().getName();
    }

}