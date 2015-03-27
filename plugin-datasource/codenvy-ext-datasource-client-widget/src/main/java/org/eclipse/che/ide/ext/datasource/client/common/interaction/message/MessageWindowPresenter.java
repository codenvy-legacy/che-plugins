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

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.ext.datasource.client.common.interaction.ConfirmCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Message/information window {@link MessageWindow} implementation.
 * 
 * @author "Mickaël Leduque"
 */
public class MessageWindowPresenter implements MessageWindow, MessageWindowView.ActionDelegate {

    /** This component view. */
    private final MessageWindowView view;

    /** The callback used on OK. */
    private final ConfirmCallback   confirmCallback;

    @AssistedInject
    public MessageWindowPresenter(final @NotNull MessageWindowView view,
                                  final @NotNull @Assisted("title") String title,
                                  final @NotNull @Assisted("message") String message,
                                  final @Nullable @Assisted ConfirmCallback confirmCallback) {
        this(view, title, new Label(message), confirmCallback);
    }

    @AssistedInject
    public MessageWindowPresenter(final @NotNull MessageWindowView view,
                                  final @NotNull @Assisted String title,
                                  final @NotNull @Assisted IsWidget content,
                                  final @Nullable @Assisted ConfirmCallback confirmCallback) {
        this.view = view;
        this.view.setContent(content);
        this.view.setTitle(title);
        this.confirmCallback = confirmCallback;
        this.view.setDelegate(this);
    }

    @Override
    public void accepted() {
        this.view.closeDialog();
        if (this.confirmCallback != null) {
            this.confirmCallback.accepted();
        }
    }

    @Override
    public void inform() {
        this.view.showDialog();
    }
}
