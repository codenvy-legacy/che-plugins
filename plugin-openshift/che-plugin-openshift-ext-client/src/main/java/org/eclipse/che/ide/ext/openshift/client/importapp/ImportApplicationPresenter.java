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
package org.eclipse.che.ide.ext.openshift.client.importapp;

import com.google.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.git.gwt.client.GitServiceClient;
import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.*;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.event.project.CreateProjectEvent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.wizard.ImportProjectNotificationSubscriber;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.client.ValidateAuthenticationPresenter;
import org.eclipse.che.ide.ext.openshift.client.oauth.OpenshiftAuthenticator;
import org.eclipse.che.ide.ext.openshift.client.oauth.OpenshiftAuthorizationHandler;
import org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.websocket.rest.RequestCallback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter, which handles logic for importing OpenShift application to Codenvy.
 *
 * @author Anna Shumilova
 */
public class ImportApplicationPresenter extends ValidateAuthenticationPresenter implements ImportApplicationView.ActionDelegate {

    private final ImportApplicationView               view;
    private final AppContext                          appContext;
    private final OpenshiftLocalizationConstant       locale;
    private final NotificationManager                 notificationManager;
    private final DialogFactory                       dialogFactory;
    private final OpenshiftServiceClient              openShiftClient;
    private final ProjectServiceClient                projectServiceClient;
    private final GitServiceClient                    gitService;
    private final DtoUnmarshallerFactory              dtoUnmarshaller;
    private final DtoFactory                          dtoFactory;
    private final Map<String, List<BuildConfig>>      buildConfigMap;
    private       BuildConfig                         selectedBuildConfig;
    private final ImportProjectNotificationSubscriber importProjectNotificationSubscriber;
    private final DtoUnmarshallerFactory              dtoUnmarshallerFactory;
    private final EventBus                            eventBus;


    @Inject
    public ImportApplicationPresenter(OpenshiftLocalizationConstant locale, ImportApplicationView view,
                                      OpenshiftServiceClient openShiftClient,
                                      ProjectServiceClient projectServiceClient,
                                      DtoUnmarshallerFactory dtoUnmarshaller,
                                      DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                      GitServiceClient gitService,
                                      NotificationManager notificationManager,
                                      DialogFactory dialogFactory,
                                      AppContext appContext,
                                      OpenshiftAuthenticator openshiftAuthenticator,
                                      OpenshiftAuthorizationHandler openshiftAuthorizationHandler,
                                      ImportProjectNotificationSubscriber importProjectNotificationSubscriber,
                                      EventBus eventBus,
                                      DtoFactory dtoFactory) {
        super(openshiftAuthenticator, openshiftAuthorizationHandler, locale, notificationManager);
        this.view = view;
        this.view.setDelegate(this);

        this.appContext = appContext;
        this.locale = locale;
        this.notificationManager = notificationManager;
        this.dialogFactory = dialogFactory;
        this.openShiftClient = openShiftClient;
        this.projectServiceClient = projectServiceClient;
        this.gitService = gitService;
        this.dtoUnmarshaller = dtoUnmarshaller;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.importProjectNotificationSubscriber = importProjectNotificationSubscriber;
        this.eventBus = eventBus;

        buildConfigMap = new HashMap<>();
    }

    /**
     * Prepare the view state to be shown.
     */
    private void prepareView() {
        selectedBuildConfig = null;
        view.setErrorMessage("");
        view.setProjectName("");
        view.setProjectDescription("");
        view.enableImportButton(false);
        view.showView();
    }

    @Override
    public void onImportApplicationClicked() {
        doImport();
    }

