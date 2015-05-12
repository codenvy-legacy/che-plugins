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
package org.eclipse.che.ide.extension.machine.client.command.configuration.api;

import javax.annotation.Nonnull;

/**
 * The type of a command.
 *
 * @author Artem Zatsarynnyy
 */
public interface CommandType {

    /** Returns unique identifier for this command type. */
    @Nonnull
    String getId();

    /** Returns name of this command type. */
    @Nonnull
    String getDisplayName();

    /** Returns the {@link ConfigurationPage} that allows to configure specific command parameters. */
    @Nonnull
    ConfigurationPage getConfigurationPage();

    /** Returns factory for {@link CommandConfiguration} instances. */
    @Nonnull
    ConfigurationFactory getConfigurationFactory();
}
