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
package org.eclipse.che.ide.extension.machine.client.actions;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandManager;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandConfiguration;

/**
 * Action to execute command which is selected in command list.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ExecuteSelectedCommandAction extends Action {
    private final AppContext           appContext;
    private final ChooseCommandAction  chooseCommandAction;
    private final CommandManager       commandManager;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public ExecuteSelectedCommandAction(MachineLocalizationConstant localizationConstant,
                                        MachineResources resources,
                                        AppContext appContext,
                                        ChooseCommandAction chooseCommandAction,
                                        CommandManager commandManager,
                                        AnalyticsEventLogger eventLogger) {
        super(localizationConstant.executeSelectedCommandControlTitle(),
              localizationConstant.executeSelectedCommandControlDescription(),
              null,
              resources.execute());
        this.appContext = appContext;
        this.chooseCommandAction = chooseCommandAction;
        this.commandManager = commandManager;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        final CommandConfiguration commandConfiguration = chooseCommandAction.selectEnvironment();
        if (commandConfiguration != null) {
            commandManager.execute(commandConfiguration);
        }
    }
}
