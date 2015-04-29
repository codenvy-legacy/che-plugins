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
package org.eclipse.che.ide.extension.machine.client.command.configuration.maven;

import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.ConfigurationFactory;

import javax.annotation.Nonnull;

/**
 * Factory for {@link MavenCommandConfiguration}s.
 *
 * @author Artem Zatsarynnyy
 */
public class MavenConfigurationFactory extends ConfigurationFactory {

    public MavenConfigurationFactory(CommandType commandType) {
        super(commandType);
    }

    @Override
    public CommandConfiguration createConfiguration(@Nonnull String name) {
        final MavenCommandConfiguration configuration = new MavenCommandConfiguration(name, getCommandType());

        configuration.setCommandLine("mvn clean install");

        return configuration;
    }
}
