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
package org.eclipse.che.ide.ext.git.client.delete;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.git.gwt.client.GitServiceClient;
import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.event.project.ProjectUpdatedEvent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.git.client.GitLocalizationConstant;
import org.eclipse.che.ide.ext.git.client.GitOutputPartPresenter;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;

/**
 * Delete repository command handler, performs deleting Git repository.
 *
 * @author Ann Zhuleva
 */
@Singleton
public class DeleteRepositoryPresenter {
    private       GitServiceClient        service;
    private       GitLocalizationConstant constant;
    private       AppContext              appContext;
    private       NotificationManager     notificationManager;
    private final ProjectServiceClient    projectService;
    private final DtoUnmarshallerFactory  dtoUnmarshaller;
    private final EventBus                eventBus;
    private final GitOutputPartPresenter  console;

    /**
     * Create presenter.
     *
     * @param service
     * @param constant
     * @param appContext
     * @param notificationManager
     */
    @Inject
    public DeleteRepositoryPresenter(GitServiceClient service,
                                     GitLocalizationConstant constant,
                                     GitOutputPartPresenter console,
                                     AppContext appContext,
                                     NotificationManager notificationManager,
                                     ProjectServiceClient projectService,
                                     DtoUnmarshallerFactory dtoUnmarshaller,
                                     EventBus eventBus) {
        this.service = service;
        this.constant = constant;
        this.console = console;
        this.appContext = appContext;
        this.notificationManager = notificationManager;
        this.projectService = projectService;
        this.dtoUnmarshaller = dtoUnmarshaller;
        this.eventBus = eventBus;
    }

    /** Delete Git repository. */
    public void deleteRepository() {
        final CurrentProject project = appContext.getCurrentProject();
        service.deleteRepository(project.getRootProject(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                console.printInfo(constant.deleteGitRepositorySuccess());
                notificationManager.notify(constant.deleteGitRepositorySuccess(), project.getRootProject());
                getRootProject(project.getRootProject());
            }

            @Override
            protected void onFailure(Throwable exception) {
                console.printError(exception.getMessage());
                notificationManager.notify(exception.getMessage(), project.getRootProject());
            }
        });
    }

    private void getRootProject(final ProjectConfigDto config) {
        projectService.getProject(config.getPath(),
                                  new AsyncRequestCallback<ProjectConfigDto>(dtoUnmarshaller.newUnmarshaller(ProjectConfigDto.class)) {
                                      @Override
                                      protected void onSuccess(ProjectConfigDto projectConfig) {
                                          eventBus.fireEvent(new ProjectUpdatedEvent(config.getPath(), projectConfig));
                                      }

                                      @Override
                                      protected void onFailure(Throwable exception) {

                                      }
                                  });
    }
}