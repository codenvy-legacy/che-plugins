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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.info.terminal;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.content.TabPresenter;

/**
 * The class defines methods which contains business logic to control machine's terminal.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TerminalPresenter implements TabPresenter, TerminalView.ActionDelegate {

    private final TerminalView view;

    @Inject
    public TerminalPresenter(TerminalView view) {
        this.view = view;
        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public IsWidget getView() {
        return view;
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }
}
