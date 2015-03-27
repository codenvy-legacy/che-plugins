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
package org.eclipse.che.ide.ext.runner.client.manager.button;

import org.eclipse.che.ide.api.mvp.View;

/**
 * Provides methods which allow change visual representation of button.
 *
 * @author Dmitry Shnurenko
 */
public interface ButtonWidget extends View<ButtonWidget.ActionDelegate> {

    /** Changes state of the button on disable. */
    void setDisable();

    /** Changes state of the button on enable. */
    void setEnable();

    interface ActionDelegate {
        /** Performs some actions in response to user's clicking on the button panel. */
        void onButtonClicked();
    }
}