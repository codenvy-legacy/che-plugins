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

import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.ext.runner.client.tabs.history.runner.RunnerWidget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
    private RunnerWidget    runnerWidget;
    @InjectMocks
    private HistoryViewImpl historyView;

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