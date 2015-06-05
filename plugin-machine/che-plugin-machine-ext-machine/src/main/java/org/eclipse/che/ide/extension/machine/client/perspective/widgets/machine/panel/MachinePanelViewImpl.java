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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.MachineWidget;

import javax.annotation.Nonnull;

/**
 * Provides implementation of view to display machines on special panel.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class MachinePanelViewImpl extends BaseView<MachinePanelView.ActionDelegate> implements MachinePanelView {
    interface MachinePanelImplUiBinder extends UiBinder<Widget, MachinePanelViewImpl> {
    }

    private static final MachinePanelImplUiBinder UI_BINDER = GWT.create(MachinePanelImplUiBinder.class);

    @UiField
    FlowPanel machines;

    @UiField
    Button createMachine;
    @UiField
    Button destroyMachine;

    @Inject
    public MachinePanelViewImpl(PartStackUIResources resources) {
        super(resources);

        setContentWidget(UI_BINDER.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void add(@Nonnull MachineWidget widget) {
        machines.add(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        machines.clear();
    }

    @UiHandler("createMachine")
    public void onCreateMachineClicked(@SuppressWarnings("UnusedParameters") ClickEvent event) {
        delegate.onCreateMachineButtonClicked();
    }

    @UiHandler("destroyMachine")
    public void onDestroyMachineClicked(@SuppressWarnings("UnusedParameters") ClickEvent event) {
        delegate.onDestroyMachineButtonClicked();
    }

}