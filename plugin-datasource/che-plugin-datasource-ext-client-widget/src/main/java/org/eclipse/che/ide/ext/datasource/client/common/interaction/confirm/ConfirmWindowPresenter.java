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

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.ext.datasource.client.common.interaction.CancelCallback;
import org.eclipse.che.ide.ext.datasource.client.common.interaction.ConfirmCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Confirmation window {@link ConfirmWindow} implementation.
 * 
 * @author "Mickaël Leduque"
 */
public class ConfirmWindowPresenter implements ConfirmWindow, ConfirmWindowView.ActionDelegate {

    /** This component view. */
    private final ConfirmWindowView view;

    /** The callback used on OK. */
    private final ConfirmCallback   confirmCallback;

    /** The callback used on cancel. */
    private final CancelCallback    cancelCallback;

    @AssistedInject
    public ConfirmWindowPresenter(final @NotNull ConfirmWindowView view,
                                  final @NotNull @Assisted("title") String title,
                                  final @NotNull @Assisted("message") String message,
                                  final @Nullable @Assisted ConfirmCallback confirmCallback,
                                  final @Nullable @Assisted CancelCallback cancelCallback) {
        this(view, title, new Label(message), confirmCallback, cancelCallback);
    }

    @AssistedInject
    public ConfirmWindowPresenter(final @NotNull ConfirmWindowView view,
                                  final @NotNull @Assisted String title,
                                  final @NotNull @Assisted IsWidget content,
                                  final @Nullable @Assisted ConfirmCallback confirmCallback,
                                  final @Nullable @Assisted CancelCallback cancelCallback) {
        this.view = view;
        this.view.setContent(content);
        this.view.setTitle(title);
        this.confirmCallback = confirmCallback;
        this.cancelCallback = cancelCallback;
        this.view.setDelegate(this);
    }

    @Override
    public void cancelled() {
        this.view.closeDialog();
        if (this.cancelCallback != null) {
            this.cancelCallback.cancelled();
        }
    }

    @Override
    public void accepted() {
        this.view.closeDialog();
        if (this.confirmCallback != null) {
            this.confirmCallback.accepted();
        }
    }

    @Override
    public void confirm() {
        this.view.showDialog();
    }
}
