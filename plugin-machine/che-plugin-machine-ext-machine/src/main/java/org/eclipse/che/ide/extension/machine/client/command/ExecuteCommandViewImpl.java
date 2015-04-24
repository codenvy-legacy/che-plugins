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
package org.eclipse.che.ide.extension.machine.client.command;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.ui.window.Window;

import javax.annotation.Nonnull;

/**
 * The implementation of {@link ExecuteCommandView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ExecuteCommandViewImpl extends Window implements ExecuteCommandView {

    private static ExecuteCommandViewImplUiBinder uiBinder = GWT.create(ExecuteCommandViewImplUiBinder.class);

    @UiField(provided = true)
    final MachineResources            res;
    @UiField(provided = true)
    final MachineLocalizationConstant locale;
    @UiField
    TextBox command;

    private ActionDelegate delegate;

    @Inject
    protected ExecuteCommandViewImpl(MachineResources resources, MachineLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;
        Widget widget = uiBinder.createAndBindUi(this);
        this.setTitle(locale.executeCommandViewTitle());
        this.setWidget(widget);

        createButtons();
    }

    @Override
    protected void onClose() {
    }

    private void createButtons() {
        final Button btnCancel = createButton(locale.executeCommandViewCancel(), "view-executeCommand-cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        final Button btnExecute = createButton(locale.executeCommandViewExecute(), "view-executeCommand-execute", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onExecuteClicked();
            }
        });
        getFooter().add(btnExecute);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getCommand() {
        return command.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setCommand(@Nonnull String command) {
        this.command.setText(command);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    interface ExecuteCommandViewImplUiBinder extends UiBinder<Widget, ExecuteCommandViewImpl> {
    }
}
