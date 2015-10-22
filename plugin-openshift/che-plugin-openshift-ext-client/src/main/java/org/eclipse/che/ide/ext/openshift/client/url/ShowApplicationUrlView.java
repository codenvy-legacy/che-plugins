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
package org.eclipse.che.ide.ext.openshift.client.url;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The view of {@link ShowApplicationUrlPresenter}.
 *
 * @author Sergii Leschenko
 */
@ImplementedBy(ShowApplicationUrlViewImpl.class)
public interface ShowApplicationUrlView extends View<ShowApplicationUrlView.ActionDelegate> {
    /** Needs for delegate some function into application url view. */
    interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();
    }

    /**
     * Set application URLs into field on the view.
     *
     * @param URLs
     *         application URLs what will be shown on view
     */
    void setURLs(@NotNull List<String> URLs);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
