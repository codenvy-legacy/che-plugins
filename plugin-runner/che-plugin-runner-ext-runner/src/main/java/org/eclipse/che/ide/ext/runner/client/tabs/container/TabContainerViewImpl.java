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
package org.eclipse.che.ide.ext.runner.client.tabs.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.inject.factories.WidgetFactory;
import org.eclipse.che.ide.ext.runner.client.tabs.common.Tab;
import org.eclipse.che.ide.ext.runner.client.tabs.common.TabPresenter;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.Background;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabType;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabWidget;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabType.LEFT;

/**
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public class TabContainerViewImpl extends Composite implements TabContainerView {

    interface TabContainerViewImplUiBinder extends UiBinder<Widget, TabContainerViewImpl> {
    }

    private static final TabContainerViewImplUiBinder UI_BINDER = GWT.create(TabContainerViewImplUiBinder.class);

    @UiField
    FlowPanel tabs;
    @UiField
    FlowPanel mainPanel;
    @UiField(provided = true)
    final RunnerResources resources;

    private final WidgetFactory          widgetFactory;
    private final Set<TabPresenter>      visiblePresenters;
    private final Set<TabPresenter>      removablePresenters;
    private final Map<String, TabWidget> titles;

    private ActionDelegate actionDelegate;

    @Inject
    public TabContainerViewImpl(RunnerResources resources, WidgetFactory widgetFactory) {
        this.resources = resources;
        this.widgetFactory = widgetFactory;

        visiblePresenters = new HashSet<>();
        removablePresenters = new HashSet<>();
        titles = new HashMap<>();

        initWidget(UI_BINDER.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void showTab(@NotNull Tab tab) {
        for (TabPresenter tabPresenter : visiblePresenters) {
            tabPresenter.setVisible(false);
        }

        for (TabPresenter tabPresenter : removablePresenters) {
            mainPanel.remove(tabPresenter.getView());
        }

        TabPresenter presenter = tab.getTab();
        if (tab.isRemovable()) {
            removablePresenters.add(presenter);
            mainPanel.add(presenter.getView());
        } else if (!visiblePresenters.contains(presenter)) {
            visiblePresenters.add(presenter);
            mainPanel.add(presenter.getView());
        } else {
            presenter.setVisible(true);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void selectTab(@NotNull Tab tab) {
        for (TabWidget widget : titles.values()) {
            widget.unSelect();
        }

        boolean isLeftPanel = LEFT.equals(tab.getTabType());

        titles.get(tab.getTitle()).select(isLeftPanel ? Background.BLACK : Background.GREY);
    }

    /** {@inheritDoc} */
    @Override
    public void setVisibleTitle(@NotNull Map<String, Boolean> tabVisibilities) {
        tabs.clear();

        for (Map.Entry<String, Boolean> entry : tabVisibilities.entrySet()) {
            String title = entry.getKey();
            TabWidget tabWidget = titles.get(title);

            if (tabWidget != null && entry.getValue()) {
                tabs.add(tabWidget);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addTab(@NotNull Tab tab) {
        final String title = tab.getTitle();
        TabType tabType = tab.getTabType();

        if (LEFT.equals(tabType)) {
            tabs.addStyleName(resources.runnerCss().leftTabContainerShadow());
        }

        TabWidget tabWidget = widgetFactory.createTab(title, tabType);
        tabWidget.setDelegate(new TabWidget.ActionDelegate() {
            @Override
            public void onMouseClicked() {
                actionDelegate.onTabClicked(title);
            }
        });

        tabs.add(tabWidget);
        titles.put(title, tabWidget);
    }

    /** {@inheritDoc} */
    @Override
    public void showTabTitle(@NotNull String tabName, boolean isShown) {
        titles.get(tabName).setVisible(isShown);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate actionDelegate) {
        this.actionDelegate = actionDelegate;
    }

}