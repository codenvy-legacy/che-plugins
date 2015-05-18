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
package org.eclipse.che.ide.extension.maven.client.command;

import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandType;

import javax.annotation.Nonnull;

/**
 * @author Artem Zatsarynnyy
 */
public class MavenCommandConfiguration implements CommandConfiguration {

    private final CommandType type;
    private       String      name;
    private       String      id;
    private       String      commandLine;

    public MavenCommandConfiguration(String name, CommandType type) {
        this.name = name;
        this.type = type;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(@Nonnull String id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    @Override
    public CommandType getType() {
        return type;
    }

    @Nonnull
    @Override
    public String toCommandLine() {
        return "mvn " + getCommandLine();
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }
}
