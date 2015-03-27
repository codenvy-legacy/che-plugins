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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabWidget.ActionDelegate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrey Plotnikov
 */
@RunWith(GwtMockitoTestRunner.class)
public class TabWidgetImplTest {

    private static final String SOME_TEXT = "some text";

    @Mock(answer = RETURNS_DEEP_STUBS)
    private RunnerResources resources;
    @Mock
    private TabType         tabType;
    private TabWidgetImpl   widget;

    @Before
    public void setUp() throws Exception {
        widget = new TabWidgetImpl(resources, SOME_TEXT, tabType);
        when(resources.runnerCss().activeTabText()).thenReturn(SOME_TEXT);
    }

    @Test
    public void constructorShouldBeVerified() throws Exception {
        verify(widget.tabTitle).setText(SOME_TEXT);
        verify(tabType).getHeight();
        verify(tabType).getWidth();
    }

    @Test
    public void widgetShouldBeSelected() throws Exception {
        widget.select(Background.BLACK);

        verify(widget.tabTitle).addStyleName(SOME_TEXT);

        RunnerResources.RunnerCss css = resources.runnerCss();
        verify(css).activeTabText();
        verify(css).activeTab();
        verify(css).notActiveTabText();
    }

    @Test
    public void widgetShouldBeUnSelected() throws Exception {
        widget.unSelect();

        verify(widget.tabTitle).removeStyleName(SOME_TEXT);

        RunnerResources.RunnerCss css = resources.runnerCss();
        verify(css).activeTabText();
        verify(css).activeTab();
        verify(css).notActiveTabText();
    }

    @Test
    public void userClickEventShouldBeDelegated() throws Exception {
        ActionDelegate delegate = mock(ActionDelegate.class);

        widget.setDelegate(delegate);
        widget.onClick(mock(ClickEvent.class));

        verify(delegate).onMouseClicked();
    }

}