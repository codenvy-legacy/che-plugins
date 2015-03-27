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
package org.eclipse.che.ide.ext.runner.client.tabs.templates.filterwidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;

import javax.annotation.Nonnull;

/**
 * The class provides methods which allows change view representation of filter panel.
 *
 * @author Dmitry Shnurenko
 */
public class FilterWidgetImpl extends Composite implements FilterWidget {
    interface FilterWidgetImplUiBinder extends UiBinder<Widget, FilterWidgetImpl> {
    }

    private static final FilterWidgetImplUiBinder UI_BINDER = GWT.create(FilterWidgetImplUiBinder.class);

    @UiField
    ListBox types;
    @UiField
    ListBox scopes;

    @UiField(provided = true)
    final RunnerResources            resources;
    @UiField(provided = true)
    final RunnerLocalizationConstant locale;

    private ActionDelegate delegate;

    @Inject
    public FilterWidgetImpl(RunnerResources resources, RunnerLocalizationConstant locale) {
        this.resources = resources;
        this.locale = locale;

        initWidget(UI_BINDER.createAndBindUi(this));

        for (Scope scope : Scope.values()) {
            scopes.addItem(scope.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void selectType(@Nonnull String type) {
        for (int i = 0; i < types.getItemCount(); i++) {
            if (types.getValue(i).equals(type)) {
                types.setItemSelected(i, true);
                return;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void selectScope(@Nonnull Scope scope) {
        scopes.setItemSelected(scope.ordinal(), true);
    }

    /** {@inheritDoc} */
    @Override
    public void addType(@Nonnull String type) {
        types.clear();

        types.addItem('/' + type);
        types.addItem(locale.configsTypeAll());

        selectType(type);
    }

    /** {@inheritDoc} */
    @Override
    public Scope getScope() {
        String value = scopes.getValue(scopes.getSelectedIndex());
        return Scope.detect(value);
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        int index = types.getSelectedIndex();
        return index == -1 ? "" : types.getValue(index);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler({"types", "scopes"})
    public void onValueChanged(@SuppressWarnings("UnusedParameters") ChangeEvent event) {
        delegate.onValueChanged();
    }
}