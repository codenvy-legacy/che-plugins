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
package org.eclipse.che.ide.ext.runner.client.tabs.history;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.tabs.history.HistoryView.ActionDelegate;
import org.eclipse.che.ide.ext.runner.client.tabs.history.runner.RunnerWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGResource;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class HistoryViewImplTest {
    @Mock
    private RunnerWidget         runnerWidget;
    @Mock
    private ActionDelegate       delegate;
    @Mock
    private PartStackUIResources buttonIcons;
    @Mock
    private SVGResource          svgResource;
    @Mock
    private ClickEvent           clickEvent;
    @Mock
    private OMSVGSVGElement      omsvgaElement;

    @Captor
    private ArgumentCaptor<ClickHandler> clickHandlerArgumentCaptor;

    private HistoryViewImpl historyView;

    @Before
    public void setUp() throws Exception {
        when(buttonIcons.erase()).thenReturn(svgResource);
        when(svgResource.getSvg()).thenReturn(omsvgaElement);

        historyView = new HistoryViewImpl(buttonIcons);

        historyView.setDelegate(delegate);
    }

    @Test
    public void onClickHandlerShouldBeFired() throws Exception {
        verify(historyView.clearAll).addDomHandler(clickHandlerArgumentCaptor.capture(), eq(ClickEvent.getType()));

        clickHandlerArgumentCaptor.getValue().onClick(clickEvent);

        verify(delegate).cleanInactiveRunners();
    }

    @Test
    public void runnerWidgetShouldBeAdded() {
        Element element = mock(Element.class);
        when(historyView.scrollPanel.getElement()).thenReturn(element);

        historyView.addRunner(runnerWidget);

        verify(historyView.runnersPanel).add(runnerWidget);
        verify(element).getScrollHeight();
        verify(element).setScrollTop(element.getScrollHeight());
    }

    @Test
    public void runnersPanelShouldBeClear() {
        historyView.clear();

        verify(historyView.runnersPanel).clear();
    }

    @Test
    public void runnerShouldBeRemoved() throws Exception {
        historyView.removeRunner(runnerWidget);

        verify(historyView.runnersPanel).remove(runnerWidget);
    }

}