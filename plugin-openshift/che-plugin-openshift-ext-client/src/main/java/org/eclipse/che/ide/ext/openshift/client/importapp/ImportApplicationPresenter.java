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

import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.api.workspace.shared.dto.SourceStorageDto;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.event.ConfigureProjectEvent;
import org.eclipse.che.ide.api.event.project.CreateProjectEvent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.wizard.ImportProjectNotificationSubscriber;
import org.eclipse.che.ide.api.wizard.Wizard;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.client.ValidateAuthenticationPresenter;
import org.eclipse.che.ide.ext.openshift.client.oauth.OpenshiftAuthenticator;
import org.eclipse.che.ide.ext.openshift.client.oauth.OpenshiftAuthorizationHandler;
import org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.NameUtils;
import org.eclipse.che.ide.websocket.rest.RequestCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;


/**
 * Presenter, which handles logic for importing OpenShift application to Codenvy.
 *
 * @author Anna Shumilova
 */
public class ImportApplicationPresenter extends ValidateAuthenticationPresenter implements ImportApplicationView.ActionDelegate {

    private final ImportApplicationView               view;
    private final OpenshiftServiceClient              openShiftClient;
    private final ProjectServiceClient                projectServiceClient;
    private final DtoFactory                          dtoFactory;
    private final Map<String, List<BuildConfig>>      buildConfigMap;
    private       BuildConfig                         selectedBuildConfig;
    private       List<String>                        cheProjects;
    private final ImportProjectNotificationSubscriber importProjectNotificationSubscriber;
    private final DtoUnmarshallerFactory              dtoUnmarshallerFactory;
    private final EventBus                            eventBus;
    private final OpenshiftLocalizationConstant       locale;


    @Inject
    public ImportApplicationPresenter(OpenshiftLocalizationConstant locale, ImportApplicationView view,
                                      OpenshiftServiceClient openShiftClient,
                                      ProjectServiceClient projectServiceClient,
                                      DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                      NotificationManager notificationManager,
                                      OpenshiftAuthenticator openshiftAuthenticator,
                                      OpenshiftAuthorizationHandler openshiftAuthorizationHandler,
                                      ImportProjectNotificationSubscriber importProjectNotificationSubscriber,
                                      EventBus eventBus,
                                      DtoFactory dtoFactory) {
        super(openshiftAuthenticator, openshiftAuthorizationHandler, locale, notificationManager);
        this.view = view;
        this.view.setDelegate(this);

        this.openShiftClient = openShiftClient;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.importProjectNotificationSubscriber = importProjectNotificationSubscriber;
        this.eventBus = eventBus;
        this.locale = locale;
        buildConfigMap = new HashMap<>();
        cheProjects = new ArrayList<>();
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
        final ProjectConfigDto projectConfig = dtoFactory.createDto(ProjectConfigDto.class)
                                                         .withSource(dtoFactory.createDto(SourceStorageDto.class));
        Map<String, String> importOptions = new HashMap<String, String>();
        String branch = selectedBuildConfig.getSpec().getSource().getGit().getRef();
        if (branch != null && !branch.isEmpty()) {
            importOptions.put("branch", selectedBuildConfig.getSpec().getSource().getGit().getRef());
        }

        String contextDir = selectedBuildConfig.getSpec().getSource().getContextDir();
        if (contextDir != null && !contextDir.isEmpty()) {
            importOptions.put("keepDirectory", contextDir);
        }

        Map<String, List<String>> attributes = new HashMap<String, List<String>>();
        attributes.put(OpenshiftProjectTypeConstants.OPENSHIFT_APPLICATION_VARIABLE_NAME, Arrays.asList(
                selectedBuildConfig.getMetadata().getName()));

        attributes.put(OpenshiftProjectTypeConstants.OPENSHIFT_NAMESPACE_VARIABLE_NAME, Arrays.asList(
                selectedBuildConfig.getMetadata().getNamespace()));

        projectConfig.withMixins(Arrays.asList(OpenshiftProjectTypeConstants.OPENSHIFT_PROJECT_TYPE_ID))
                     .withAttributes(attributes);

        projectConfig.getSource().withType("git").withParameters(importOptions)
                     .withLocation(selectedBuildConfig.getSpec().getSource().getGit().getUri());

        projectConfig.withType("blank").withDescription(view.getProjectDescription());

        importProjectNotificationSubscriber.subscribe(view.getProjecName());


        projectServiceClient.importProject(view.getProjecName(), false, projectConfig.getSource(), new RequestCallback<Void>(
                dtoUnmarshallerFactory.newWSUnmarshaller(Void.class)) {
            @Override
            protected void onSuccess(final Void result) {
                createProject(new Wizard.CompleteCallback() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onFailure(Throwable e) {

                    }
                }, projectConfig);
            }

            @Override
            protected void onFailure(Throwable exception) {
                view.setErrorMessage(exception.getMessage());
                importProjectNotificationSubscriber.onFailure(exception.getMessage());
                //TODO
            }
        });
    }

    private void createProject(final Wizard.CompleteCallback callback, ProjectConfigDto projectConfig) {
        final String projectName = projectConfig.getName();
        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.updateProject(projectName, projectConfig, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                eventBus.fireEvent(new CreateProjectEvent(result));
                if (!result.getProblems().isEmpty()) {
                    eventBus.fireEvent(new ConfigureProjectEvent(result));
                }
                importProjectNotificationSubscriber.onSuccess();
                callback.onCompleted();
            }

            @Override
            protected void onFailure(Throwable exception) {
                view.setErrorMessage(exception.getMessage());
                importProjectNotificationSubscriber.onFailure(exception.getMessage());
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
        view.enableImportButton(selectedBuildConfig != null & isCheProjectNameValid(view.getProjecName()));
    }

    private boolean isCheProjectNameValid(String projectName) {
        if (cheProjects.contains(projectName)) {
            view.showCheProjectNameError(locale.existingProjectNameError());
            return false;
        }
        if (!NameUtils.checkProjectName(projectName)) {
            view.showCheProjectNameError(locale.invalidCheProjectNameError());
            return false;
        }
        view.hideCheProjectNameError();
        return true;
    }

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

    private void loadCheProjects() {
        projectServiceClient.getProjects(false).then(new Operation<List<ProjectDescriptor>>() {
            @Override
            public void apply(List<ProjectDescriptor> result) throws OperationException {
                cheProjects.clear();
                for (ProjectDescriptor project : result) {
                    cheProjects.add(project.getName());
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
        loadCheProjects();
    }
}
