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
package org.eclipse.che.ide.ext.runner.client.inject.factories;

import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.CheckRamAndRunAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.GetLogsAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.GetRunningProcessesAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.StopAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.LaunchAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions.CheckHealthStatusAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions.OutputAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions.StatusAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.RunAction;

import javax.validation.constraints.NotNull;

/**
 * The factory for creating sub-actions for Launch action.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public interface RunnerActionFactory {

    /**
     * Create an instance of {@link org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions.StatusAction} with a given notification for updating status of process.
     *
     * @param notification
     *         notification that has to show status of process
     * @return an instance of {@link org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions.StatusAction}
     */
    @NotNull
    StatusAction createStatus(@NotNull Notification notification);

    /**
     * Create an instance of {@link org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions.CheckHealthStatusAction} with a given notification for updating status of process.
     *
     * @param notification
     *         notification that has to show status of process
     * @return an instance of {@link org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions.CheckHealthStatusAction}
     */
    @NotNull
    CheckHealthStatusAction createCheckHealthStatus(@NotNull Notification notification);

    /** @return an instance of {@link org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions.OutputAction} */
    @NotNull
    OutputAction createOutput();

    /** @return an instance of {@link org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.LaunchAction} */
    @NotNull
    LaunchAction createLaunch();

    /** @return an instance of {@link org.eclipse.che.ide.ext.runner.client.runneractions.impl.CheckRamAndRunAction} */
    @NotNull
    CheckRamAndRunAction createCheckRamAndRun();

    /** @return an instance of {@link org.eclipse.che.ide.ext.runner.client.runneractions.impl.GetLogsAction} */
    @NotNull
    GetLogsAction createGetLogs();

    /** @return an instance of {@link org.eclipse.che.ide.ext.runner.client.runneractions.impl.GetRunningProcessesAction} */
    @NotNull
    GetRunningProcessesAction createGetRunningProcess();

    /** @return an instance of {@link RunAction} */
    @NotNull
    RunAction createRun();

    /** @return an instance of {@link org.eclipse.che.ide.ext.runner.client.runneractions.impl.StopAction} */
    @NotNull
    StopAction createStop();

}