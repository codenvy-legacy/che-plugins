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

import org.eclipse.che.ide.api.preferences.PreferencePagePresenter;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandType;
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

    private final CategoriesList list;
    Button btnClose;
    Button btnSave;
    @UiField
    SimplePanel                   configurations;
    @UiField
    SimplePanel                   contentPanel;
    @UiField(provided = true)
    org.eclipse.che.ide.Resources resources;
    private final CategoryRenderer<CommandConfiguration> preferencesPageRenderer =
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
    private final MachineLocalizationConstant locale;
    private ActionDelegate           delegate;
    private final Category.CategoryEventDelegate<CommandConfiguration> preferencesPageDelegate =
            new Category.CategoryEventDelegate<CommandConfiguration>() {
                @Override
                public void onListItemClicked(com.google.gwt.dom.client.Element listItemBase, CommandConfiguration itemData) {
                    delegate.onConfigurationSelected(itemData);
                }
            };

    @Inject
    protected EditConfigurationsViewImpl(org.eclipse.che.ide.Resources resources, MachineLocalizationConstant locale) {
        this.resources = resources;
        this.locale = locale;

        Widget widget = UI_BINDER.createAndBindUi(this);

        this.setTitle(locale.editConfigurationsViewTitle());
        this.setWidget(widget);

        //create list of configurations
        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = new CategoriesList(resources);
        configurations.add(list);
        createButtons();
    }

    private void createButtons() {
        btnSave = createButton(locale.okButton(), "window-edit-configurations-storeChanges", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onSaveClicked();
            }
        });
        btnSave.addStyleName(resources.wizardCss().button());
        btnSave.addStyleName(resources.wizardCss().rightButton());
        btnSave.addStyleName(resources.wizardCss().buttonPrimary());
        getFooter().add(btnSave);

        btnClose = createButton(locale.cancelButton(), "window-edit-configurations-cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseClicked();
            }
        });
        btnClose.addStyleName(resources.wizardCss().button());
        getFooter().add(btnClose);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void show() {
        super.show();
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getContentPanel() {
        return contentPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void enableSaveButton(boolean enabled) {
        btnSave.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setCommandTypes(Map<CommandType, Set<CommandConfiguration>> preferences) {
        List<Category<?>> categoriesList = new ArrayList<>();
        for (CommandType s : preferences.keySet()) {
            Category<CommandConfiguration> category = new Category<>(s.getDisplayName(),
                                                                     preferencesPageRenderer,
                                                                     preferences.get(s),
                                                                     preferencesPageDelegate);
            categoriesList.add(category);
        }
        list.render(categoriesList);
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }

    /** {@inheritDoc} */
    @Override
    public void selectPreference(PreferencePagePresenter preference) {
        list.selectElement(preference);
    }

    interface EditConfigurationsViewImplUiBinder extends UiBinder<Widget, EditConfigurationsViewImpl> {
    }
}
