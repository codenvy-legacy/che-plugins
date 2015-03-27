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

import org.eclipse.che.ide.ext.runner.client.state.PanelState;
import org.eclipse.che.ide.ext.runner.client.state.State;
import org.eclipse.che.ide.ext.runner.client.tabs.common.Tab;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public class TabContainerPresenter implements TabContainer, TabContainerView.ActionDelegate, PanelState.StateChangeListener {

    private final TabContainerView     view;
    private final PanelState           panelState;
    private final Map<String, Tab>     tabs;
    private final Map<String, Boolean> tabVisibilities;

    private boolean isFirst;

    @Inject
    public TabContainerPresenter(TabContainerView view, PanelState panelState) {
        this.view = view;
        this.view.setDelegate(this);
        this.panelState = panelState;

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
    public void showTab(@Nonnull String title) {
        onTabClicked(title);
    }

    /** {@inheritDoc} */
    @Override
    public void addTab(@Nonnull Tab tab) {
        String title = tab.getTitle();

        if (tabs.containsKey(title)) {
            throw new IllegalStateException("Tab with title " + title + " has already added. Please, check your title name.");
        }

        view.addTab(tab);

        if (isFirst) {
            view.showTab(tab);
            view.selectTab(tab);

            isFirst = false;
        }

        tabs.put(title, tab);
        tabVisibilities.put(title, true);
    }

    /** {@inheritDoc} */
    @Override
    public void onTabClicked(@Nonnull String title) {
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

        for (Tab tab : tabs.values()) {
            boolean visible = tab.isAvailableScope(state);
            if (visible && firsVisibleTab == null) {
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