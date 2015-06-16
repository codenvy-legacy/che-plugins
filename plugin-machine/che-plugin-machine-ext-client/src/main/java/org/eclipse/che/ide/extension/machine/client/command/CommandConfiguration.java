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
package org.eclipse.che.ide.extension.machine.client.command;

import javax.annotation.Nonnull;

/**
 * Abstract command which can be configured and executed in machine.
 *
 * @author Artem Zatsarynnyy
 */
public abstract class CommandConfiguration {

    private final String      id;
    private final CommandType type;
    private       String      name;

    /**
     * Creates new command configuration of the specified type with the given name and ID.
     *
     * @param id
     *         command ID
     * @param type
     *         type of the command
     * @param name
     *         command name
     */
    protected CommandConfiguration(@Nonnull String id, @Nonnull CommandType type, @Nonnull String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    /** Returns unique identifier for this command configuration. */
    @Nonnull
    public String getId() {
        return id;
    }

    /** Returns command configuration name. */
    @Nonnull
    public String getName() {
        return name;
    }

    /** Sets command configuration name. */
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    /** Returns command configuration type. */
    @Nonnull
    public CommandType getType() {
        return type;
    }

    /** Returns command line to execute in machine. */
    @Nonnull
    public abstract String toCommandLine();
}
