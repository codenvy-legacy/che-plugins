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
package org.eclipse.che.ide.ext.svn.client.merge;

import com.google.gwt.user.client.ui.HasValue;
import org.eclipse.che.ide.api.mvp.View;

/**
 * An interface representing Merge view.
 */
public interface MergeView extends View<MergeView.ActionDelegate> {

    interface ActionDelegate {

        void mergeClicked();

        void cancelClicked();

        /** Perform actions when source url check box changed. */
        void onSourceCheckBoxChanged();

    }

    /**
     * Displays the view.
     */
    void show();

    /**
     * Hides the view.
     */
    void hide();

    /** Returns checkbox indicating type of the target. */
    HasValue<Boolean> targetCheckBox();

    void setTargetIsURL(boolean targetIsURL);

    /** Returns target text box */
    HasValue<String> targetTextBox();

    void enableTargetTextBox(boolean enabled);

}
