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
package org.eclipse.che.ide.extension.machine.client.command.edit;

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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.edit.CommandDataAdapter.CommandTreeNode;
import org.eclipse.che.ide.ui.tree.Tree;
import org.eclipse.che.ide.ui.tree.TreeNodeElement;
import org.eclipse.che.ide.ui.window.Window;
import org.eclipse.che.ide.util.input.SignalEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The implementation of {@link EditConfigurationsView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class EditConfigurationsViewImpl extends Window implements EditConfigurationsView {

    private static final EditConfigurationsViewImplUiBinder UI_BINDER = GWT.create(EditConfigurationsViewImplUiBinder.class);

    private final Label hintLabel;

    @UiField(provided = true)
    MachineLocalizationConstant locale;
    @UiField(provided = true)
    Tree<CommandTreeNode>       tree;
    @UiField
    Button                      addButton;
    @UiField
    Button                      removeButton;
    @UiField
    TextBox                     configurationName;
    @UiField
    Button                      saveButton;
    @UiField
    SimplePanel                 contentPanel;
    @UiField
    FlowPanel                   savePanel;

    private ActionDelegate delegate;

    @Inject
    protected EditConfigurationsViewImpl(org.eclipse.che.ide.Resources resources,
                                         MachineResources machineResources,
                                         MachineLocalizationConstant locale,
                                         CommandDataAdapter dataAdapter,
                                         CommandRenderer renderer) {
        this.locale = locale;
        tree = Tree.create(resources, dataAdapter, renderer);
        hintLabel = new Label(locale.editConfigurationsViewHint());
        hintLabel.addStyleName(machineResources.getCss().commandHint());

        setWidget(UI_BINDER.createAndBindUi(this));

        setTitle(locale.editConfigurationsViewTitle());

        createFooterButtons(resources);

        configurationName.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                delegate.onNameChanged(configurationName.getText());
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

        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                delegate.onSaveClicked();
            }
        });

        tree.setTreeEventHandler(new Tree.Listener<CommandTreeNode>() {
            @Override
            public void onNodeAction(TreeNodeElement<CommandTreeNode> node) {
            }

            @Override
            public void onNodeClosed(TreeNodeElement<CommandTreeNode> node) {
            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<CommandTreeNode> node) {
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<CommandTreeNode> node, MouseEvent event) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<CommandTreeNode> node, MouseEvent event) {
            }

            @Override
            public void onNodeExpanded(TreeNodeElement<CommandTreeNode> node) {
            }

            @Override
            public void onNodeSelected(TreeNodeElement<CommandTreeNode> node, SignalEvent event) {
                final Object selectedNode = node.getData().getData();

                if (selectedNode instanceof CommandType) {
                    delegate.onCommandTypeSelected((CommandType)selectedNode);

                    setHint(true);
                } else if (selectedNode instanceof CommandConfiguration) {
                    delegate.onConfigurationSelected((CommandConfiguration)selectedNode);

                    setHint(false);
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
                switch (event.getKeyCode()) {
                    case KeyboardEvent.KeyCode.INSERT:
                        delegate.onAddClicked();
                        break;
                    case KeyboardEvent.KeyCode.DELETE:
                        delegate.onDeleteClicked();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setHint(boolean show) {
        savePanel.setVisible(!show);

        if (show) {
            contentPanel.setWidget(hintLabel);
        } else {
            contentPanel.remove(hintLabel);
        }
    }

    private void createFooterButtons(@Nonnull org.eclipse.che.ide.Resources resources) {
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
        addButton.setEnabled(true);
        removeButton.setEnabled(false);
        configurationName.setText("");

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
    public void setData(Collection<CommandType> commandTypes, Collection<CommandConfiguration> commandConfigurations) {
        List<CommandTreeNode> rootChildren = new ArrayList<>();
        final CommandTreeNode rootNode = new CommandTreeNode(null, "ROOT", rootChildren);

        for (CommandType type : commandTypes) {
            List<CommandTreeNode> typeChildren = new ArrayList<>();
            final CommandTreeNode typeNode = new CommandTreeNode(rootNode, type, typeChildren);
            rootChildren.add(typeNode);

            for (CommandConfiguration configuration : commandConfigurations) {
                if (type.getId().equals(configuration.getType().getId())) {
                    final CommandTreeNode confNode = new CommandTreeNode(typeNode, configuration, null);
                    typeChildren.add(confNode);
                }
            }
        }

        tree.asWidget().setVisible(true);
        tree.getModel().setRoot(rootNode);
        tree.renderTree(1);

        tree.getSelectionModel().selectSingleNode(getFirstNode(rootNode));

        setHint(true);
    }

    private CommandTreeNode getFirstNode(@Nonnull CommandTreeNode rootNode) {
        Collection<CommandTreeNode> childNodes = rootNode.getChildren();

        return childNodes.iterator().next();
    }

    @Override
    public void setConfigurationName(String name) {
        configurationName.setText(name);
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
        final Array<CommandTreeNode> selectedNodes = tree.getSelectionModel().getSelectedNodes();
        if (!selectedNodes.isEmpty()) {
            final CommandTreeNode node = selectedNodes.get(0);

            Object data = node.getData();

            if (data instanceof CommandType) {
                return (CommandType)data;
            } else if (data instanceof CommandConfiguration) {
                return ((CommandConfiguration)data).getType();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public CommandConfiguration getSelectedConfiguration() {
        final Array<CommandTreeNode> selectedNodes = tree.getSelectionModel().getSelectedNodes();
        if (!selectedNodes.isEmpty()) {
            final CommandTreeNode node = selectedNodes.get(0);
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
