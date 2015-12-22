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
package org.eclipse.che.ide.ext.git.client.status;

import org.eclipse.che.ide.ext.git.client.GitLocalizationConstant;
import org.eclipse.che.api.git.gwt.client.GitServiceClient;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.git.client.GitOutputPartPresenter;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.StringUnmarshaller;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import static org.eclipse.che.api.git.shared.StatusFormat.LONG;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;

/**
 * Handler to process actions with displaying the status of the Git work tree.
 *
 * @author Ann Zhuleva
 */
@Singleton
public class StatusCommandPresenter {

    private final GitOutputPartPresenter  console;

    private GitServiceClient        service;
    private AppContext              appContext;
    private GitLocalizationConstant constant;
    private NotificationManager     notificationManager;

    /**
     * Create presenter.
     */
    @Inject
    public StatusCommandPresenter(GitServiceClient service,
                                  AppContext appContext,
                                  GitOutputPartPresenter console,
                                  GitLocalizationConstant constant,
                                  NotificationManager notificationManager) {
        this.service = service;
        this.appContext = appContext;
        this.console = console;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** Show status. */
    public void showStatus() {
        final CurrentProject project = appContext.getCurrentProject();
        if (project == null) {
            return;
        }

        service.statusText(project.getRootProject(), LONG,
                           new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                               @Override
                               protected void onSuccess(String result) {
                                   printGitStatus(result);
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   notificationManager.notify(constant.statusFailed(), FAIL, true, project.getRootProject());
                               }
                           });
    }

    /**
     * Print colored Git status to Output
     *
     * @param statusText text to be printed
     */
    private void printGitStatus(String statusText) {

        console.print("");
        String[] lines = statusText.split("\n");
        for (String line : lines) {

            if (line.startsWith("\tmodified:") || line.startsWith("#\tmodified:")) {
                console.printError(line);
                continue;
            }

            if (line.startsWith("\t") || line.startsWith("#\t")) {
                console.printInfo(line);
                continue;
            }

            console.print(line);
        }
    }

}
