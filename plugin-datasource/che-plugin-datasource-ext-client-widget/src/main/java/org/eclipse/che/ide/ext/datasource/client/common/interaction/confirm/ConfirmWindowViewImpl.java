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
package org.eclipse.che.ide.ext.datasource.client.common.interaction.confirm;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Implementation for the confirmation window view.
 * 
 * @author "Mickaël Leduque"
 */
public class ConfirmWindowViewImpl extends Window implements ConfirmWindowView {

    /** The UI binder instance. */
    private static ConfirmWindowUiBinder uiBinder = GWT.create(ConfirmWindowUiBinder.class);

    /** The container for the window content. */
    @UiField
    SimplePanel                          content;

    /** The window footer. */
    private final ConfirmWindowFooter    footer;

    @Inject
    public ConfirmWindowViewImpl(final @NotNull ConfirmWindowFooter footer) {
        Widget widget = uiBinder.createAndBindUi(this);
        setWidget(widget);

        this.footer = footer;
        getFooter().add(this.footer);
    }

    @Override
    public void setDelegate(final ActionDelegate delegate) {
        this.footer.setDelegate(delegate);
    }


    @Override
    protected void onClose() {
    }

    @Override
    public void showDialog() {
        this.show();
    }

    @Override
    public void closeDialog() {
        this.hide();
    }

    @Override
    public void setContent(final IsWidget content) {
        this.content.clear();
        this.content.setWidget(content);
    }

    /**
     * The UI binder interface for this components.
     * 
     * @author "Mickaël Leduque"
     */
    interface ConfirmWindowUiBinder extends UiBinder<Widget, ConfirmWindowViewImpl> {
    }
}
