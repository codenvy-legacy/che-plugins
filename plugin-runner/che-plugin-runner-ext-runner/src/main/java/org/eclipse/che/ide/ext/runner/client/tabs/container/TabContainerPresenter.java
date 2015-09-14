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

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.state.PanelState;
import org.eclipse.che.ide.ext.runner.client.state.State;
import org.eclipse.che.ide.ext.runner.client.tabs.common.Tab;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.eclipse.che.ide.ext.runner.client.manager.menu.SplitterState.SPLITTER_OFF;
import static org.eclipse.che.ide.ext.runner.client.tabs.container.PanelLocation.RIGHT_PROPERTIES;

/**
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public class TabContainerPresenter implements TabContainer, TabContainerView.ActionDelegate, PanelState.StateChangeListener {

    private final TabContainerView           view;
    private final PanelState                 panelState;
    private final Map<String, Tab>           tabs;
    private final Map<String, Boolean>       tabVisibilities;
    private final RunnerLocalizationConstant locale;

    private PanelLocation panelLocation;
    private boolean       isFirst;

    @Inject
    public TabContainerPresenter(TabContainerView view, PanelState panelState, RunnerLocalizationConstant locale) {
        this.view = view;
        this.view.setDelegate(this);
        this.panelState = panelState;
        this.locale = locale;

        tabs = new LinkedHashMap<>();
        tabVisibilities = new LinkedHashMap<>();

        panelState.addListener(this);

        isFirst = true;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void showTab(@NotNull String title) {
        onTabClicked(title);
    }

    /** {@inheritDoc} */
    @Override
    public void addTab(@NotNull Tab tab) {
        String title = tab.getTitle();

        if (tabs.containsKey(title)) {
            throw new IllegalStateException("Tab with title " + title + " has already added. Please, check your title name.");
        }

        view.addTab(tab);

        if (isFirst || locale.runnerTabTerminal().equals(title)) {
            view.showTab(tab);
            view.selectTab(tab);

            isFirst = false;
        }

        tabs.put(title, tab);
        tabVisibilities.put(title, true);
    }

    /** {@inheritDoc} */
    @Override
    public void showTabTitle(@NotNull String tabName, boolean isShown) {
        view.showTabTitle(tabName, isShown);
    }

    /** {@inheritDoc} */
    @Override
    public void setLocation(@NotNull PanelLocation panelLocation) {
        this.panelLocation = panelLocation;
    }

    /** {@inheritDoc} */
    @Override
    public void onTabClicked(@NotNull String title) {
        Tab tab = tabs.get(title);

        if (tab != null && title.equals(tab.getTitle())) {
            tab.performHandler();

            view.showTab(tab);
            view.selectTab(tab);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStateChanged() {
        State state = panelState.getState();
        Tab firsVisibleTab = null;

        boolean isRightPropertiesPanel = RIGHT_PROPERTIES.equals(panelLocation);
        boolean isSplitterOff = SPLITTER_OFF.equals(panelState.getSplitterState());

        for (Tab tab : tabs.values()) {
            boolean visible = tab.isAvailableScope(state);
            if (visible && firsVisibleTab == null && (!isRightPropertiesPanel || isSplitterOff)) {
                firsVisibleTab = tab;
            }
            tabVisibilities.put(tab.getTitle(), visible);
        }

        view.setVisibleTitle(tabVisibilities);

        if (firsVisibleTab == null) {
            return;
        }

        view.showTab(firsVisibleTab);
        view.selectTab(firsVisibleTab);
    }

}