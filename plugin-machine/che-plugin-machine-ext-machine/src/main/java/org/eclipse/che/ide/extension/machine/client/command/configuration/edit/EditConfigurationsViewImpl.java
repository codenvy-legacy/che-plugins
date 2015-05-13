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
package org.eclipse.che.ide.extension.machine.client.command.configuration.edit;

import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandType;
import org.eclipse.che.ide.ui.tree.Tree;
import org.eclipse.che.ide.ui.tree.TreeNodeElement;
import org.eclipse.che.ide.ui.window.Window;
import org.eclipse.che.ide.util.input.SignalEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The implementation of {@link EditConfigurationsView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class EditConfigurationsViewImpl extends Window implements EditConfigurationsView {

    private static final EditConfigurationsViewImplUiBinder UI_BINDER = GWT.create(EditConfigurationsViewImplUiBinder.class);

    private final MachineLocalizationConstant locale;

    @UiField(provided = true)
    Tree<CommandDataAdapter.CommandTreeNode> tree;
    @UiField
    Button                                   addButton;
    @UiField
    Button                                   removeButton;
    @UiField
    TextBox                                  commandConfigurationName;
    @UiField
    SimplePanel                              contentPanel;
    @UiField(provided = true)
    org.eclipse.che.ide.Resources            resources;

    private ActionDelegate delegate;

    @Inject
    protected EditConfigurationsViewImpl(org.eclipse.che.ide.Resources resources,
                                         MachineLocalizationConstant locale,
                                         CommandDataAdapter dataAdapter,
                                         CommandRenderer renderer) {
        this.tree = Tree.create(resources, dataAdapter, renderer);
        this.resources = resources;
        this.locale = locale;

        final Widget widget = UI_BINDER.createAndBindUi(this);
        this.setWidget(widget);
        this.setTitle(locale.editConfigurationsViewTitle());

        createButtons();

        commandConfigurationName.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                delegate.onNameChanged(commandConfigurationName.getText());
            }
        });

        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                delegate.onAddClicked();
            }
        });
        addButton.setEnabled(false);

        removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                delegate.onDeleteClicked();
            }
        });
        removeButton.setEnabled(false);

        tree.setTreeEventHandler(new Tree.Listener<CommandDataAdapter.CommandTreeNode>() {
            @Override
            public void onNodeAction(TreeNodeElement<CommandDataAdapter.CommandTreeNode> node) {
            }

            @Override
            public void onNodeClosed(TreeNodeElement<CommandDataAdapter.CommandTreeNode> node) {
            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<CommandDataAdapter.CommandTreeNode> node) {
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<CommandDataAdapter.CommandTreeNode> node, MouseEvent event) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<CommandDataAdapter.CommandTreeNode> node, MouseEvent event) {
            }

            @Override
            public void onNodeExpanded(TreeNodeElement<CommandDataAdapter.CommandTreeNode> node) {
            }

            @Override
            public void onNodeSelected(TreeNodeElement<CommandDataAdapter.CommandTreeNode> node, SignalEvent event) {
                if (node.getData().getData() instanceof CommandType) {
                    delegate.onCommandTypeSelected((CommandType)node.getData().getData());
                } else if (node.getData().getData() instanceof CommandConfiguration) {
                    delegate.onConfigurationSelected((CommandConfiguration)node.getData().getData());
                }
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {
            }

            @Override
            public void onRootDragDrop(MouseEvent event) {
            }

            @Override
            public void onKeyboard(KeyboardEvent event) {
            }
        });
    }

    private void createButtons() {
        final Button closeButton = createButton(locale.closeButton(), "window-edit-configurations-close", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseClicked();
            }
        });
        closeButton.addStyleName(resources.wizardCss().button());
        getFooter().add(closeButton);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void show() {
        super.show();

        resetView();
    }

    private void resetView() {
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
        commandConfigurationName.setText("");
        contentPanel.clear();
    }

    @Override
    public void close() {
        this.hide();
    }

    @Override
    public AcceptsOneWidget getCommandConfigurationsDisplayContainer() {
        return contentPanel;
    }

    @Override
    public void clearCommandConfigurationsDisplayContainer() {
        contentPanel.clear();
    }

    @Override
    public void setCommandConfigurations(Map<CommandType, Set<CommandConfiguration>> commandConfigurations) {
        List<CommandDataAdapter.CommandTreeNode> rootChildren = new ArrayList<>();
        final CommandDataAdapter.CommandTreeNode rootNode = new CommandDataAdapter.CommandTreeNode(null, "ROOT", rootChildren);

        for (CommandType type : commandConfigurations.keySet()) {
            List<CommandDataAdapter.CommandTreeNode> typeChildren = new ArrayList<>();
            final CommandDataAdapter.CommandTreeNode typeNode = new CommandDataAdapter.CommandTreeNode(rootNode, type, typeChildren);
            rootChildren.add(typeNode);

            for (CommandConfiguration configuration : commandConfigurations.get(type)) {
                final CommandDataAdapter.CommandTreeNode confNode = new CommandDataAdapter.CommandTreeNode(typeNode, configuration, null);
                typeChildren.add(confNode);
            }
        }

        tree.asWidget().setVisible(true);
        tree.getModel().setRoot(rootNode);
        tree.renderTree(1);
    }

    @Override
    public void setConfigurationName(String name) {
        commandConfigurationName.setText(name);
    }

    @Override
    public void setAddButtonState(boolean enabled) {
        addButton.setEnabled(enabled);
    }

    @Override
    public void setRemoveButtonState(boolean enabled) {
        removeButton.setEnabled(enabled);
    }

    @Nullable
    @Override
    public CommandType getSelectedCommandType() {
        final Array<CommandDataAdapter.CommandTreeNode> selectedNodes = tree.getSelectionModel().getSelectedNodes();
        if (!selectedNodes.isEmpty()) {
            final CommandDataAdapter.CommandTreeNode node = selectedNodes.get(0);
            if (node.getData() instanceof CommandType) {
                return (CommandType)node.getData();
            } else if (node.getData() instanceof CommandConfiguration) {
                return ((CommandConfiguration)node.getData()).getType();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public CommandConfiguration getSelectedConfiguration() {
        final Array<CommandDataAdapter.CommandTreeNode> selectedNodes = tree.getSelectionModel().getSelectedNodes();
        if (!selectedNodes.isEmpty()) {
            final CommandDataAdapter.CommandTreeNode node = selectedNodes.get(0);
            if (node.getData() instanceof CommandConfiguration) {
                return (CommandConfiguration)node.getData();
            }
        }
        return null;
    }

    @Override
    protected void onClose() {
    }

    interface EditConfigurationsViewImplUiBinder extends UiBinder<Widget, EditConfigurationsViewImpl> {
    }
}
