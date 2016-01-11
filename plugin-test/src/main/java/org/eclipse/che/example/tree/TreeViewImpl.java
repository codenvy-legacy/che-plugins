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
package org.eclipse.che.example.tree;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.api.project.node.Node;
import org.eclipse.che.ide.api.project.node.interceptor.NodeInterceptor;
import org.eclipse.che.ide.ui.smartTree.NodeLoader;
import org.eclipse.che.ide.ui.smartTree.NodeStorage;
import org.eclipse.che.ide.ui.smartTree.NodeUniqueKeyProvider;
import org.eclipse.che.ide.ui.smartTree.Tree;

import javax.validation.constraints.NotNull;
import java.util.Collections;

/**
 * @author Vlad Zhukovskiy
 */
@Singleton
public class TreeViewImpl extends BaseView<TreeView.ActionDelegate> implements TreeView {

    private Tree tree;

    @Inject
    public TreeViewImpl(PartStackUIResources resources) {
        super(resources);

        NodeUniqueKeyProvider idProvider = new NodeUniqueKeyProvider() {
            @Override
            public String getKey(@NotNull Node item) {
                return String.valueOf(item.hashCode());
            }
        };

        NodeStorage storage = new NodeStorage(idProvider);
        NodeLoader loader = new NodeLoader(Collections.<NodeInterceptor>emptySet());

        tree = new Tree(storage, loader);

        setContentWidget(tree);
    }
}
