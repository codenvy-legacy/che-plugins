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
package org.eclipse.che.ide.ext.openshift.client.webhooks;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ext.openshift.shared.dto.WebHook;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The view of {@link ShowWebhooksPresenter}.
 *
 * @author Sergii Leschenko
 */
@ImplementedBy(ShowWebhooksViewImpl.class)
public interface ShowWebhooksView extends View<ShowWebhooksView.ActionDelegate> {
    /** Needs for delegate some function into webhooks view. */
    interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();
    }

    /**
     * Set application webhooks into field on the view.
     *
     * @param webhooks
     *         application webhooks what will be shown on view
     */
    void setWebhooks(@NotNull List<WebHook> webhooks);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
