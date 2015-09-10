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
package org.eclipse.che.ide.ext.runner.client.util;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerPresenter;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.tabs.console.container.ConsoleContainer;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

import static org.eclipse.che.ide.api.notification.Notification.Status.FINISHED;
import static org.eclipse.che.ide.api.notification.Notification.Type.ERROR;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.RAM.MB_100;

/**
 * Contains implementations of methods which are general for runner plugin classes.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class RunnerUtilImpl implements RunnerUtil {

    private final DialogFactory              dialogFactory;
    private final RunnerLocalizationConstant locale;
    private final Provider<RunnerManagerPresenter>     presenter;
    private final NotificationManager        notificationManager;
    private final ConsoleContainer           consoleContainer;
    private final AppContext appContext;

    @Inject
    public RunnerUtilImpl(DialogFactory dialogFactory,
                          RunnerLocalizationConstant locale,
                          Provider<RunnerManagerPresenter> presenter,
                          ConsoleContainer consoleContainer,
                          NotificationManager notificationManager,
                          AppContext appContext) {
        this.dialogFactory = dialogFactory;
        this.locale = locale;
        this.presenter = presenter;
        this.notificationManager = notificationManager;
        this.consoleContainer = consoleContainer;
        this.appContext = appContext;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRunnerMemoryCorrect(@Min(value=0) int totalMemory, @Min(value=0) int usedMemory, @Min(value=0) int availableMemory) {
        if (usedMemory < 0 || totalMemory < 0 || availableMemory < 0) {
            showWarning(locale.messagesIncorrectValue());
            return false;
        }

        if (usedMemory % MB_100.getValue() != 0) {
            showWarning(locale.ramSizeMustBeMultipleOf(MB_100.getValue()));
            return false;
        }

        if (usedMemory > totalMemory) {
            showWarning(locale.messagesTotalRamLessCustom(usedMemory, totalMemory));
            return false;
        }

        if (usedMemory > availableMemory) {
            showWarning(locale.messagesAvailableRamLessCustom(usedMemory, totalMemory, totalMemory - availableMemory));
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void showWarning(@NotNull String message) {
        dialogFactory.createMessageDialog(locale.titlesWarning(), message, null).show();
    }

    /** {@inheritDoc} */
    @Override
    public void showError(@NotNull Runner runner, @NotNull String message, @Nullable Throwable exception) {
        Notification notification = new Notification(message, ERROR, true);

        showError(runner, message, exception, notification);

        notificationManager.showNotification(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void showError(@NotNull Runner runner,
                          @NotNull String message,
                          @Nullable Throwable exception,
                          @NotNull Notification notification) {
        runner.setStatus(Runner.Status.FAILED);

        presenter.get().update(runner);

        notification.update(message, ERROR, FINISHED, null, true);

        if (exception != null && exception.getMessage() != null) {
            consoleContainer.printError(runner, message + ": " + exception.getMessage());
        } else {
            consoleContainer.printError(runner, message);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasRunPermission() {
        CurrentProject currentProject = appContext.getCurrentProject();
        return currentProject != null && currentProject.getProjectDescription().getPermissions().contains("run");
    }
}