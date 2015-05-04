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

import elemental.html.TableElement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandType;
import org.eclipse.che.ide.ui.list.CategoriesList;
import org.eclipse.che.ide.ui.list.Category;
import org.eclipse.che.ide.ui.list.CategoryRenderer;
import org.eclipse.che.ide.ui.window.Window;
import org.eclipse.che.ide.util.dom.Elements;

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

    private final CategoriesList              categoriesList;
    private final MachineLocalizationConstant locale;
    @UiField
    Button                        addButton;
    @UiField
    Button                        removeButton;
    @UiField
    Button                        executeButton;
    @UiField
    SimplePanel                   configurations;
    @UiField
    SimplePanel                   contentPanel;
    @UiField(provided = true)
    org.eclipse.che.ide.Resources resources;
    private final CategoryRenderer<CommandConfiguration> categoryRenderer =
            new CategoryRenderer<CommandConfiguration>() {
                @Override
                public void renderElement(com.google.gwt.dom.client.Element element, CommandConfiguration preference) {
                    element.setInnerText(preference.getName());
                }

                @Override
                public SpanElement renderCategory(Category<CommandConfiguration> category) {
                    SpanElement spanElement = Document.get().createSpanElement();
                    spanElement.setClassName(resources.defaultCategoriesListCss().headerText());
                    spanElement.setInnerText(category.getTitle());
                    return spanElement;
                }
            };
    private ActionDelegate delegate;
    private final Category.CategoryEventDelegate<CommandConfiguration> eventDelegate =
            new Category.CategoryEventDelegate<CommandConfiguration>() {
                @Override
                public void onListItemClicked(com.google.gwt.dom.client.Element listItemBase, CommandConfiguration itemData) {
                    delegate.onConfigurationSelected(itemData);

                    removeButton.setEnabled(true);
                    executeButton.setEnabled(true);
                }
            };

    @Inject
    protected EditConfigurationsViewImpl(org.eclipse.che.ide.Resources resources, MachineLocalizationConstant locale) {
        this.resources = resources;
        this.locale = locale;

        final Widget widget = UI_BINDER.createAndBindUi(this);
        this.setWidget(widget);
        this.setTitle(locale.editConfigurationsViewTitle());

        //create list of configurations
        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        categoriesList = new CategoriesList(resources);
        configurations.add(categoriesList);
        createButtons();

        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                delegate.onAddClicked();
            }
        });
        removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                delegate.onDeleteClicked();
            }
        });
        executeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                delegate.onExecuteClicked();
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

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void show() {
        super.show();

        contentPanel.clear();
        removeButton.setEnabled(false);
        executeButton.setEnabled(false);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getContentPanel() {
        return contentPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void setCommandConfigurations(Map<CommandType, Set<CommandConfiguration>> commandConfigurations) {
        categoriesList.clear();

        List<Category<?>> categoriesList = new ArrayList<>();
        for (CommandType commandType : commandConfigurations.keySet()) {
            final Set<CommandConfiguration> configurations = commandConfigurations.get(commandType);
            if (!configurations.isEmpty()) {
                final Category<CommandConfiguration> category = new Category<>(commandType.getDisplayName(),
                                                                               categoryRenderer,
                                                                               configurations,
                                                                               eventDelegate);
                categoriesList.add(category);
            }
        }

        this.categoriesList.render(categoriesList);
    }

    /** {@inheritDoc} */
    @Override
    public void selectConfiguration(CommandConfiguration configuration) {
        categoriesList.selectElement(configuration);
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }

    interface EditConfigurationsViewImplUiBinder extends UiBinder<Widget, EditConfigurationsViewImpl> {
    }
}
