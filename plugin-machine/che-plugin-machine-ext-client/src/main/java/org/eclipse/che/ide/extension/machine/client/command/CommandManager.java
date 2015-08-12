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

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.ProcessDescriptor;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.command.valueproviders.CommandValueProvider;
import org.eclipse.che.ide.extension.machine.client.command.valueproviders.CommandValueProviderRegistry;
import org.eclipse.che.ide.extension.machine.client.outputspanel.OutputsContainerPresenter;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.CommandConsoleFactory;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.OutputConsole;
import org.eclipse.che.ide.util.UUID;

import javax.annotation.Nonnull;

/**
 * Manager for command operations.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CommandManager {

    private final MachineServiceClient         machineServiceClient;
    private final OutputsContainerPresenter    outputsContainerPresenter;
    private final CommandConsoleFactory        commandConsoleFactory;
    private final NotificationManager          notificationManager;
    private final MachineLocalizationConstant  localizationConstant;
    private final WorkspaceAgent               workspaceAgent;
    private final AppContext                   appContext;
    private final CommandValueProviderRegistry commandValueProviderRegistry;

    @Inject
    public CommandManager(MachineServiceClient machineServiceClient,
                          OutputsContainerPresenter outputsContainerPresenter,
                          CommandConsoleFactory commandConsoleFactory,
                          NotificationManager notificationManager,
                          MachineLocalizationConstant localizationConstant,
                          WorkspaceAgent workspaceAgent,
                          AppContext appContext,
                          CommandValueProviderRegistry commandValueProviderRegistry) {
        this.machineServiceClient = machineServiceClient;
        this.outputsContainerPresenter = outputsContainerPresenter;
        this.commandConsoleFactory = commandConsoleFactory;
        this.notificationManager = notificationManager;
        this.localizationConstant = localizationConstant;
        this.workspaceAgent = workspaceAgent;
        this.appContext = appContext;
        this.commandValueProviderRegistry = commandValueProviderRegistry;
    }

    /** Execute the the given command configuration on the developer machine. */
    public void execute(@Nonnull CommandConfiguration configuration) {
        final String devMachineId = appContext.getDevMachineId();
        if (devMachineId == null) {
            notificationManager.showWarning(localizationConstant.noDevMachine());
            return;
        }

        final String outputChannel = "process:output:" + UUID.uuid();

        final OutputConsole console = commandConsoleFactory.create(configuration, devMachineId);
        console.listenToOutput(outputChannel);
        outputsContainerPresenter.addConsole(console);
        workspaceAgent.setActivePart(outputsContainerPresenter);

        final String commandLine = processCommandLineVariables(configuration.toCommandLine());

        final Promise<ProcessDescriptor> processPromise = machineServiceClient.executeCommand(devMachineId, commandLine, outputChannel);
        processPromise.then(new Operation<ProcessDescriptor>() {
            @Override
            public void apply(ProcessDescriptor arg) throws OperationException {
                console.attachToProcess(arg.getPid());
            }
        });
    }

    /**
     * Returns the given command line with values of variables.
     *
     * @see CommandValueProvider
     */
    public String processCommandLineVariables(final String commandLine) {
        String cmdLine = commandLine;

        for (CommandValueProvider valueProvider : commandValueProviderRegistry.getValueProviders()) {
            cmdLine = cmdLine.replace(valueProvider.getKey(), valueProvider.getValue());
        }

        return cmdLine;
    }
}
