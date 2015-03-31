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
package org.eclipse.che.ide.ext.datasource.client.editdatasource;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.ext.datasource.client.editdatasource.EditDatasourcesView.ActionDelegate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Widget used as footer in the "Manage Datasources" dialog window.
 * 
 * @author "Mickaël Leduque"
 */
public class EditWindowFooter extends Composite {

    /** The UI binder instance for this class. */
    private static EditWindowFooterUiBinder uiBinder = GWT.create(EditWindowFooterUiBinder.class);

    /** The action delegate. */
    private ActionDelegate                  actionDelegate;

    /** The i18n messages. */
    @UiField(provided = true)
    EditDatasourceMessages                  messages;

    @Inject
    public EditWindowFooter(final @NotNull EditDatasourceMessages messages) {
        this.messages = messages;
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Sets the action delegate.
     * 
     * @param delegate the new value
     */
    public void setDelegate(final ActionDelegate delegate) {
        this.actionDelegate = delegate;
    }

    /**
     * Handler set on the close button.
     * 
     * @param event the event that triggers the handler call
     */
    @UiHandler("closeButton")
    public void handleCloseClick(final ClickEvent event) {
        this.actionDelegate.closeDialog();
    }

    /**
     * The UI binder interface for this view component.
     * 
     * @author "Mickaël Leduque"
     */
    interface EditWindowFooterUiBinder extends UiBinder<Widget, EditWindowFooter> {
    }
}
