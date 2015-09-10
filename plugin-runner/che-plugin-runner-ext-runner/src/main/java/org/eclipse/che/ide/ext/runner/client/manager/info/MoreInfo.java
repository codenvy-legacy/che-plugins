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
package org.eclipse.che.ide.ext.runner.client.manager.info;

import org.eclipse.che.ide.ext.runner.client.models.Runner;
import com.google.gwt.user.client.ui.IsWidget;

import org.eclipse.che.commons.annotation.Nullable;

/**
 * Provides methods which allow update info about runner and display it on special widget.
 *
 * @author Dmitry Shnurenko
 */
public interface MoreInfo extends IsWidget {
    /**
     * Update runner information state which displayed on special widget.
     *
     * @param runner
     *         runner for which need update info
     */
    void update(@Nullable Runner runner);
}