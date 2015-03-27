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

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.ext.runner.client.state.State;
import org.eclipse.che.ide.ext.runner.client.tabs.container.TabContainer;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Andrienko Alexander
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class TabBuilderTest {
    private static final String     title  = "Title";
    private static final Set<State> scopes = new HashSet<>();

    @Mock
    private TabPresenter                  presenter;
    @Mock
    private TabContainer.TabSelectHandler handler;

    private TabBuilder tabBuilder;

    @Before
    public void setUp() {
        tabBuilder = new TabBuilder();

        scopes.add(State.RUNNERS);
    }

    @Test
    public void shouldDefaultStateInDefaultConstructor() {
        tabBuilder = new TabBuilder();
        tabBuilder.title(title)
                  .presenter(presenter)
                  .scope(scopes)
                  .selectHandler(handler)
                  .tabType(TabType.RIGHT);

        assertThat(tabBuilder.build().isRemovable(), is(true));
    }

    @Test
    public void createTabSuccess() {
        Tab tab = tabBuilder.visible(Tab.VisibleState.VISIBLE)
                            .title(title)
                            .presenter(presenter)
                            .scope(scopes)
                            .selectHandler(handler)
                            .tabType(TabType.LEFT)
                            .build();

        assertThat(tab.isRemovable(), is(false));
        assertThat(tab.getTitle(), is(title));
        assertThat(tab.getTab(), is(presenter));
        assertThat(tab.isAvailableScope(State.RUNNERS), is(true));
        assertThat(tab.isAvailableScope(State.TEMPLATE), is(false));
        assertThat(tab.getTabType(), is(TabType.LEFT));
    }

    @Test
    public void shouldTitleIsNull() {
        String errorMessage = null;
        try {
            tabBuilder.visible(Tab.VisibleState.VISIBLE)
                      .presenter(presenter)
                      .scope(scopes)
                      .selectHandler(handler)
                      .tabType(TabType.LEFT)
                      .build();
        } catch (IllegalStateException e) {
            errorMessage = e.getMessage();
        }
        assertThat(errorMessage, is("You forgot to initialize 'Title' value. Please, initialize it and try again."));
    }

    @Test
    public void shouldScopesAreNull() {
        String errorMessage = null;
        try {
            tabBuilder.visible(Tab.VisibleState.VISIBLE)
                      .title("")
                      .presenter(presenter)
                      .selectHandler(handler)
                      .tabType(TabType.LEFT)
                      .build();
        } catch (IllegalStateException e) {
            errorMessage = e.getMessage();
        }
        assertThat(errorMessage, is("You forgot to initialize 'Scopes' value. Please, initialize it and try again."));
    }

    @Test
    public void shouldPresenterIsNull() {
        String errorMessage = null;
        try {
            tabBuilder.visible(Tab.VisibleState.VISIBLE)
                      .title("")
                      .scope(scopes)
                      .selectHandler(handler)
                      .tabType(TabType.LEFT)
                      .build();
        } catch (IllegalStateException e) {
            errorMessage = e.getMessage();
        }
        assertThat(errorMessage, is("You forgot to initialize 'Widget presenter' value. Please, initialize it and try again."));
    }

    @Test
    public void shouldTabWidthIsNull() {
        String errorMessage = null;
        try {
            tabBuilder.visible(Tab.VisibleState.VISIBLE)
                      .title("")
                      .presenter(presenter)
                      .scope(scopes)
                      .selectHandler(handler)
                      .build();
        } catch (IllegalStateException e) {
            errorMessage = e.getMessage();
        }
        assertThat(errorMessage, is("You forgot to initialize 'Type' value. Please, initialize it and try again."));
    }

}