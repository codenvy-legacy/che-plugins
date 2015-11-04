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

import org.eclipse.che.api.machine.shared.dto.MachineStateDto;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.api.project.node.Node;
import org.eclipse.che.ide.api.project.node.interceptor.NodeInterceptor;

import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.ui.smartTree.NodeUniqueKeyProvider;
import org.eclipse.che.ide.ui.smartTree.Tree;
import org.eclipse.che.ide.ui.smartTree.TreeNodeLoader;
import org.eclipse.che.ide.ui.smartTree.TreeNodeStorage;
import org.eclipse.che.ide.ui.smartTree.event.NodeAddedEvent;
import org.eclipse.che.ide.ui.smartTree.event.StoreRemoveEvent;

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
            @Override
            public String getKey(Node item) {
                return item.getName();
            }
        });

        tree = new Tree(nodeStorage, new TreeNodeLoader(nodeInterceptorSet));

        setContentWidget(UI_BINDER.createAndBindUi(this));

        tree.getSelectionModel().addSelectionHandler(new SelectionHandler<Node>() {
            @Override
            public void onSelection(SelectionEvent<Node> event) {
                Node selectedNode = event.getSelectedItem();
                if (selectedNode instanceof MachineNode) {
                    delegate.onMachineSelected(((MachineNode)selectedNode).getData());
                }
            }
        });

        tree.addNodeAddedHandler(new NodeAddedEvent.NodeAddedEventHandler() {
            @Override
            public void onNodeAdded(NodeAddedEvent event) {
                Node selectedNode = event.getNodes().get(0);
                if (selectedNode instanceof MachineNode) {
                    tree.getSelectionModel().select(selectedNode, false);
                    delegate.onMachineSelected(((MachineNode)selectedNode).getData());
                }
            }
        });

        tree.getNodeStorage().addStoreRemoveHandler(new StoreRemoveEvent.StoreRemoveHandler() {
            @Override
            public void onRemove(StoreRemoveEvent event) {
                MachineNode machineNode = (MachineNode)tree.getNodeStorage().getAll().get(0);
                tree.getSelectionModel().select(machineNode, false);
                delegate.onMachineSelected((machineNode).getData());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void addData(MachineNode node) {
        tree.getNodeStorage().add(node);

        delegate.onMachineSelected((node).getData());
    }

    @Override
    public MachineNode findNode(MachineStateDto machineStateDto) {
        return (MachineNode)tree.getNodeStorage().findNodeWithKey(machineStateDto.getName());//?
    }

    /** {@inheritDoc} */
    @Override
    public void selectNode(MachineNode machineNode) {
        if (machineNode == null) {
            return;
        }

        tree.getSelectionModel().select(machineNode, false);

        delegate.onMachineSelected(machineNode.getData());
    }

    @Override
    public void removeData(MachineNode data) {
        tree.getNodeStorage().remove(data);

        delegate.onMachineSelected(data.getData());
    }

    @Override
    public void clear() {
        tree.getNodeStorage().clear();
    }
}