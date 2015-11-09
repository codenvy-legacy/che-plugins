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
package org.eclipse.che.ide.ext.openshift.client.project.wizard.page.configure;

import com.google.common.base.Strings;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ImportProject;
import org.eclipse.che.api.project.shared.dto.ImportSourceDescriptor;
import org.eclipse.che.api.project.shared.dto.NewProject;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.project.shared.dto.Source;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.wizard.AbstractWizardPage;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.client.dto.NewApplicationRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.ObjectMeta;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.ext.openshift.shared.dto.ProjectRequest;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.util.NameUtils;

import java.util.Collections;
import java.util.List;

import static org.eclipse.che.api.promises.client.callback.PromiseHelper.newCallback;
import static org.eclipse.che.api.promises.client.callback.PromiseHelper.newPromise;

/**
 * Presenter for configuring OpenShift project.
 *
 * @author Vlad Zhukovskiy
 */
@Singleton
public class ConfigureProjectPresenter extends AbstractWizardPage<NewApplicationRequest> implements ConfigureProjectView.ActionDelegate {

    private final ConfigureProjectView    view;
    private final OpenshiftServiceClient  openShiftClient;
    private final ProjectServiceClient    projectServiceClient;
    private final DtoUnmarshallerFactory  dtoUnmarshaller;
    private final DtoFactory              dtoFactory;
    private       List<Project>           cachedOpenShiftProjects;
    private       List<ProjectDescriptor> cachedCodenvyProjects;

    private AbstractProjectNameValidator<Project>           openShiftProjectNameValidator;
    private AbstractProjectNameValidator<ProjectDescriptor> codenvyProjectNameValidator;

    @Inject
    public ConfigureProjectPresenter(ConfigureProjectView view,
                                     OpenshiftServiceClient openShiftClient,
                                     ProjectServiceClient projectServiceClient,
                                     DtoUnmarshallerFactory dtoUnmarshaller,
                                     DtoFactory dtoFactory) {
        this.view = view;
        this.openShiftClient = openShiftClient;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshaller = dtoUnmarshaller;
        this.dtoFactory = dtoFactory;

        view.setDelegate(this);

        openShiftProjectNameValidator = new OpenShiftProjectNameValidator();
        codenvyProjectNameValidator = new CodenvyProjectNameValidator();
    }

