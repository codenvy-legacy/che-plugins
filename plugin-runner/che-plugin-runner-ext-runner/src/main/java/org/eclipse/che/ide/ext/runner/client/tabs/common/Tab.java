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
package org.eclipse.che.ide.ext.runner.client.tabs.common;

import org.eclipse.che.ide.ext.runner.client.state.State;
import org.eclipse.che.ide.ext.runner.client.tabs.container.TabContainer.TabSelectHandler;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

import static org.eclipse.che.ide.ext.runner.client.tabs.common.Tab.VisibleState.REMOVABLE;

/**
 * The class that contains general information about tab of 'Multi runner' panel.
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public class Tab {

    private final String           title;
    private final TabPresenter     tabPresenter;
    private final Set<State>       scopes;
    private final TabSelectHandler handler;
    private final TabType          tabType;
    private final VisibleState     visibleState;

    public Tab(@Nonnull String title,
               @Nonnull TabPresenter tabPresenter,
               @Nonnull Set<State> scopes,
               @Nullable TabSelectHandler handler,
               @Nonnull TabType tabType,
               @Nonnull VisibleState visibleState) {
        this.title = title;
        this.tabPresenter = tabPresenter;
        this.scopes = scopes;
        this.handler = handler;
        this.tabType = tabType;
        this.visibleState = visibleState;
    }

    /** @return title for the current tab */
    @Nonnull
    public String getTitle() {
        return title;
    }

    /** @return widget of the current tab */
    @Nonnull
    public TabPresenter getTab() {
        return tabPresenter;
    }

    /**
     * Validates an ability to show current tab for a given scope.
     *
     * @param scope
     *         current scope
     * @return <code>true</code> if need to show this tab <code>false</code> otherwise
     */
    public boolean isAvailableScope(@Nonnull State scope) {
        return scopes.contains(scope);
    }

    /** Fire tab selection event for listeners if any. */
    public void performHandler() {
        if (handler == null) {
            return;
        }

        handler.onTabSelected();
    }

    /** @return type of tab */
    @Nonnull
    public TabType getTabType() {
        return tabType;
    }

    /** @return <code>true</code> if current tab can be removed from the container <code>false</code> otherwise */
    public boolean isRemovable() {
        return REMOVABLE.equals(visibleState);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Tab)) {
            return false;
        }

        Tab tab = (Tab)other;
        return Objects.equals(title, tab.title);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    public enum VisibleState {
        REMOVABLE, VISIBLE
    }

}