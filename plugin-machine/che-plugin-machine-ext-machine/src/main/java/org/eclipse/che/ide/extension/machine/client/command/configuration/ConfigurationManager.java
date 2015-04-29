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

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.command.configuration.gwt.GWTCommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.gwt.GWTCommandType;
import org.eclipse.che.ide.extension.machine.client.command.configuration.maven.MavenCommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.maven.MavenCommandType;

import java.util.HashSet;
import java.util.Set;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ConfigurationManager {

    private final Set<CommandType> commandTypes;
    private final Set<CommandConfiguration> commandConfigurations;

    @Inject
    public ConfigurationManager(Set<CommandType> commandTypes, GWTCommandType gwt, MavenCommandType mvn) {
        this.commandTypes = commandTypes;
        commandConfigurations = new HashSet<>();
        commandConfigurations.add(new GWTCommandConfiguration("GWT Super DevMode", gwt));
        commandConfigurations.add(new MavenCommandConfiguration("Maven Build", mvn));
    }

    public Set<CommandType> getCommandTypes() {
        return new HashSet<>(commandTypes);
    }

    public Set<CommandConfiguration> getCommandConfigurations() {
        return new HashSet<>(commandConfigurations);
    }

    /** Launches the given configuration. */
    public void launch(CommandConfiguration configuration) {
        // TODO
    }
}
