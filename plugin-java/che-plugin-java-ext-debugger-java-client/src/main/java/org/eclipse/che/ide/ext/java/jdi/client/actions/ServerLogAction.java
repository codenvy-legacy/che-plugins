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
package org.eclipse.che.ide.ext.java.jdi.client.actions;

import com.google.inject.Inject;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.outputspanel.OutputsContainerPresenter;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.CommandConsoleFactory;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.DefaultOutputConsole;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.OutputConsole;

/**
 * The action contains business logic which displays logs of the extension server.
 *
 * @author Roman Nikitenko
 */
public class ServerLogAction extends Action {

    //TODO This is a temporary solution
    private static final String SERVER_LOG_PATH = "/home/user/che/ext-server/logs/catalina.out";

    private final AppContext                      appContext;
    private final AnalyticsEventLogger            eventLogger;
    private final CommandConsoleFactory           commandConsoleFactory;
    private final OutputsContainerPresenter       outputsContainerPresenter;
    private final MachineServiceClient            machineServiceClient;
    private final WorkspaceAgent                  workspaceAgent;
    private final JavaRuntimeLocalizationConstant locale;

    @Inject
    public ServerLogAction(AppContext appContext,
                           CommandConsoleFactory commandConsoleFactory,
                           OutputsContainerPresenter outputsContainerPresenter,
                           MachineServiceClient machineServiceClient,
                           JavaRuntimeLocalizationConstant locale,
                           WorkspaceAgent workspaceAgent,
                           AnalyticsEventLogger eventLogger) {

        super(locale.displayServerLogTitle(), locale.displayServerLogDescription(), null, null);

        this.locale = locale;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
        this.workspaceAgent = workspaceAgent;
        this.commandConsoleFactory = commandConsoleFactory;
        this.machineServiceClient = machineServiceClient;
        this.outputsContainerPresenter = outputsContainerPresenter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        final OutputConsole console = commandConsoleFactory.create(locale.serverLogTabTitle());
        outputsContainerPresenter.addConsole(console);
        workspaceAgent.setActivePart(outputsContainerPresenter);

        machineServiceClient.getFileContent(appContext.getDevMachineId(), SERVER_LOG_PATH, 1, 10_000).then(new Operation<String>() {
            @Override
            public void apply(String content) throws OperationException {
                ((DefaultOutputConsole)console).printText(content);
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                ((DefaultOutputConsole)console).printText(arg.getMessage());
            }
        });
    }
}
