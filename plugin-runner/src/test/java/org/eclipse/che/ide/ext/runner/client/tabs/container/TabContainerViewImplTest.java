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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.inject.factories.WidgetFactory;
import org.eclipse.che.ide.ext.runner.client.tabs.common.Tab;
import org.eclipse.che.ide.ext.runner.client.tabs.common.TabPresenter;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.Background;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabType;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabType.RIGHT;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class TabContainerViewImplTest {
    private static final String TITLE1 = "Title1";
    private static final String TITLE2 = "Title2";
    private static final String TITLE3 = "Title3";

    //mocks for constructor
    @Mock(answer = RETURNS_DEEP_STUBS)
    private RunnerResources resources;
    @Mock
    private WidgetFactory   widgetFactory;

    //Tab mock
    @Mock
    private Tab tab1;
    @Mock
    private Tab tab2;
    @Mock
    private Tab tab3;

    //TabPresenter mocks
    @Mock
    private TabPresenter tabPresenter1;
    @Mock
    private TabPresenter tabPresenter2;

    //Widget mocks
    @Mock
    private TabWidget tabWidget1;
    @Mock
    private TabWidget tabWidget2;
    @Mock
    private TabWidget tabWidget3;
    @Mock
    private TabWidget tabWidget4;
    @Mock
    private IsWidget  isWidget1;
    @Mock
    private IsWidget  isWidget2;

    @Mock
    private TabContainerView.ActionDelegate actionDelegate;

    @InjectMocks
    private TabContainerViewImpl tabContainerView;

    @Before
    public void setUp() {
        when(tab1.getTitle()).thenReturn(TITLE1);
        when(tab2.getTitle()).thenReturn(TITLE2);
        when(tab3.getTitle()).thenReturn(TITLE3);

        when(tab1.getTabType()).thenReturn(TabType.LEFT);
        when(tab2.getTabType()).thenReturn(TabType.LEFT);
        when(tab3.getTabType()).thenReturn(RIGHT);

        when(widgetFactory.createTab(anyString(), any(TabType.class))).thenReturn(tabWidget1)
                                                                      .thenReturn(tabWidget2)
                                                                      .thenReturn(tabWidget3);
        when(tab1.getTab()).thenReturn(tabPresenter1);
        when(tab2.getTab()).thenReturn(tabPresenter2);

        when(tabPresenter1.getView()).thenReturn(isWidget1);
        when(tabPresenter2.getView()).thenReturn(isWidget2);

        when(resources.runnerCss().leftTabContainerShadow()).thenReturn(TITLE1);
    }

    @Test
    public void shouldAddTab() {
        ArgumentCaptor<TabWidget.ActionDelegate> tabActionDelegateCaptor = ArgumentCaptor.forClass(TabWidget.ActionDelegate.class);

        tabContainerView.setDelegate(actionDelegate);
        tabContainerView.addTab(tab1);

        verify(tab1).getTitle();
        verify(tab1).getTabType();

        verify(widgetFactory).createTab(TITLE1, TabType.LEFT);

        verify(tabWidget1).setDelegate(tabActionDelegateCaptor.capture());
        TabWidget.ActionDelegate delegate = tabActionDelegateCaptor.getValue();
        delegate.onMouseClicked();

        verify(actionDelegate).onTabClicked(TITLE1);

        verify(tabContainerView.tabs).add(tabWidget1);
        verify(tabContainerView.tabs).addStyleName(TITLE1);
        verify(resources.runnerCss()).leftTabContainerShadow();
    }

    @Test
    public void shadowShouldNotBeAddedWhenTabTypeIsRight() throws Exception {
        when(tab1.getTabType()).thenReturn(RIGHT);

        tabContainerView.addTab(tab1);

        verify(resources.runnerCss(), never()).leftTabContainerShadow();
        verify(tabContainerView.tabs, never()).addStyleName(TITLE1);
    }

    @Test
    public void shouldVisibleOneTitle() {
        Map<String, Boolean> tabVisibilities = new HashMap<>();
        tabVisibilities.put(TITLE1, true);
        tabVisibilities.put(TITLE2, false);

        addThreeTabs();

        tabContainerView.setVisibleTitle(tabVisibilities);

        verify(tabContainerView.tabs).clear();

        verify(tabContainerView.tabs).add(tabWidget1);
    }

    @Test
    public void shouldVisibleTwoTitle() {
        Map<String, Boolean> tabVisibilities = new HashMap<>();
        tabVisibilities.put(TITLE1, true);
        tabVisibilities.put(TITLE2, true);

        addThreeTabs();

        tabContainerView.setVisibleTitle(tabVisibilities);

        verify(tabContainerView.tabs).clear();

        verify(tabContainerView.tabs).add(tabWidget1);
        verify(tabContainerView.tabs).add(tabWidget2);
    }

    @Test
    public void shouldVisibleTitleWithWrongList() {
        Map<String, Boolean> tabVisibilities = new HashMap<>();
        tabVisibilities.put(TITLE1, true);
        tabVisibilities.put("not existed title", true);

        addThreeTabs();

        tabContainerView.setVisibleTitle(tabVisibilities);

        verify(tabContainerView.tabs).clear();

        verify(tabContainerView.tabs).add(tabWidget1);
    }

    @Test
    public void shouldSelectTabWhenTabHasTypeLeftPanel() {
        addThreeTabs();

        tabContainerView.selectTab(tab1);

        verify(tabWidget1).unSelect();
        verify(tabWidget2).unSelect();
        verify(tabWidget3).unSelect();

        verify(tabWidget1).select(Background.BLACK);
    }

    @Test
    public void shouldSelectTabWhenTabHasTypeRightPanel() {
        addThreeTabs();

        tabContainerView.selectTab(tab3);

        verify(tabWidget1).unSelect();
        verify(tabWidget2).unSelect();
        verify(tabWidget3).unSelect();

        verify(tabWidget3).select(Background.GREY);
    }

    @Test
    public void shouldShowRemovableTab() {
        when(tab1.isRemovable()).thenReturn(true);

        tabContainerView.showTab(tab1);

        verify(tab1).getTab();
        verify(tab1).isRemovable();
        verify(tabPresenter1).getView();
        verify(tabContainerView.mainPanel).add(isWidget1);
    }

    @Test
    public void shouldShowNotRemovableTab() {
        when(tab1.isRemovable()).thenReturn(false);

        tabContainerView.addTab(tab1);
        tabContainerView.showTab(tab1);

        verify(tab1).getTab();
        verify(tab1).isRemovable();
        verify(tabPresenter1).getView();
        verify(tabContainerView.mainPanel).add(isWidget1);
    }

    @Test
    public void shouldShowTabAndRemoveRemovableTab() {
        when(tab1.isRemovable()).thenReturn(true);
        when(tab2.isRemovable()).thenReturn(false);

        addThreeTabs();

        tabContainerView.showTab(tab1);
        tabContainerView.showTab(tab2);

        verify(tabContainerView.mainPanel).remove(isWidget1);

        verify(tab1).getTab();
        verify(tab1).isRemovable();
        verify(tabPresenter1, times(2)).getView();
        verify(tabContainerView.mainPanel).add(isWidget1);

        verify(tab2).getTab();
        verify(tab2).isRemovable();
        verify(tabPresenter2).getView();
        verify(tabContainerView.mainPanel).add(isWidget2);
    }

    @Test
    public void shouldShowNotRemovableTabWhichIsAlreadyShown() {
        when(tab1.isRemovable()).thenReturn(false);

        tabContainerView.showTab(tab1);
        tabContainerView.showTab(tab1);

        verify(tabPresenter1).setVisible(false);
        verify(tabPresenter1).setVisible(true);
        verify(tab1, times(2)).getTab();
        verify(tab1, times(2)).isRemovable();
        verify(tabPresenter1).getView();
        verify(tabContainerView.mainPanel).add(isWidget1);
    }

    private void addThreeTabs() {
        tabContainerView.setDelegate(actionDelegate);
        tabContainerView.addTab(tab1);
        tabContainerView.addTab(tab2);
        tabContainerView.addTab(tab3);

        reset(tabContainerView.tabs);
    }
}
