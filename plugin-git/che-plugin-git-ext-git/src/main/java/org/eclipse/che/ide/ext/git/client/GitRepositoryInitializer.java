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
package org.eclipse.che.ide.ext.git.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.eclipse.che.api.git.gwt.client.GitServiceClient;
import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.StringUnmarshaller;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.RequestCallback;

import javax.inject.Inject;
import java.util.List;

/**
 * Class for init repository
 *
 * @author Sergii Leschenko
 */
public class GitRepositoryInitializer {
    private final GitServiceClient        gitService;
    private final GitLocalizationConstant gitLocale;
    private final AppContext              appContext;
    private final NotificationManager     notificationManager;
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private final ProjectServiceClient    projectServiceClient;

    @Inject
    public GitRepositoryInitializer(GitServiceClient gitService,
                                    GitLocalizationConstant gitLocale,
                                    AppContext appContext,
                                    NotificationManager notificationManager,
                                    DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                    ProjectServiceClient projectServiceClient) {
        this.gitService = gitService;
        this.gitLocale = gitLocale;
        this.appContext = appContext;
        this.notificationManager = notificationManager;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectServiceClient = projectServiceClient;
    }

    public static boolean isGitRepository(ProjectConfigDto project) {
        List<String> listVcsProvider = project.getAttributes().get("vcs.provider.name");

        return listVcsProvider != null
               && !listVcsProvider.isEmpty()
               && listVcsProvider.contains("git");
    }

    /**
     * Initializes GIT repository.
     */
    public void initGitRepository(final ProjectConfigDto project, final AsyncCallback<Void> callback) {
        try {
            gitService.init(project, false, new RequestCallback<Void>() {
                                @Override
                                protected void onSuccess(Void result) {
                                    updateGitProvider(project, new AsyncCallback<ProjectConfigDto>() {
                                        @Override
                                        public void onFailure(Throwable caught) {
                                            callback.onFailure(caught);
                                        }

                                        @Override
                                        public void onSuccess(ProjectConfigDto result) {
                                            callback.onSuccess(null);
                                        }
                                    });
                                }

                                @Override
                                protected void onFailure(Throwable exception) {
                                    callback.onFailure(exception);
                                }
                            }
                           );
        } catch (WebSocketException e) {
            callback.onFailure(e);
        }
    }

    /**
     * Returns git url using callback. If the project has no repository then method initializes it.
     */
    public void getGitUrlWithAutoInit(final ProjectConfigDto project, final AsyncCallback<String> callback) {
        if (!GitRepositoryInitializer.isGitRepository(project)) {
            initGitRepository(project, new AsyncCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    notificationManager.notify(gitLocale.initSuccess(), project);
                    getGitUrlWithAutoInit(appContext.getCurrentProject().getRootProject(), callback);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });

            return;
        }

        gitService.getGitReadOnlyUrl(project,
                                     new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                         @Override
                                         protected void onSuccess(String result) {
                                             callback.onSuccess(result);
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             callback.onFailure(exception);
                                         }
                                     });
    }

    /**
     * Update information about vcs provider name of current project in application context.
     */
    void updateGitProvider(final ProjectConfigDto project, final AsyncCallback<ProjectConfigDto> callback) {
        // update 'vcs.provider.name' attribute value
        projectServiceClient.getProject(project.getPath(),
                                        new AsyncRequestCallback<ProjectConfigDto>(
                                                dtoUnmarshallerFactory.newUnmarshaller(ProjectConfigDto.class)) {
                                            @Override
                                            protected void onSuccess(ProjectConfigDto projectConfig) {
                                                appContext.getCurrentProject().setRootProject(projectConfig);
                                                callback.onSuccess(projectConfig);
                                            }

                                            @Override
                                            protected void onFailure(Throwable throwable) {
                                                callback.onFailure(throwable);
                                            }
                                        });
    }
}
