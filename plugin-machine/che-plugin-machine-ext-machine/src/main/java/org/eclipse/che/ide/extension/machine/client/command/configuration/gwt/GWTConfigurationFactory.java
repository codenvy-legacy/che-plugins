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
package org.eclipse.che.ide.extension.machine.client.command.configuration.gwt;

import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.configuration.ConfigurationFactory;

import javax.annotation.Nonnull;

/**
 * Factory for {@link GWTCommandConfiguration} instances.
 *
 * @author Artem Zatsarynnyy
 */
public class GWTConfigurationFactory extends ConfigurationFactory<GWTCommandConfiguration> {

    protected GWTConfigurationFactory(CommandType commandType) {
        super(commandType);
    }

    @Override
    public GWTCommandConfiguration createConfiguration(@Nonnull String name) {
        final GWTCommandConfiguration configuration = new GWTCommandConfiguration(name, getCommandType());

        configuration.setDevModeParameters("-noincremental -nostartServer -port 8080");
        configuration.setVmOptions("-Xss512m -Xmx2048m -XX:MaxPermSize=1024m");

        return configuration;
    }
}
