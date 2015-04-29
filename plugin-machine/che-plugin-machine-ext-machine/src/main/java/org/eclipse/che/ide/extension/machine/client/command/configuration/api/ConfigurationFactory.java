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
 * Factory for {@link CommandConfiguration}s.
 *
 * @author Artem Zatsarynnyy
 */
public abstract class ConfigurationFactory {

    private final CommandType commandType;

    public ConfigurationFactory(CommandType commandType) {
        this.commandType = commandType;
    }

    @Nonnull
    public CommandType getCommandType() {
        return commandType;
    }

    /** Creates a new command configuration with the given name. */
    public abstract CommandConfiguration createConfiguration(@Nonnull String name);
}
