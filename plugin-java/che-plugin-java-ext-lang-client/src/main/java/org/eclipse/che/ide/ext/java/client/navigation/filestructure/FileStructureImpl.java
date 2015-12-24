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
package org.eclipse.che.ide.ext.java.client.navigation.filestructure;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.project.node.Node;
import org.eclipse.che.ide.api.project.node.interceptor.NodeInterceptor;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.navigation.factory.NodeFactory;
import org.eclipse.che.ide.ext.java.shared.dto.model.CompilationUnit;
import org.eclipse.che.ide.ui.smartTree.KeyboardNavigationHandler;
import org.eclipse.che.ide.ui.smartTree.NodeUniqueKeyProvider;
import org.eclipse.che.ide.ui.smartTree.Tree;
import org.eclipse.che.ide.ui.smartTree.TreeNodeLoader;
import org.eclipse.che.ide.ui.smartTree.TreeNodeStorage;
import org.eclipse.che.ide.ui.window.Window;

import javax.validation.constraints.NotNull;
import java.util.Collections;

import static org.eclipse.che.ide.ui.smartTree.TreeSelectionModel.Mode.SINGLE;

/**
 * Implementation of {@link FileStructure} view.
 *
 * @author Valeriy Svydenko
 */
@Singleton
final class FileStructureImpl extends Window implements FileStructure {
    interface FileStructureImplUiBinder extends UiBinder<Widget, FileStructureImpl> {
    }

    private static FileStructureImplUiBinder UI_BINDER = GWT.create(FileStructureImplUiBinder.class);

    @UiField
    DockLayoutPanel treeContainer;
    @UiField
    Label           showInheritedLabel;

    private final NodeFactory              nodeFactory;
    private final JavaLocalizationConstant locale;
    private final Tree                     tree;

    private ActionDelegate delegate;

    @Inject
    public FileStructureImpl(NodeFactory nodeFactory, JavaLocalizationConstant locale) {
        super(false);
        this.nodeFactory = nodeFactory;
        this.locale = locale;
        setWidget(UI_BINDER.createAndBindUi(this));

        TreeNodeStorage storage = new TreeNodeStorage(new NodeUniqueKeyProvider() {
            @Override
            public String getKey(@NotNull Node item) {
                return String.valueOf(item.hashCode());
            }
        });
        TreeNodeLoader loader = new TreeNodeLoader(Collections.<NodeInterceptor>emptySet());
        tree = new Tree(storage, loader);
        tree.setAutoExpand(true);
        tree.getSelectionModel().setSelectionMode(SINGLE);

        KeyboardNavigationHandler handler = new KeyboardNavigationHandler() {
            @Override
            public void onEnter(NativeEvent evt) {
                hide();
            }
        };

        handler.bind(tree);

        treeContainer.add(tree);
    }


    /** {@inheritDoc} */
    @Override
    public void setStructure(CompilationUnit compilationUnit, boolean showInheritedMembers) {
        showInheritedLabel.setText(showInheritedMembers ? locale.hideInheritedMembersLabel() : locale.showInheritedMembersLabel());
        tree.getNodeStorage().clear();
        tree.getNodeStorage().add(nodeFactory.create(compilationUnit.getTypes().get(0), compilationUnit, showInheritedMembers, false));
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        hide();
    }

    /** {@inheritDoc} */
    @Override
    public void show() {
        super.show(tree);
        if (!tree.getRootNodes().isEmpty()) {
            tree.getSelectionModel().select(tree.getRootNodes().get(0), false);
        }
        tree.expandAll();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

}