    /** {@inheritDoc} */
    @Override
    public void init(NewApplicationRequest dataObject) {
        super.init(dataObject);

        view.resetControls();

        openShiftClient.getProjects()
                       .then(processOpenShiftProjects())
                       .then(getCodenvyProjects())
                       .thenPromise(processCodenvyProjects());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        if (view.isNewOpenShiftProjectSelected()) {
            final String osNewProjectName = view.getOpenShiftNewProjectName();
            boolean b1 = openShiftProjectNameValidator.isValid(osNewProjectName,
                                                               cachedOpenShiftProjects == null ? Collections.<Project>emptyList()
                                                                                               : cachedOpenShiftProjects);

            final String cdNewProjectName = view.getCodenvyNewProjectName();
            boolean b2 = NameUtils.checkProjectName(cdNewProjectName)
                         && codenvyProjectNameValidator.isValid(cdNewProjectName,
                                                                cachedCodenvyProjects == null ? Collections.<ProjectDescriptor>emptyList()
                                                                                              : cachedCodenvyProjects);

            return b1 && b2;
        } else {
            final String cdNewProjectName = view.getCodenvyNewProjectName();
            boolean b2 = NameUtils.checkProjectName(cdNewProjectName)
                         && codenvyProjectNameValidator.isValid(cdNewProjectName,
                                                                cachedCodenvyProjects == null ? Collections.<ProjectDescriptor>emptyList()
                                                                                              : cachedCodenvyProjects);

            return view.getExistedSelectedProject() != null && b2;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenShiftNewProjectNameChanged() {
        setUpNewProjectRequest();
        view.showOpenShiftNewProjectNameInvalidValueMessage(!isOpenShiftProjectNameValid(view.getOpenShiftNewProjectName()));
        updateDelegate.updateControls();
    }

    private boolean isOpenShiftProjectNameValid(String name) {
        return name.length() < 63 && name.matches("[a-z0-9]([-a-z0-9]*[a-z0-9])?");
    }

    /** {@inheritDoc} */
    @Override
    public void onCodenvyNewProjectNameChanged() {
        setUpCodenvyProjectRequest();
        view.showCodenvyNewProjectNameInvalidValueMessage(!isCodenvyProjectNameValid(view.getCodenvyNewProjectName()));
        updateDelegate.updateControls();
    }

    private boolean isCodenvyProjectNameValid(String name) {
        return NameUtils.checkProjectName(name);
    }

    private Promise<List<ProjectDescriptor>> getCodenvyProjects() {
        return newPromise(new AsyncPromiseHelper.RequestCall<List<ProjectDescriptor>>() {
            @Override
            public void makeCall(AsyncCallback<List<ProjectDescriptor>> callback) {
                projectServiceClient
                        .getProjects(false, newCallback(callback, dtoUnmarshaller.newListUnmarshaller(ProjectDescriptor.class)));
            }
        });
    }

    private Function<List<ProjectDescriptor>, Promise<List<ProjectDescriptor>>> processCodenvyProjects() {
        return new Function<List<ProjectDescriptor>, Promise<List<ProjectDescriptor>>>() {
            @Override
            public Promise<List<ProjectDescriptor>> apply(List<ProjectDescriptor> projects) throws FunctionException {
                cachedCodenvyProjects = projects;
                return Promises.resolve(projects);
            }
        };
    }

    private Operation<List<Project>> processOpenShiftProjects() {
        return new Operation<List<Project>>() {
            @Override
            public void apply(List<Project> projects) throws OperationException {
                cachedOpenShiftProjects = projects;
                view.setExistOpenShiftProjects(projects);
            }
        };
    }

    protected abstract class AbstractProjectNameValidator<T> {

        abstract String getProjectName(T project);

        public boolean isValid(String proposedName, List<T> existedProjects) {
            if (Strings.isNullOrEmpty(proposedName)) {
                return false;
            }

            for (T project : existedProjects) {
                final String projectName = getProjectName(project);
                if (Strings.isNullOrEmpty(projectName)) {
                    continue;
                }

                if (projectName.equals(proposedName)) {
                    return false;
                }
            }

            return true;
        }
    }

    protected class OpenShiftProjectNameValidator extends AbstractProjectNameValidator<Project> {
        @Override
        String getProjectName(Project project) {
            return project.getMetadata().getName();
        }
    }

    protected class CodenvyProjectNameValidator extends AbstractProjectNameValidator<ProjectDescriptor> {
        @Override
        String getProjectName(ProjectDescriptor project) {
            return project.getName();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onExistProjectSelected() {
        setUpExistProjectRequest();
        updateDelegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenShiftDescriptionChanged() {
        setUpNewProjectRequest();
    }

    /** {@inheritDoc} */
    @Override
    public void onCodenvyDescriptionChanged() {
        setUpCodenvyProjectRequest();
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenShiftDisplayNameChanged() {
        setUpNewProjectRequest();
    }

    /** {@inheritDoc} */
    @Override
    public void onCodenvyProjectPrivacyChanged() {
        setUpCodenvyProjectRequest();
    }

    private void setUpNewProjectRequest() {
        ProjectRequest projectRequest;

        if (dataObject.getProjectRequest() == null) {
            dataObject.withProjectRequest(dtoFactory.createDto(ProjectRequest.class));
        }

        projectRequest = dataObject.getProjectRequest();
        projectRequest.withApiVersion("v1")
                      .withDescription(view.getOpenShiftProjectDescription())
                      .withDisplayName(view.getOpenShiftProjectDisplayName())
                      .withMetadata(dtoFactory.createDto(ObjectMeta.class).withName(view.getOpenShiftNewProjectName()));

        if (dataObject.getProject() != null) {
            dataObject.setProject(null);
        }
    }

    private void setUpExistProjectRequest() {
        dataObject.setProject(view.getExistedSelectedProject());

        if (dataObject.getProjectRequest() != null) {
            dataObject.setProjectRequest(null);
        }
    }

    private void setUpCodenvyProjectRequest() {
        ImportProject importProject;

        if (dataObject.getImportProject() == null) {
            ImportProject dtoImportProject = dtoFactory.createDto(ImportProject.class);
            dtoImportProject.withProject(dtoFactory.createDto(NewProject.class))
                            .withSource(dtoFactory.createDto(Source.class).withProject(dtoFactory.createDto(ImportSourceDescriptor.class)));

            dataObject.withImportProject(dtoImportProject);
        }

        importProject = dataObject.getImportProject();
        importProject.getProject()
                     .withName(view.getCodenvyNewProjectName())
                     .withDescription(view.getCodenvyProjectDescription())
                     .withVisibility(view.isCodenvyPublicProject() ? "public" : "private");
    }
}
