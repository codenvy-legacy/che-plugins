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

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.api.project.node.Node;
import org.eclipse.che.ide.api.project.node.interceptor.NodeInterceptor;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;

import org.eclipse.che.ide.ui.smartTree.NodeUniqueKeyProvider;
import org.eclipse.che.ide.ui.smartTree.Tree;
import org.eclipse.che.ide.ui.smartTree.TreeNodeLoader;
import org.eclipse.che.ide.ui.smartTree.TreeNodeStorage;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

import java.util.Set;

/**
 * Provides implementation of view to display machines on special panel.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class MachinePanelViewImpl extends BaseView<MachinePanelView.ActionDelegate> implements MachinePanelView {
    interface MachinePanelImplUiBinder extends UiBinder<Widget, MachinePanelViewImpl> {
    }

    private static final MachinePanelImplUiBinder UI_BINDER = GWT.create(MachinePanelImplUiBinder.class);

    @UiField(provided = true)
    Tree tree;

    @Inject
    public MachinePanelViewImpl(PartStackUIResources partStackUIResources, final Set<NodeInterceptor> nodeInterceptorSet) {
        super(partStackUIResources);

        TreeNodeStorage nodeStorage = new TreeNodeStorage(new NodeUniqueKeyProvider() {
            @NotNull
            @Override
            public String getKey(@NotNull Node item) {
                String key = "";
                if (item instanceof Machine) {
                    key = ((Machine)item).getId();
                }
                return key;
            }
        });

        tree = new Tree(nodeStorage, new TreeNodeLoader(nodeInterceptorSet));

        setContentWidget(UI_BINDER.createAndBindUi(this));

        tree.getSelectionModel().addSelectionHandler(new SelectionHandler<Node>() {
            @Override
            public void onSelection(SelectionEvent<Node> event) {
                Node selectedNode = event.getSelectedItem();

                if (selectedNode instanceof MachineNode) {
                    delegate.onMachineSelected(((MachineNode) selectedNode).getData());
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void setData(@NotNull MachineNode node) {
        tree.getNodeStorage().add(node);
    }

    @Override
    public void clear() {
        tree.getNodeStorage().clear();
    }

    /** {@inheritDoc} */
    @Override
    public void selectNode(@Nullable MachineNode machineNode) {
        if (machineNode == null) {
            return;
        }

        tree.getSelectionModel().select(machineNode, false);

        delegate.onMachineSelected(machineNode.getData());
    }
}