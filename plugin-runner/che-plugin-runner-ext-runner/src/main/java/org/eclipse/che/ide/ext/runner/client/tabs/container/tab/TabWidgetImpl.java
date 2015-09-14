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
package org.eclipse.che.ide.ext.runner.client.tabs.container.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;

import javax.validation.constraints.NotNull;

/**
 * Class provides view representation of tab.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class TabWidgetImpl extends Composite implements TabWidget, ClickHandler {

    interface TabViewImplUiBinder extends UiBinder<Widget, TabWidgetImpl> {
    }

    private static final TabViewImplUiBinder UI_BINDER = GWT.create(TabViewImplUiBinder.class);

    @UiField
    Label     tabTitle;
    @UiField
    FlowPanel tabPanel;

    @UiField(provided = true)
    final RunnerResources resources;

    private ActionDelegate delegate;

    @Inject
    public TabWidgetImpl(RunnerResources resources,
                         @NotNull @Assisted String title,
                         @NotNull @Assisted TabType tabType) {
        this.resources = resources;

        initWidget(UI_BINDER.createAndBindUi(this));

        setHeight(tabType.getHeight());
        setWidth(tabType.getWidth());

        ensureDebugId(title + "-tab");
        tabTitle.setText(title);

        addDomHandler(this, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void select(@NotNull Background background) {
        getElement().getStyle().setBackgroundColor(background.toString());

        tabTitle.addStyleName(resources.runnerCss().activeTabText());
        addStyleName(resources.runnerCss().activeTab());
        removeStyleName(resources.runnerCss().notActiveTabText());
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        getElement().getStyle().clearBackgroundColor();

        tabTitle.removeStyleName(resources.runnerCss().activeTabText());
        removeStyleName(resources.runnerCss().activeTab());
        addStyleName(resources.runnerCss().notActiveTabText());
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@NotNull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(ClickEvent event) {
        delegate.onMouseClicked();
    }

}