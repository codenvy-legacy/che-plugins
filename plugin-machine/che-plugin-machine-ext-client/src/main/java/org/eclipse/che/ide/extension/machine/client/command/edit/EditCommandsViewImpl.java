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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
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

import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.CommandType;
import org.eclipse.che.ide.ui.list.CategoriesList;
import org.eclipse.che.ide.ui.list.Category;
import org.eclipse.che.ide.ui.list.CategoryRenderer;
import org.eclipse.che.ide.ui.window.Window;

import org.eclipse.che.commons.annotation.Nullable;
import org.vectomatic.dom.svg.ui.SVGImage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The implementation of {@link EditCommandsView}.
 *
 * @author Artem Zatsarynnyy
 * @author Oleksii Orel
 */
@Singleton
public class EditCommandsViewImpl extends Window implements EditCommandsView {

    private static final EditConfigurationsViewImplUiBinder UI_BINDER = GWT.create(EditConfigurationsViewImplUiBinder.class);

    private final EditCommandResources                        commandResources;
    private final Label                                       hintLabel;
    private final Map<CommandType, Set<CommandConfiguration>> categories;
    private       Button                                      addImageButton;
    private       Button                                      removeImageButton;
    private       Button                                      duplicateImageButton;

    @UiField
    MachineLocalizationConstant locale;
    @UiField
    SimplePanel                 categoriesPanel;
    @UiField
    FlowPanel                   filterPanel;
    @UiField
    TextBox                     inputField;
    @UiField
    TextBox                     configurationName;
    @UiField
    SimplePanel                 contentPanel;
    @UiField
    FlowPanel                   savePanel;

    private ActionDelegate delegate;
    private Button         cancelButton;
    private Button         applyButton;
    private CategoriesList list;

    private CommandConfiguration selectItem;
    private CommandType          selectType;

    private final CategoryRenderer<CommandConfiguration> projectImporterRenderer =
            new CategoryRenderer<CommandConfiguration>() {
                @Override
                public void renderElement(Element element, CommandConfiguration data) {
                    element.setInnerText(data.getName());
                }

                @Override
                public Element renderCategory(Category<CommandConfiguration> category) {
                    Element textElement = createCategoryElement();
                    textElement.setInnerText(category.getTitle());
                    textElement.setAttribute("style",
                                             "position:absolute;left:0;right:0;padding:inherit;margin:inherit;text-transform:uppercase;");
                    Event.sinkEvents(textElement, Event.ONCLICK);
                    Event.setEventListener(textElement, new EventListener() {
                        @Override
                        public void onBrowserEvent(Event event) {
                            if (Event.ONCLICK == event.getTypeInt()) {
                                for (CommandType type : categories.keySet()) {
                                    if (type.getDisplayName().equals(Element.as(event.getEventTarget()).getInnerText())) {
                                        selectType = type;
                                        selectType(type);
                                    }
                                }
                            }
                        }
                    });
                    return textElement;
                }
            };

    private final Category.CategoryEventDelegate<CommandConfiguration> projectImporterDelegate =
            new Category.CategoryEventDelegate<CommandConfiguration>() {
                @Override
                public void onListItemClicked(Element listItemBase, CommandConfiguration itemData) {
                    selectItem = itemData;
                    selectType = itemData.getType();
                    selectCommand(selectItem);
                }
            };

