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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance.terminal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.machine.Machine;

import javax.annotation.Nonnull;

/**
 * The class contains methods to displaying terminal.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TerminalViewImpl extends Composite implements TerminalView {
    interface TerminalViewImplUiBinder extends UiBinder<Widget, TerminalViewImpl> {
    }

    private final static TerminalViewImplUiBinder UI_BINDER = GWT.create(TerminalViewImplUiBinder.class);

    @UiField
    Frame terminal;

    @Inject
    public TerminalViewImpl() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void updateTerminal(@Nonnull Machine machine) {
        terminal.setUrl(machine.getTerminalUrl());
    }
}