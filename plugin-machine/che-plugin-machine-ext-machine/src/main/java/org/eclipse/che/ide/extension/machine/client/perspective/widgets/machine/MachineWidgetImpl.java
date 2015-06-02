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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.extension.machine.client.machine.Machine;

import javax.annotation.Nonnull;

/**
 * Provides implementation of methods to control displaying of machine.
 *
 * @author Dmitry Shnurenko
 */
public class MachineWidgetImpl extends Composite implements MachineWidget, ClickHandler {
    interface MachineWidgetImplUiBinder extends UiBinder<Widget, MachineWidgetImpl> {
    }

    private static final MachineWidgetImplUiBinder UI_BINDER = GWT.create(MachineWidgetImplUiBinder.class);

    @UiField
    SimplePanel leftIcon;
    @UiField
    SimplePanel rightIcon;
    @UiField
    Label       name;

    private ActionDelegate delegate;
    private Machine        machine;

    @Inject
    public MachineWidgetImpl() {
        initWidget(UI_BINDER.createAndBindUi(this));

        addDomHandler(this, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Machine machine) {
        this.machine = machine;

        name.setText(machine.getId());
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(ClickEvent event) {
        delegate.onMachineClicked(machine);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }
}