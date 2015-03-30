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

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class TabTest {
    private static final String     title  = "TITLE";
    private static final Set<State> scopes = new HashSet<>();
    @Mock
    private TabPresenter                  tabPresenter;
    @Mock
    private TabContainer.TabSelectHandler handler;

    private Tab tab;

    @Before
    public void setUp() {
        scopes.add(State.RUNNERS);
        tab = new Tab(title, tabPresenter, scopes, handler, TabType.LEFT, Tab.VisibleState.VISIBLE);
    }

    @Test
    public void shouldGetCorrectParameter() {
        assertThat(tab.getTitle(), is(title));
        assertThat(tab.getTab(), is(tabPresenter));
        assertThat(tab.isAvailableScope(State.RUNNERS), is(true));
        assertThat(tab.isAvailableScope(State.TEMPLATE), is(false));
        assertThat(tab.getTabType(), is(TabType.LEFT));
        assertThat(tab.isRemovable(), is(false));
    }

    @Test
    public void shouldPerformHandler() {
        tab.performHandler();

        verify(handler).onTabSelected();
    }

    @Test
    public void shouldPerformHandlerWhenHandlerIsNull() {
        tab = new Tab(title, tabPresenter, scopes, null, TabType.LEFT, Tab.VisibleState.VISIBLE);

        tab.performHandler();

        verify(handler, never()).onTabSelected();
    }

    @Test
    public void shouldEqualsWhenObjectIsSame() {
        assertThat(tab.equals(tab), is(true));
    }

    @Test
    public void shouldNotEqualsWhenObjectHasTypeNotTab() {
        assertThat(tab.equals(new Object()), is(false));
    }

    @Test
    public void shouldNotEqualsWhenObjectIsNewTabWithAnotherTitle() {
        Tab tab2 = new Tab("new Title", tabPresenter, scopes, handler, TabType.LEFT, Tab.VisibleState.VISIBLE);
        assertThat(tab.equals(tab2), is(false));
    }

    @Test
    public void shouldEqualsWhenObjectIsNewTabButTitleIsSame() {
        Tab tab2 = new Tab(title, tabPresenter, scopes, handler, TabType.LEFT, Tab.VisibleState.REMOVABLE);
        assertThat(tab.equals(tab2), is(true));
    }

    @Test
    public void shouldCheckHashCodeForEquivalentObjects() {
        Tab tab1 = new Tab(title, tabPresenter, scopes, handler, TabType.LEFT, Tab.VisibleState.REMOVABLE);
        Tab tab2 = new Tab(title, tabPresenter, scopes, handler, TabType.LEFT, Tab.VisibleState.REMOVABLE);
        assertThat(tab1.hashCode(), is(tab2.hashCode()));
    }

    @Test
    public void shouldCheckHashCodeForNotEquivalentObjects() {
        Tab tab1 = new Tab(title, tabPresenter, scopes, handler, TabType.LEFT, Tab.VisibleState.REMOVABLE);
        Tab tab2 = new Tab("title", tabPresenter, scopes, handler, TabType.LEFT, Tab.VisibleState.REMOVABLE);
        assertThat(tab1.hashCode(), is(not(tab2.hashCode())));
    }

}