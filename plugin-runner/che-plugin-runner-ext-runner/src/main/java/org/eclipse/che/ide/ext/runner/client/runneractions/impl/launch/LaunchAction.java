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
package org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch;

import com.google.inject.Inject;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.RunnerAction;
import org.eclipse.che.ide.ext.runner.client.tabs.console.container.ConsoleContainer;

import javax.validation.constraints.NotNull;

import static org.eclipse.che.ide.api.notification.Notification.Status.PROGRESS;

/**
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public class LaunchAction extends AbstractRunnerAction {

    private final NotificationManager        notificationManager;
    private final RunnerLocalizationConstant locale;
    private final AppContext                 appContext;
    private final RunnerActionFactory        runnerActionFactory;
    private final RunnerAction               outputAction;
    private final ConsoleContainer           consoleContainer;

    @Inject
    public LaunchAction(NotificationManager notificationManager,
                        RunnerLocalizationConstant locale,
                        AppContext appContext,
                        ConsoleContainer consoleContainer,
                        RunnerActionFactory runnerActionFactory) {
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.appContext = appContext;
        this.runnerActionFactory = runnerActionFactory;
        this.consoleContainer = consoleContainer;

        outputAction = runnerActionFactory.createOutput();
        addAction(outputAction);
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@NotNull Runner runner) {
        CurrentProject project = appContext.getCurrentProject();
        if (project == null) {
            return;
        }

        project.setIsRunningEnabled(false);

        String projectName = project.getProjectDescription().getName();
        String message = locale.environmentCooking(projectName);

        Notification notification = new Notification(message, PROGRESS, true);
        notificationManager.showNotification(notification);

        consoleContainer.printInfo(runner, message);

        RunnerAction statusAction = runnerActionFactory.createStatus(notification);
        addAction(statusAction);

        statusAction.perform(runner);
        outputAction.perform(runner);
    }
}