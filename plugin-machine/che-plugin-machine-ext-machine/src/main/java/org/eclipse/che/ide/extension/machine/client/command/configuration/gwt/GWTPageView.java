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
package org.eclipse.che.ide.extension.machine.client.command.configuration.gwt;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;

/**
 * @author Artem Zatsarynnyy
 */
@ImplementedBy(GWTPageViewImpl.class)
public interface GWTPageView extends View<GWTPageView.ActionDelegate> {

    String getDevModeParameters();

    void setDevModeParameters(String parameters);

    String getVmOptionsField();

    void setVmOptionsField(String vmOptions);

    interface ActionDelegate {

        void onDevModeParametersChanged(String devModeParameters);

        void onVmOptionsChanged(String vmOptions);
    }
}
