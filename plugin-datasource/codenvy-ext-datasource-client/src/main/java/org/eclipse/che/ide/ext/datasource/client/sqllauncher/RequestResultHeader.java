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
package org.eclipse.che.ide.ext.datasource.client.sqllauncher;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Interface to the request result header.
 * 
 * @author "Mickaël Leduque"
 */
public interface RequestResultHeader extends IsWidget {
    /**
     * Sets the delegate that will handle actions.
     * 
     * @param delegate the delegate
     */
    void setOpenCloseDelegate(OpenCloseDelegate delegate);

    /**
     * Change view to reflext opened/closed state.
     * 
     * @param open state
     */
    void setOpen(boolean open);

    /**
     * The interface for the action delegate.
     * 
     * @author "Mickaël Leduque"
     */
    public interface OpenCloseDelegate {
        /** Handles the open/close action. */
        void onOpenClose();
    }
}
