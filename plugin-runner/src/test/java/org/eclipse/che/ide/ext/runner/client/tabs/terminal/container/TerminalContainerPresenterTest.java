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
package org.eclipse.che.ide.ext.runner.client.tabs.terminal.container;

import org.eclipse.che.ide.ext.runner.client.inject.factories.WidgetFactory;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.selection.Selection;
import org.eclipse.che.ide.ext.runner.client.selection.SelectionManager;
import org.eclipse.che.ide.ext.runner.client.tabs.terminal.panel.Terminal;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class TerminalContainerPresenterTest {
    //variables for constructor
    @Mock
    private TerminalContainerView view;
    @Mock
    private WidgetFactory         widgetFactory;
    @Mock
    private SelectionManager      selectionManager;

    @Mock
    private Runner   runner;
    @Mock
    private Terminal terminal;

    @InjectMocks
    private TerminalContainerPresenter presenter;

    @Before
    public void setUp() {
        when(selectionManager.getRunner()).thenReturn(runner);
        when(widgetFactory.createTerminal()).thenReturn(terminal);
    }

    @Test
    public void prepareActionShouldBePerformed() {
        verify(view).setDelegate(presenter);
        verify(selectionManager).addListener(presenter);
    }

    @Test
    public void shouldOnSelectionChangedToEnvironment() {
        reset(view, selectionManager);

        presenter.onSelectionChanged(Selection.ENVIRONMENT);

        verifyNoMoreInteractions(selectionManager, widgetFactory, view);
    }

    @Test
    public void shouldOnSelectionChangedToRunnerWhenRunnerIsNull() {
        reset(view, selectionManager);
        when(selectionManager.getRunner()).thenReturn(null);

        presenter.onSelectionChanged(Selection.RUNNER);

        verify(selectionManager).getRunner();
        verifyNoMoreInteractions(selectionManager, widgetFactory, view);
    }

    @Test
    public void shouldOnSelectionChangedToRunner() {
        presenter.onSelectionChanged(Selection.RUNNER);

        verify(selectionManager).getRunner();

        verify(widgetFactory).createTerminal();
        verify(terminal).update(runner);
        verify(view).addWidget(terminal);
    }

    @Test
    public void shouldOnSelectionChangedToRunnerWhenTerminalIsNotNullAndRunnerIsAlive() {
        when(runner.isAlive()).thenReturn(true);
        //generate terminal
        presenter.onSelectionChanged(Selection.RUNNER);

        presenter.onSelectionChanged(Selection.RUNNER);

        verify(selectionManager, times(2)).getRunner();

        verify(terminal).setVisible(false);
        verify(terminal, times(2)).setUnavailableLabelVisible(false);
        verify(runner).isAlive();
        verify(terminal).setVisible(true);
    }

    @Test
    public void shouldOnSelectionChangedToRunnerWhenTerminalIsNotNullAndRunnerIsNotAlive() {
        when(runner.isAlive()).thenReturn(false);
        //generate terminal
        presenter.onSelectionChanged(Selection.RUNNER);

        presenter.onSelectionChanged(Selection.RUNNER);

        verify(selectionManager, times(2)).getRunner();

        verify(terminal, times(2)).setVisible(false);
        verify(terminal).setUnavailableLabelVisible(false);
        verify(runner).isAlive();

        verify(terminal).setUnavailableLabelVisible(true);
    }

    @Test
    public void viewShouldBeReturned() {
        assertThat(presenter.getView(), CoreMatchers.<IsWidget>is(view));
    }

    @Test
    public void viewShouldBeVisible() {
        presenter.setVisible(true);

        view.setVisible(true);
    }

    @Test
    public void viewShouldNotBeVisible() {
        presenter.setVisible(false);

        view.setVisible(false);
    }

    @Test
    public void shouldGo() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        presenter.go(container);

        container.setWidget(view);
    }

    @Test
    public void terminalShouldBeUpdated() {
        presenter.onSelectionChanged(Selection.RUNNER);
        reset(terminal);

        presenter.update(runner);

        verify(terminal).update(runner);
    }

    @Test
    public void viewShouldBeReset() {
        presenter.onSelectionChanged(Selection.RUNNER);

        presenter.reset();

        verify(view).removeWidget(terminal);
    }
}