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

import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.CommandType;

import javax.annotation.Nonnull;

/**
 * Represents Maven command.
 *
 * @author Artem Zatsarynnyy
 */
public class MavenCommandConfiguration extends CommandConfiguration {

    private String commandLine;

    protected MavenCommandConfiguration(String id, CommandType type, String name) {
        super(id, type, name);
        commandLine = "";
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    @Nonnull
    @Override
    public String toCommandLine() {
        return "mvn " + getCommandLine();
    }
}
