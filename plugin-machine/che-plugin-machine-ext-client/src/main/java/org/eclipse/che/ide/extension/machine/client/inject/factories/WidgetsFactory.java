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
package org.eclipse.che.ide.extension.machine.client.inject.factories;

import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.header.TabHeader;

import javax.annotation.Nonnull;

/**
 * Special factory for creating different widgets.
 *
 * @author Dmitry Shnurenko
 */
public interface WidgetsFactory {

    /**
     * Creates widget for tab header.
     *
     * @param tabName
     *         name which need set to tab
     * @return an instance of {@link TabHeader}
     */
    TabHeader createTabHeader(@Nonnull String tabName);
}
