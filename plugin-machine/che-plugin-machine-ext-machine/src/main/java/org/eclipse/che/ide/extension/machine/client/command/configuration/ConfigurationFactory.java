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
package org.eclipse.che.ide.extension.machine.client.command.configuration;

import javax.annotation.Nonnull;

/**
 * Factory for {@link CommandConfiguration} instances.
 *
 * @param <T>
 *         type of the command configuration which this factory produces
 * @author Artem Zatsarynnyy
 */
public abstract class ConfigurationFactory<T extends CommandConfiguration> {

    private final CommandType commandType;

    /**
     * Creates new command configuration factory for the specified command type.
     *
     * @param commandType
     *         type of the command configuration which this factory should create
     */
    protected ConfigurationFactory(CommandType commandType) {
        this.commandType = commandType;
    }

    /** Returns type of the command configuration which this factory creates. */
    @Nonnull
    public CommandType getCommandType() {
        return commandType;
    }

    /** Creates a new command configuration with the given name. */
    public abstract T createConfiguration(@Nonnull String name);
}
