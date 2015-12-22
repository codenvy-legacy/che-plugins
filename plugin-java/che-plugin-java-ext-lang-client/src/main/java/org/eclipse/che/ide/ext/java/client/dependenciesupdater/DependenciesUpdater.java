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
package org.eclipse.che.ide.ext.java.client.dependenciesupdater;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.event.project.OpenProjectEvent;
import org.eclipse.che.ide.api.event.project.OpenProjectHandler;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.event.DependencyUpdatedEvent;
import org.eclipse.che.ide.ext.java.client.project.node.jar.ExternalLibrariesNode;
import org.eclipse.che.ide.ext.java.shared.dto.ClassPathBuilderResult;
import org.eclipse.che.ide.extension.machine.client.outputspanel.OutputsContainerPresenter;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.CommandConsoleFactory;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.DefaultOutputConsole;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.OutputConsole;
import org.eclipse.che.ide.part.explorer.project.ProjectExplorerPresenter;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.rest.RequestCallback;
import org.eclipse.che.ide.websocket.rest.Unmarshallable;

import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.PROGRESS;
import static org.eclipse.che.ide.ext.java.shared.dto.ClassPathBuilderResult.Status.SUCCESS;

/**
 * Updates dependencies for Maven project.
 *
 * @author Artem Zatsarynnyi
 * @author Vladyslav Zhukovskii
 */
@Singleton
public class DependenciesUpdater {
    private final NotificationManager        notificationManager;
    private final AppContext                 appContext;
    private final CommandConsoleFactory      commandConsoleFactory;
    private final OutputsContainerPresenter  outputsContainerPresenter;
    private final DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private final JavaClasspathServiceClient classpathServiceClient;
    private final JavaLocalizationConstant   javaLocalizationConstant;

    private       boolean                  updating;
    private       StatusNotification       notification;
    private       EventBus                 eventBus;
    private final ProjectExplorerPresenter projectExplorer;

    @Inject
    public DependenciesUpdater(JavaLocalizationConstant javaLocalizationConstant,
                               NotificationManager notificationManager,
                               AppContext appContext,
                               CommandConsoleFactory commandConsoleFactory,
                               OutputsContainerPresenter outputsContainerPresenter,
                               DtoUnmarshallerFactory dtoUnmarshallerFactory,
                               JavaClasspathServiceClient classpathServiceClient,
                               EventBus eventBus,
                               ProjectExplorerPresenter projectExplorer) {
        this.javaLocalizationConstant = javaLocalizationConstant;
        this.notificationManager = notificationManager;
        this.appContext = appContext;
        this.commandConsoleFactory = commandConsoleFactory;
        this.outputsContainerPresenter = outputsContainerPresenter;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.classpathServiceClient = classpathServiceClient;
        this.eventBus = eventBus;
        this.projectExplorer = projectExplorer;

        updating = false;

        eventBus.addHandler(OpenProjectEvent.TYPE, new OpenProjectHandler() {
            @Override
            public void onProjectOpened(OpenProjectEvent event) {
                updateDependencies(event.getProjectConfig());
            }
        });
    }

    public void updateDependencies(final ProjectConfigDto project) {
        if (updating) {
            return;
        }

        if (appContext.getCurrentProject() == null) {
            return;
        }

        updating = true;
        notification = notificationManager
                .notify(javaLocalizationConstant.updatingDependencies(project.getName()), PROGRESS, false, project);

        Unmarshallable<ClassPathBuilderResult> unmarshaller = dtoUnmarshallerFactory.newWSUnmarshaller(ClassPathBuilderResult.class);

        classpathServiceClient.updateDependencies(project.getPath(), new RequestCallback<ClassPathBuilderResult>(unmarshaller) {
            @Override
            protected void onSuccess(ClassPathBuilderResult result) {
                if (SUCCESS.equals(result.getStatus())) {
                    onUpdated();
                } else {
                    updateFinishedWithError(javaLocalizationConstant.updateDependenciesFailed(), notification);

                    OutputConsole console = commandConsoleFactory.create(javaLocalizationConstant.updateDependenciesTabTitle());
                    outputsContainerPresenter.addConsole(console);
                    ((DefaultOutputConsole)console).printText(result.getLogs());
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.warn(DependenciesUpdater.class, "Failed to launch update dependency process for " + project.getPath());
                updateFinishedWithError(exception.getMessage(), notification);
            }
        });
    }

    private void onUpdated() {
        updating = false;
        notification.setContent(javaLocalizationConstant.dependenciesSuccessfullyUpdated());
        notification.setStatus(StatusNotification.Status.SUCCESS);
        projectExplorer.reloadChildrenByType(ExternalLibrariesNode.class);
        eventBus.fireEvent(new DependencyUpdatedEvent());
    }

    private void updateFinishedWithError(java.lang.String message, StatusNotification notification) {
        updating = false;
        notification.setBalloon(true);
        notification.setContent(message);
        notification.setStatus(FAIL);
    }
}