    /**
     * Fills project data and imports project.
     */
    private void doImport() {
        ImportProject importProject = dtoFactory.createDto(ImportProject.class)
                                                .withProject(dtoFactory.createDto(NewProject.class))
                                                .withSource(dtoFactory.createDto(Source.class)
                                                                      .withProject(dtoFactory.createDto(ImportSourceDescriptor.class)));
        Map<String, String> importOptions = new HashMap<String, String>();
        String branch = selectedBuildConfig.getSpec().getSource().getGit().getRef();
        if (branch != null && !branch.isEmpty()) {
            importOptions.put("branch", selectedBuildConfig.getSpec().getSource().getGit().getRef());
        }

        String contextDir = selectedBuildConfig.getSpec().getSource().getContextDir();
        if (contextDir != null && !contextDir.isEmpty()) {
            importOptions.put("keepDirectory", contextDir);
            importProject.getProject().withContentRoot(contextDir);
        }

        Map<String, List<String>> attributes = new HashMap<String, List<String>>();
        attributes.put(OpenshiftProjectTypeConstants.OPENSHIFT_APPLICATION_VARIABLE_NAME, Arrays.asList(
                selectedBuildConfig.getMetadata().getName()));

        attributes.put(OpenshiftProjectTypeConstants.OPENSHIFT_NAMESPACE_VARIABLE_NAME, Arrays.asList(
                selectedBuildConfig.getMetadata().getNamespace()));

        importProject.getProject().withMixins(Arrays.asList(OpenshiftProjectTypeConstants.OPENSHIFT_PROJECT_TYPE_ID))
                     .withAttributes(attributes);

        importProject.getSource().getProject().withType("git").withParameters(importOptions)
                     .withLocation(selectedBuildConfig.getSpec().getSource().getGit().getUri());

        importProject.getProject().withType("blank").withDescription(view.getProjectDescription());

        importProjectNotificationSubscriber.subscribe(view.getProjecName());

        projectServiceClient.importProject(view.getProjecName(), false, importProject, new RequestCallback<ImportResponse>(
                dtoUnmarshallerFactory.newWSUnmarshaller(ImportResponse.class)) {
            @Override
            protected void onSuccess(final ImportResponse result) {
                importProjectNotificationSubscriber.onSuccess();
                eventBus.fireEvent(new CreateProjectEvent(result.getProjectDescriptor()));
                view.closeView();
            }

            @Override
            protected void onFailure(Throwable exception) {
                view.setErrorMessage(exception.getMessage());
                importProjectNotificationSubscriber.onFailure(exception.getMessage());
                //TODO
            }
        });
    }

    @Override
    public void onCancelClicked() {
        view.closeView();
    }

    @Override
    public void onBuildConfigSelected(BuildConfig buildConfig) {
        selectedBuildConfig = buildConfig;

        if (buildConfig != null) {
            view.setProjectName(buildConfig.getMetadata().getName());

            view.setApplicationInfo(buildConfig);
        }

        view.enableImportButton((buildConfig != null && view.getProjecName() != null && !view.getProjecName().isEmpty()));
    }

    @Override
    public void onProjectNameChanged(String name) {
        view.enableImportButton((selectedBuildConfig != null && view.getProjecName() != null && !view.getProjecName().isEmpty()));
    }

    /**
     * Load OpenShift Project and Application data.
     */
    private void loadOpenShiftData() {
        openShiftClient.getProjects().then(new Operation<List<Project>>() {
            @Override
            public void apply(List<Project> result) throws OperationException {
                for (Project project : result) {
                    getBuildConfigs(project.getMetadata().getName());
                }
            }
        });
    }

    /**
     * Get OpenShift Build Configs by namespace.
     *
     * @param namespace
     */
    private void getBuildConfigs(final String namespace) {
        openShiftClient.getBuildConfigs(namespace).then(new Operation<List<BuildConfig>>() {
            @Override
            public void apply(List<BuildConfig> result) throws OperationException {
                buildConfigMap.put(namespace, result);
                //TODO update by portions?
                view.setBuildConfigs(buildConfigMap);
            }
        });
    }

    @Override
    protected void onSuccessAuthentication() {
        prepareView();
        loadOpenShiftData();
    }
}
