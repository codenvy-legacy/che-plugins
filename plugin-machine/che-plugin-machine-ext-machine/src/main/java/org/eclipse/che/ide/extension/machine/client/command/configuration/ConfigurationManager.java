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

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.ProcessDescriptor;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandType;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.util.loging.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ConfigurationManager {

    private final Set<CommandType> commandTypes;
    private final MachineManager   machineManager;
    private final MachineServiceClient machineServiceClient;
    private final Set<CommandConfiguration> commandConfigurations;

    @Inject
    public ConfigurationManager(Set<CommandType> commandTypes,
                                MachineManager machineManager,
                                MachineServiceClient machineServiceClient) {
        this.commandTypes = commandTypes;
        this.machineManager = machineManager;
        this.machineServiceClient = machineServiceClient;
        commandConfigurations = new HashSet<>();

        // TODO: fake configurations
        final Iterator<CommandType> iterator = commandTypes.iterator();
        commandConfigurations.add(iterator.next().getConfigurationFactory().createConfiguration("GWT Super DevMode"));
        commandConfigurations.add(iterator.next().getConfigurationFactory().createConfiguration("Maven Build"));
    }

    public Set<CommandType> getCommandTypes() {
        return new HashSet<>(commandTypes);
    }

    public Set<CommandConfiguration> getCommandConfigurations() {
        return new HashSet<>(commandConfigurations);
    }

    /** Create new configuration of the specified type. */
    public CommandConfiguration createConfiguration(CommandType type) {
        // TODO
        return type.getConfigurationFactory().createConfiguration("conf name");
    }

    /** Remove the given configuration. */
    public void removeConfiguration(CommandConfiguration configuration) {
        // TODO
    }

    /** Launch the command with the given configuration. */
    public void launch(CommandConfiguration configuration) {
        final String currentMachineId = machineManager.getCurrentMachineId();

        machineServiceClient.executeCommandInMachine(
                currentMachineId, configuration.getCommand(), "output", new AsyncRequestCallback<ProcessDescriptor>() {
                    @Override
                    protected void onSuccess(ProcessDescriptor result) {
                        // TODO: print to output
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(ConfigurationManager.class, exception);
                    }
                });
    }
}
