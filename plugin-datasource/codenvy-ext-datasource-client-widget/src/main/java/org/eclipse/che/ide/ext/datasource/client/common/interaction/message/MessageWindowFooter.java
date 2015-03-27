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
package org.eclipse.che.ide.ext.datasource.client.common.interaction.message;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.ext.datasource.client.common.interaction.InteractionWindowMessages;
import org.eclipse.che.ide.ext.datasource.client.common.interaction.message.MessageWindowView.ActionDelegate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * The footer show on message windows.
 * 
 * @author "Mickaël Leduque"
 */
public class MessageWindowFooter extends Composite {

    /** The UI binder instance. */
    private static MessageWindowFooterUiBinder uiBinder = GWT.create(MessageWindowFooterUiBinder.class);

    /** The action delegate. */
    private ActionDelegate                     actionDelegate;

    /** The i18n messages. */
    @UiField(provided = true)
    InteractionWindowMessages                      messages;

    @Inject
    public MessageWindowFooter(final @NotNull InteractionWindowMessages messages) {
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
     * Handler set on the OK button.
     * 
     * @param event the event that triggers the handler call
     */
    @UiHandler("okButton")
    public void handleOkClick(final ClickEvent event) {
        this.actionDelegate.accepted();
    }

    /**
     * The UI binder interface for this component.
     * 
     * @author "Mickaël Leduque"
     */
    interface MessageWindowFooterUiBinder extends UiBinder<Widget, MessageWindowFooter> {
    }
}
