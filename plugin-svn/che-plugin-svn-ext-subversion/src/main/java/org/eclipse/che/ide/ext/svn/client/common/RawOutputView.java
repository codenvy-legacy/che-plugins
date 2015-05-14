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
package org.eclipse.che.ide.ext.svn.client.common;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;


/**
 * View for {@link RawOutputPresenter}.
 */
public interface RawOutputView extends View<RawOutputView.ActionDelegate> {

    /**
     * Action handler for the view actions/controls.
     */
    interface ActionDelegate extends BaseActionDelegate {
        /**
         * Handle the clear button.
         */
        void onClearClicked();
    }

    /**
     * Set title of view.
     *
     * @param title The title to display
     */
    void setTitle(final String title);

    /**
     * Print text in view.
     *
     * @param safeText The text to display
     */
    void print(final String safeText);

    /**
     * Clear console. Remove all messages.
     */
    void clear();

    /**
     * Scroll to bottom of the view.
     */
    void scrollBottom();

}
