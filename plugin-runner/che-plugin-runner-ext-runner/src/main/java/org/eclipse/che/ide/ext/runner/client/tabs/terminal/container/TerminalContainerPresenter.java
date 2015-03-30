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
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.che.ide.ext.runner.client.selection.Selection.ENVIRONMENT;

/**
 * The class that manages a container of the terminals.
 *
 * @author Valeriy Svydenko
 */
@Singleton
public class TerminalContainerPresenter implements TerminalContainer,
                                                   TerminalContainerView.ActionDelegate,
                                                   SelectionManager.SelectionChangeListener {

    private final TerminalContainerView view;
    private final SelectionManager      selectionManager;
    private final Map<Runner, Terminal> terminals;
    private final WidgetFactory         widgetFactory;

    @Inject
    public TerminalContainerPresenter(TerminalContainerView view, WidgetFactory widgetFactory, SelectionManager selectionManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.widgetFactory = widgetFactory;

        this.selectionManager = selectionManager;
        this.selectionManager.addListener(this);

        terminals = new HashMap<>();
    }

    /** {@inheritDoc} */
    public void onSelectionChanged(@Nonnull Selection selection) {
        if (ENVIRONMENT.equals(selection)) {
            return;
        }

        Runner runner = selectionManager.getRunner();
        if (runner == null) {
            return;
        }

        showTerminal(runner);
    }

    private void showTerminal(@Nonnull Runner runner) {
        for (Terminal terminal : terminals.values()) {
            terminal.setVisible(false);
            terminal.setUnavailableLabelVisible(false);
        }

        Terminal terminal = terminals.get(runner);
        if (terminal == null) {
            terminal = widgetFactory.createTerminal();
            terminal.update(runner);

            terminals.put(runner, terminal);

            view.addWidget(terminal);
        } else {
            boolean isAnyAppRun = runner.isAlive();

            terminal.setVisible(isAnyAppRun);
            terminal.setUnavailableLabelVisible(!isAnyAppRun);
        }
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public IsWidget getView() {
        return view;
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        Terminal terminal = terminals.get(runner);
        if (terminal != null) {
            terminal.update(runner);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void reset() {
        for (Terminal terminal : terminals.values()) {
            view.removeWidget(terminal);
        }

        terminals.clear();
    }
}