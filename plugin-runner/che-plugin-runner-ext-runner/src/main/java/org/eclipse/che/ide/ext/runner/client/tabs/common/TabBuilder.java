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

import com.google.inject.Inject;

import org.eclipse.che.ide.ext.runner.client.state.State;
import org.eclipse.che.ide.ext.runner.client.tabs.common.Tab.VisibleState;
import org.eclipse.che.ide.ext.runner.client.tabs.container.TabContainer.TabSelectHandler;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabType;

import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.eclipse.che.ide.ext.runner.client.tabs.common.Tab.VisibleState.REMOVABLE;

/**
 * The builder for simplifying work flow with creating of 'Multi runner' panel's tab. It provides methods which collect all parameters for
 * creating an instance of tab.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class TabBuilder {

    private String           title;
    private TabPresenter     presenter;
    private Set<State>       scopes;
    private TabSelectHandler handler;
    private TabType          tabType;
    private VisibleState     visibleState;

    @Inject
    public TabBuilder() {
        visibleState = REMOVABLE;
    }

    /**
     * Adds title parameter to configuration of tab.
     *
     * @param title
     *         title that needs to be used
     * @return an instance of {@link TabBuilder}
     */
    @NotNull
    public TabBuilder title(@NotNull String title) {
        this.title = title;
        return this;
    }

    /**
     * Adds widget parameter to configuration of tab.
     *
     * @param presenter
     *         presenter of widget that needs to be shown
     * @return an instance of {@link TabBuilder}
     */
    @NotNull
    public TabBuilder presenter(@NotNull TabPresenter presenter) {
        this.presenter = presenter;
        return this;
    }

    /**
     * Adds scope of states when tab has to be shown to configuration of tab.
     *
     * @param scopes
     *         scope these need to be used
     * @return an instance of {@link TabBuilder}
     */
    @NotNull
    public TabBuilder scope(@NotNull Set<State> scopes) {
        this.scopes = scopes;
        return this;
    }

    /**
     * Adds selection handler to configuration of tab.
     *
     * @param handler
     *         handler that needs to be added
     * @return an instance of {@link TabBuilder}
     */
    @NotNull
    public TabBuilder selectHandler(@NotNull TabSelectHandler handler) {
        this.handler = handler;
        return this;
    }

    /**
     * Adds height to configuration of tab.
     *
     * @param tabType
     *         height of tab that needs to be added
     * @return an instance of {@link TabBuilder}
     */
    @NotNull
    public TabBuilder tabType(@NotNull TabType tabType) {
        this.tabType = tabType;
        return this;
    }

    /**
     * Adds visibility state to configuration of tab.
     *
     * @param visibleState
     *         visibility state that needs to be applied for tab
     * @return an instance of {@link TabBuilder}
     */
    @NotNull
    public TabBuilder visible(@NotNull VisibleState visibleState) {
        this.visibleState = visibleState;
        return this;
    }

    /** @return an instance of {@link Tab} with given parameters */
    @NotNull
    public Tab build() {
        if (title == null) {
            throw new IllegalStateException("You forgot to initialize 'Title' value. Please, initialize it and try again.");
        }

        if (scopes == null) {
            throw new IllegalStateException("You forgot to initialize 'Scopes' value. Please, initialize it and try again.");
        }

        if (presenter == null) {
            throw new IllegalStateException("You forgot to initialize 'Widget presenter' value. Please, initialize it and try again.");
        }

        if (tabType == null) {
            throw new IllegalStateException("You forgot to initialize 'Type' value. Please, initialize it and try again.");
        }

        return new Tab(title, presenter, scopes, handler, tabType, visibleState);
    }

}