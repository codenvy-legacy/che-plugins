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

import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance.server.Server;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.Tab;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.container.TabContainerView.TabSelectHandler;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.content.TabPresenter;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.header.TabHeader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Special factory for creating entities.
 *
 * @author Dmitry Shnurenko
 */
public interface EntityFactory {

    /**
     * Creates machine object.
     *
     * @return an instance of {@link Machine}
     */
    Machine createMachine();

    /**
     * Creates tab entity using special parameters.
     *
     * @param tabHeader
     *         header of tab
     * @param tabPresenter
     *         content of tab
     * @return an instance of {@link Tab}
     */
    Tab createTab(@Nonnull TabHeader tabHeader, @Nonnull TabPresenter tabPresenter, @Nullable TabSelectHandler handler);

    /**
     * Creates server entity with special parameters.
     *
     * @param name
     *         server name
     * @param address
     *         server address
     * @return an instance of {@link Server}
     */
    Server createServer(@Assisted("name") String name, @Assisted("address") String address);
}
