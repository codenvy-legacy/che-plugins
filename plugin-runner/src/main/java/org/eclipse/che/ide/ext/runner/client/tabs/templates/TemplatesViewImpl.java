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
package org.eclipse.che.ide.ext.runner.client.tabs.templates;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.inject.factories.WidgetFactory;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.tabs.common.item.RunnerItems;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import org.eclipse.che.ide.ext.runner.client.tabs.templates.environment.EnvironmentWidget;
import org.eclipse.che.ide.ext.runner.client.tabs.templates.filterwidget.FilterWidget;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;

/**
 * The Class provides graphical implementation of runner environments.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TemplatesViewImpl extends Composite implements TemplatesView {

    interface TemplatesViewImplUiBinder extends UiBinder<Widget, TemplatesViewImpl> {
    }

    private static final TemplatesViewImplUiBinder UI_BINDER = GWT.create(TemplatesViewImplUiBinder.class);

    @UiField
    FlowPanel   environmentsPanel;
    @UiField
    SimplePanel filterPanel;

    @UiField(provided = true)
    final RunnerResources            resources;
    @UiField(provided = true)
    final RunnerLocalizationConstant locale;

    private final Map<Environment, EnvironmentWidget> environmentWidgets;
    private final WidgetFactory                       widgetFactory;
    private final List<EnvironmentWidget>             cacheWidgets;

    @Inject
    public TemplatesViewImpl(RunnerResources resources,
                             RunnerLocalizationConstant locale,
                             WidgetFactory widgetFactory) {
        this.resources = resources;
        this.locale = locale;
        this.widgetFactory = widgetFactory;

        initWidget(UI_BINDER.createAndBindUi(this));

        this.environmentWidgets = new HashMap<>();
        this.cacheWidgets = new ArrayList<>();
    }

    /** {@inheritDoc} */
    @Override
    public void addEnvironment(@Nonnull Map<Scope, List<Environment>> environments) {
        clearEnvironmentsPanel();
        int i = 0;

        for (Environment environment : environments.get(PROJECT)) {
            addEnvironment(environment, PROJECT, i++);
        }

        List<Environment> systemEnvironments = environments.get(SYSTEM);

        for (Environment environment : systemEnvironments) {
            addEnvironment(environment, SYSTEM, i++);
        }
    }

    private void addEnvironment(@Nonnull Environment environment, @Nonnull Scope scope, @Nonnegative int index) {
        EnvironmentWidget widget = getItem(index);

        widget.setScope(scope);
        widget.update(environment);

        environmentWidgets.put(environment, widget);
        environmentsPanel.add(widget);
    }

    @Nonnull
    private EnvironmentWidget getItem(@Nonnegative int index) {
        if (cacheWidgets.size() > index) {
            EnvironmentWidget widget = cacheWidgets.get(index);
            widget.unSelect();
            return widget;
        }

        EnvironmentWidget widget = widgetFactory.createEnvironment();

        cacheWidgets.add(widget);

        return widget;
    }

    /** {@inheritDoc} */
    @Override
    public void selectEnvironment(@Nullable Environment selectedEnvironment) {
        for (RunnerItems widget : environmentWidgets.values()) {
            widget.unSelect();
        }

        EnvironmentWidget selectedWidget = environmentWidgets.get(selectedEnvironment);
        if (selectedWidget != null) {
            selectedWidget.select();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setFilterWidget(@Nonnull FilterWidget filterWidget) {
        filterPanel.setWidget(filterWidget);
    }

    /** {@inheritDoc} */
    @Override
    public void clearEnvironmentsPanel() {
        environmentsPanel.clear();
    }
}