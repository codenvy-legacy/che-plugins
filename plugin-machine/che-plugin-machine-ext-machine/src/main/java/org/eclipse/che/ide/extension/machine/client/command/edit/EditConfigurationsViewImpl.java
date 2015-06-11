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
import com.google.gwt.user.client.Timer;
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
    SimplePanel                 contentPanel;
    @UiField
    FlowPanel                   savePanel;

    private ActionDelegate delegate;
    private Button         cancelButton;
    private Button         applyButton;

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
                // configurationName value may not be updated immediately after keyUp
                // therefore use the timer with delay=0
                new Timer() {
                    @Override
                    public void run() {
                        delegate.onNameChanged();
                    }
                }.schedule(0);
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
                delegate.onRemoveClicked();
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
                        delegate.onRemoveClicked();
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
        final Button okButton = createButton(locale.okButton(), "window-edit-configurations-ok", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onOkClicked();
            }
        });
        applyButton = createButton(locale.applyButton(), "window-edit-configurations-apply", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onApplyClicked();
            }
        });
        cancelButton = createButton(locale.cancelButton(), "window-edit-configurations-cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });

        okButton.addStyleName(resources.wizardCss().buttonPrimary());
        applyButton.addStyleName(resources.wizardCss().buttonSuccess());
        cancelButton.addStyleName(resources.wizardCss().button());

        getFooter().add(okButton);
        getFooter().add(applyButton);
        getFooter().add(cancelButton);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void show() {
        super.show();
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
        tree.renderTree(-1);

        final CommandTreeNode firstNode = getFirstNode(rootNode);
        if (firstNode != null) {
            selectNode(firstNode);
        }
    }

    private void selectNode(CommandTreeNode node) {
        tree.getSelectionModel().selectSingleNode(node);
        if (node.getData() instanceof CommandType) {
            delegate.onCommandTypeSelected((CommandType)node.getData());
            setHint(true);
        } else if (node.getData() instanceof CommandConfiguration) {
            delegate.onConfigurationSelected((CommandConfiguration)node.getData());
            setHint(false);
        }
    }

    private CommandTreeNode getFirstNode(@Nonnull CommandTreeNode rootNode) {
        final Collection<CommandTreeNode> childNodes = rootNode.getChildren();
        return childNodes.isEmpty() ? null : childNodes.iterator().next();
    }

    @Override
    public String getConfigurationName() {
        return configurationName.getText();
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

    @Override
    public void setCancelButtonState(boolean enabled) {
        cancelButton.setEnabled(enabled);
    }

    @Override
    public void setApplyButtonState(boolean enabled) {
        applyButton.setEnabled(enabled);
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

    @Override
    public void selectCommand(String commandId) {
        for (TreeNodeElement<CommandTreeNode> nodeElement : tree.getVisibleTreeNodes().asIterable()) {
            final CommandTreeNode treeNode = nodeElement.getData();
            if (commandId.equals(treeNode.getId())) {
                selectNode(treeNode);
                break;
            }
        }
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
