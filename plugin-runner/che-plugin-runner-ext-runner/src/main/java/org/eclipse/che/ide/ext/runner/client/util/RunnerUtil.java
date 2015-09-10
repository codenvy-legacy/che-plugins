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

import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.ext.runner.client.models.Runner;

import com.google.inject.ImplementedBy;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * The class contains methods which are general used.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(RunnerUtilImpl.class)
public interface RunnerUtil {

    /**
     * Checks correctness total and used memory. Method shows different messages depending on the values of total and used memory.
     *
     * @param totalMemory
     *         total memory value which accessed current user
     * @param usedMemory
     *         value of memory which used by runner
     * @param availableMemory
     *         value of available runner memory
     * @return <code>true</code> memory values are correct,<code>false</code> memory values are incorrect
     */
    boolean isRunnerMemoryCorrect(@Min(value=0) int totalMemory, @Min(value=0) int usedMemory, @Min(value=0) int availableMemory);

    /**
     * Shows warning message using dialog factory.
     *
     * @param message
     *         message which need to show
     */
    void showWarning(@NotNull String message);

    /**
     * Show error to user. It creates a new notification and shows it. Updates Multi-runner panel and print a message on the console for a
     * given runner.
     *
     * @param runner
     *         a runner that is bound with console where a message have to be shown
     * @param message
     *         message that needs to be shown
     * @param exception
     *         exception that happened
     */
    void showError(@NotNull Runner runner, @NotNull String message, @Nullable Throwable exception);

    /**
     * Show error to user. It updates a given notification, updates Multi-runner panel and print a message on the console for a
     * given runner.
     *
     * @param runner
     *         a runner that is bound with console where a message have to be shown
     * @param message
     *         message that needs to be shown
     * @param exception
     *         exception that happened
     * @param notification
     *         notification that needs to be updated with some message
     */
    void showError(@NotNull Runner runner, @NotNull String message, @Nullable Throwable exception, @NotNull Notification notification);

    /**
     * Checks user permissions for running project.
     *
     * @return true if current project is not null and user has permission for run project
     */
    boolean hasRunPermission();
}