    @Inject
    protected EditCommandsViewImpl(org.eclipse.che.ide.Resources ideResources,
                                   EditCommandResources commandResources,
                                   MachineLocalizationConstant locale) {
        this.commandResources = commandResources;
        this.locale = locale;
        hintLabel = new Label();
        commandResources.getCss().ensureInjected();
        hintLabel.addStyleName(commandResources.getCss().commandHint());
        setWidget(UI_BINDER.createAndBindUi(this));
        setTitle(locale.editCommandsViewTitle());
        createFooterButtons();
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
        list = new CategoriesList(ideResources);
        list.asWidget().addDomHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                switch (event.getNativeKeyCode()) {
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
        }, KeyDownEvent.getType());
        contentPanel.setWidget(hintLabel);
        setHint(true);
        categoriesPanel.add(list);
        createFilterButtons();
        selectItem = null;
        categories = new HashMap<>();
        getWidget().getElement().getStyle().setPadding(0, Style.Unit.PX);
    }

    private void createFilterButtons() {
        addImageButton =
                createButton(null, new SVGImage(this.commandResources.addCommandButton()), "commandWizard-addButton", new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        delegate.onAddClicked();
                    }
                });
        removeImageButton = createButton(null, new SVGImage(this.commandResources.removeCommandButton()), "commandWizard-createButton",
                                         new ClickHandler() {
                                             @Override
                                             public void onClick(ClickEvent event) {
                                                 delegate.onRemoveClicked();
                                                 savePanel.setVisible(false);
                                                 contentPanel.setWidget(hintLabel);
                                             }
                                         });
        duplicateImageButton =
                createButton(null, new SVGImage(this.commandResources.duplicateCommandButton()), "commandWizard-duplicateButton",
                             new ClickHandler() {
                                 @Override
                                 public void onClick(ClickEvent event) {
                                     delegate.onAddClicked();
                                 }
                             });
        filterPanel.add(duplicateImageButton);
        filterPanel.add(removeImageButton);
        filterPanel.add(addImageButton);
    }


    public void setImporters(Map<CommandType, Set<CommandConfiguration>> categories) {
        list.clear();
        List<Category<?>> categoriesList = new ArrayList<>();
        for (CommandType type : categories.keySet()) {
            Category<CommandConfiguration> category = new Category<>(type.getDisplayName(),
                                                                     projectImporterRenderer,
                                                                     categories.get(type),
                                                                     projectImporterDelegate);
            categoriesList.add(category);
        }
        list.render(categoriesList);
    }

    @Override
    public void setData(Collection<CommandType> commandTypes, Collection<CommandConfiguration> commandConfigurations) {
        categories.clear();
        for (CommandType type : commandTypes) {
            Set<CommandConfiguration> settingsCategory = new HashSet<>();
            for (CommandConfiguration configuration : commandConfigurations) {
                if (type.getId().equals(configuration.getType().getId())) {
                    settingsCategory.add(configuration);
                }
            }
            categories.put(type, settingsCategory);
        }
        this.setImporters(categories);

        inputField.setEnabled(!commandConfigurations.isEmpty());
    }


    public void setHint(boolean show) {
        if (show) {
            savePanel.setVisible(false);
            if (this.getSelectedCommandType() == null) {
                return;
            }
            hintLabel.setText(locale.editCommandsViewHint(this.getSelectedCommandType().getDisplayName().toUpperCase()));
            contentPanel.setWidget(hintLabel);
        } else {
            contentPanel.remove(hintLabel);
            savePanel.setVisible(true);
        }
    }

    private void createFooterButtons() {
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

        okButton.addStyleName(resources.windowCss().primaryButton());

        addButtonToFooter(okButton);
        addButtonToFooter(applyButton);
        addButtonToFooter(cancelButton);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void show() {
        super.show();
        configurationName.setText("");
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

    private void selectType(CommandType type) {
        delegate.onCommandTypeSelected(type);
        setHint(true);
    }

    private void selectCommand(CommandConfiguration config) {
        delegate.onConfigurationSelected(config);
        setHint(false);
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
        addImageButton.setEnabled(enabled);
    }

    @Override
    public void setRemoveButtonState(boolean enabled) {
        removeImageButton.setEnabled(enabled);
        duplicateImageButton.setEnabled(enabled);
    }

    @Override
    public void setExecuteButtonState(boolean enabled) {
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
        return selectType;
    }

    @Override
    public void selectCommand(String typeName, String commandName) {
        if (commandName == null || typeName == null) {
            return;
        }
        for (CommandType type : categories.keySet()) {
            Set<CommandConfiguration> configurations = categories.get(type);
            for (CommandConfiguration configuration : configurations) {
                if (commandName.equals(configuration.getName()) && typeName.equals(type.getDisplayName())) {
                    selectCommand(configuration);
                    list.selectElement(configuration);
                    break;
                }
            }
        }
    }

    @Nullable
    @Override
    public CommandConfiguration getSelectedConfiguration() {
        return selectItem;
    }

    interface EditConfigurationsViewImplUiBinder extends UiBinder<Widget, EditCommandsViewImpl> {
    }
}
