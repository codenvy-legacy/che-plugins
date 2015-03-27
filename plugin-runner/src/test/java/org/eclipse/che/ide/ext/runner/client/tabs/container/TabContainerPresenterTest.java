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
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class TabContainerPresenterTest {
    private static final String TITLE2 = "title2";

    //variables for constructor
    @Mock
    private TabContainerView view;
    @Mock
    private PanelState       panelState;

    @Mock
    private Tab tab1;
    @Mock
    private Tab tab2;

    @InjectMocks
    private TabContainerPresenter tabContainerPresenter;

    @Before
    public void setUp() {
        String TITLE1 = "title";

        when(tab1.getTitle()).thenReturn(TITLE1);
        when(tab2.getTitle()).thenReturn(TITLE2);

        when(panelState.getState()).thenReturn(State.RUNNERS);
    }

    @Test
    public void shouldGo() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        tabContainerPresenter.go(container);

        verify(container).setWidget(view);
    }

    @Test
    public void shouldAddFirstTab() {
        tabContainerPresenter.addTab(tab1);

        verify(tab1).getTitle();
        verify(view).addTab(tab1);
        verify(view).showTab(tab1);
        verify(view).selectTab(tab1);
    }

    @Test
    public void shouldAddTwoTabs() {
        shouldAddFirstTab();

        tabContainerPresenter.addTab(tab2);

        verify(tab2).getTitle();
        verify(view).addTab(tab2);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldAddTabWhichIsAlreadyExist() {
        tabContainerPresenter.addTab(tab1);

        //add tab1 again
        tabContainerPresenter.addTab(tab1);

        verify(tab1).getTitle();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void shouldOnTabClicked() {
        addTwoTabs();

        tabContainerPresenter.onTabClicked(TITLE2);

        verify(tab2, times(2)).getTitle();
        verify(tab2).performHandler();

        verify(view).showTab(tab2);
        verify(view).selectTab(tab2);
    }

    @Test
    public void shouldOnTabClickedWhenTitleIsWrong() {
        addTwoTabs();

        tabContainerPresenter.onTabClicked("some not exist title");

        verify(tab1).getTitle();
        verify(tab2).getTitle();

        verifyNoMoreInteractions(view, tab1, tab2);
    }

    @Test
    public void shouldShowTab() {
        addTwoTabs();

        tabContainerPresenter.showTab(TITLE2);

        verify(tab2, times(2)).getTitle();
        verify(tab2).performHandler();

        verify(view).showTab(tab2);
        verify(view).selectTab(tab2);
    }

    @Test
    public void shouldShowTabWhenTitleIsWrong() {
        addTwoTabs();

        tabContainerPresenter.showTab("some not exist title");

        verify(tab1).getTitle();
        verify(tab2).getTitle();

        verifyNoMoreInteractions(view, tab1, tab2);
    }

    @Test
    public void shouldOnStateChangedWhenOneTabIsVisible() {
        when(tab1.isAvailableScope(State.RUNNERS)).thenReturn(true);
        when(tab2.isAvailableScope(State.RUNNERS)).thenReturn(false);

        addTwoTabs();

        tabContainerPresenter.onStateChanged();

        verify(panelState).getState();

        verify(tab1).isAvailableScope(State.RUNNERS);
        verify(tab2).isAvailableScope(State.RUNNERS);

        verify(tab1, times(2)).getTitle();
        verify(tab2, times(2)).getTitle();

        verify(view).setVisibleTitle(Matchers.<Map<String, Boolean>>anyObject());

        verify(view).showTab(tab1);
        verify(view).selectTab(tab1);
    }

    @Test
    public void shouldOnStateChangedWhenNoneTabIsVisible() {
        when(tab1.isAvailableScope(State.RUNNERS)).thenReturn(false);
        when(tab2.isAvailableScope(State.RUNNERS)).thenReturn(false);

        addTwoTabs();

        tabContainerPresenter.onStateChanged();

        verify(panelState).getState();

        verify(tab1).isAvailableScope(State.RUNNERS);
        verify(tab2).isAvailableScope(State.RUNNERS);

        verify(tab1, times(2)).getTitle();
        verify(tab2, times(2)).getTitle();

        verify(view).setVisibleTitle(Matchers.<Map<String, Boolean>>anyObject());

        verifyNoMoreInteractions(view);
    }

    @Test
    public void shouldOnStateChangedWhenNoneTabAreNotExist() {
        tabContainerPresenter.onStateChanged();

        verify(panelState).getState();

        verify(view).setVisibleTitle(Matchers.<Map<String, Boolean>>anyObject());

        verify(view, never()).showTab(any(Tab.class));
        verify(view, never()).selectTab(any(Tab.class));
    }

    @Test
    /* Two tabs are visible, but we should select first of them */
    public void shouldOnStateChangedWhenTwoTabAreVisible() {
        when(tab1.isAvailableScope(State.RUNNERS)).thenReturn(true);
        when(tab2.isAvailableScope(State.RUNNERS)).thenReturn(true);

        addTwoTabs();

        tabContainerPresenter.onStateChanged();

        verify(panelState).getState();

        verify(tab1).isAvailableScope(State.RUNNERS);
        verify(tab2).isAvailableScope(State.RUNNERS);

        verify(tab1, times(2)).getTitle();
        verify(tab2, times(2)).getTitle();

        verify(view).setVisibleTitle(Matchers.<Map<String, Boolean>>anyObject());

        verify(view).showTab(tab1);
        verify(view).selectTab(tab1);
    }

    private void addTwoTabs() {
        tabContainerPresenter.addTab(tab1);
        tabContainerPresenter.addTab(tab2);
        reset(view);
    }
}
