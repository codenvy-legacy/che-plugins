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
package org.eclipse.che.ide.ext.runner.client.tabs.common;

import com.google.gwt.user.client.ui.IsWidget;

import org.eclipse.che.ide.api.mvp.Presenter;

import javax.validation.constraints.NotNull;

/**
 * Provides general methods which must be implemented by all presenters which are added in tab container.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public interface TabPresenter extends Presenter {

    /** @return view representation of current tab. */
    @NotNull
    IsWidget getView();

    /**
     * Sets visibility of tab.
     *
     * @param visible
     *         <code>true</code> tab is visible,<code>false</code> tab isn't visible
     */
    void setVisible(boolean visible);

}