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
package org.eclipse.che.ide.extension.machine.client.command.configuration.gwt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Artem Zatsarynnyy
 */
public class GWTPageViewImpl implements GWTPageView {

    private static final GWTPageViewImplUiBinder UI_BINDER = GWT.create(GWTPageViewImplUiBinder.class);
    private final DockLayoutPanel rootElement;

    @UiField
    TextBox devModeParametersField;
    @UiField
    TextBox vmOptionsField;

    private ActionDelegate delegate;

    public GWTPageViewImpl() {
        rootElement = UI_BINDER.createAndBindUi(this);

        devModeParametersField.setFocus(true);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    @Override
    public String getDevModeParameters() {
        return devModeParametersField.getText();
    }

    @Override
    public void setDevModeParameters(String value) {
        devModeParametersField.setText(value);
    }

    @Override
    public String getVmOptionsField() {
        return vmOptionsField.getText();
    }

    @Override
    public void setVmOptionsField(String vmOptions) {
        vmOptionsField.setText(vmOptions);
    }

    @UiHandler({"devModeParametersField"})
    void onKeyUpInDevModeParametersField(KeyUpEvent event) {
        delegate.onDevModeParametersChanged(getDevModeParameters());
    }

    @UiHandler({"vmOptionsField"})
    void onKeyUpInVmOptionsField(KeyUpEvent event) {
        delegate.onVmOptionsChanged(getVmOptionsField());
    }

    interface GWTPageViewImplUiBinder extends UiBinder<DockLayoutPanel, GWTPageViewImpl> {
    }
}
