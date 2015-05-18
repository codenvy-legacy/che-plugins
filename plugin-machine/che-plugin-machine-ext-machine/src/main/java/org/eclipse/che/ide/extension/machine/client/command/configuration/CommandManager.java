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
import org.eclipse.che.ide.extension.machine.client.console.MachineConsolePresenter;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
import org.eclipse.che.ide.extension.machine.client.OutputMessageUnmarshaller;
import org.eclipse.che.ide.util.UUID;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Manager for command operations.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CommandManager {

    private final Set<CommandType>          commandTypes;
    private final MachineManager            machineManager;
    private final MachineServiceClient      machineServiceClient;
    private final MessageBus                messageBus;
    private final MachineConsolePresenter   machineConsole;
    private final Set<CommandConfiguration> commandConfigurations;

    @Inject
    public CommandManager(Set<CommandType> commandTypes,
                          MachineManager machineManager,
                          MachineServiceClient machineServiceClient,
                          MessageBus messageBus,
                          MachineConsolePresenter machineConsole) {
        this.commandTypes = commandTypes;
        this.machineManager = machineManager;
        this.machineServiceClient = machineServiceClient;
        this.messageBus = messageBus;
        this.machineConsole = machineConsole;
        commandConfigurations = new HashSet<>();

        fetchCommandConfigurations();
    }

    private void fetchCommandConfigurations() {
        // TODO: use Command API
        final Iterator<CommandType> iterator = commandTypes.iterator();

        final CommandType gwtCommandType = iterator.next();
        commandConfigurations.add(gwtCommandType.getConfigurationFactory().createConfiguration("GWT Super DevMode"));

        final CommandType mavenCommandType = iterator.next();
        commandConfigurations.add(mavenCommandType.getConfigurationFactory().createConfiguration("Maven Build"));
        commandConfigurations.add(mavenCommandType.getConfigurationFactory().createConfiguration("Build module 1"));
        commandConfigurations.add(mavenCommandType.getConfigurationFactory().createConfiguration("Build module 2"));
    }

    /** Returns all registered command types. */
    public Set<CommandType> getCommandTypes() {
        return new HashSet<>(commandTypes);
    }

    /** Returns all registered command configurations. */
    public Set<CommandConfiguration> getCommandConfigurations() {
        return new HashSet<>(commandConfigurations);
    }

    /** Create new command configuration of the specified type. */
    public CommandConfiguration createConfiguration(@Nonnull CommandType type) {
        // TODO: use Command API
        final CommandConfiguration configuration = type.getConfigurationFactory().createConfiguration(UUID.uuid(5));
        commandConfigurations.add(configuration);
        return configuration;
    }

    /** Remove the given command configuration. */
    public void removeConfiguration(@Nonnull CommandConfiguration configuration) {
        // TODO: use Command API
        commandConfigurations.remove(configuration);
    }

    /** Execute the the given command configuration. */
    public void execute(@Nonnull CommandConfiguration configuration) {
        final String currentMachineId = machineManager.getCurrentMachineId();
        if (currentMachineId == null) {
            return;
        }

        final String outputChannel = getOutputChannel();
        subscribeToOutput(outputChannel);

        machineServiceClient.executeCommand(currentMachineId, configuration.toCommandLine(), outputChannel);
    }

    @Nonnull
    private String getOutputChannel() {
        return "process:output:" + UUID.uuid();
    }

    private void subscribeToOutput(final String channel) {
        try {
            messageBus.subscribe(
                    channel,
                    new SubscriptionHandler<String>(new OutputMessageUnmarshaller()) {
                        @Override
                        protected void onMessageReceived(String result) {
                            machineConsole.print(result);
                        }

                        @Override
                        protected void onErrorReceived(Throwable exception) {
                            Log.error(CommandManager.class, exception);
                        }
                    });
        } catch (WebSocketException e) {
            Log.error(CommandManager.class, e);
        }
    }
}
