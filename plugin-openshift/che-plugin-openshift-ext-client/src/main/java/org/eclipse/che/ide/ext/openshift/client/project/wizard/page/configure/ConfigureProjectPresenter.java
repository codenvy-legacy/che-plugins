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
import org.eclipse.che.api.project.shared.dto.ProjectReference;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.wizard.AbstractWizardPage;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.client.dto.NewApplicationRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;

import java.util.Collections;
import java.util.List;

import static org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper.createFromAsyncRequest;

/**
 * Presenter for configuring OpenShift project.
 *
 * @author Vlad Zhukovskiy
 */
@Singleton
public class ConfigureProjectPresenter extends AbstractWizardPage<NewApplicationRequest> implements ConfigureProjectView.ActionDelegate {

    private final ConfigureProjectView   view;
    private final OpenshiftServiceClient openShiftClient;
    private final ProjectServiceClient   projectServiceClient;
    private final DtoUnmarshallerFactory dtoUnmarshaller;
    private       List<Project>          cachedOpenShiftProjects;
    private       List<ProjectReference> cachedCodenvyProjects;

    private AbstractProjectNameValidator<Project>          openShiftProjectNameValidator;
    private AbstractProjectNameValidator<ProjectReference> codenvyProjectNameValidator;

    @Inject
    public ConfigureProjectPresenter(ConfigureProjectView view,
                                     OpenshiftServiceClient openShiftClient,
                                     ProjectServiceClient projectServiceClient,
                                     DtoUnmarshallerFactory dtoUnmarshaller) {
        this.view = view;
        this.openShiftClient = openShiftClient;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshaller = dtoUnmarshaller;

        view.setDelegate(this);

        openShiftProjectNameValidator = new OpenShiftProjectNameValidator();
        codenvyProjectNameValidator = new CodenvyProjectNameValidator();
    }

    /** {@inheritDoc} */
    @Override
    public void init(NewApplicationRequest dataObject) {
        super.init(dataObject);

        view.resetControls();

        getOpenShiftProjects().thenPromise(cacheOpenShiftProjects())
                              .thenPromise(showOpenShiftProjects());

        getCodenvyProjects().thenPromise(cacheCodenvyProjects());
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
            boolean b2 = codenvyProjectNameValidator.isValid(cdNewProjectName,
                                                             cachedCodenvyProjects == null ? Collections.<ProjectReference>emptyList()
                                                                                           : cachedCodenvyProjects);

            return b1 && b2;
        } else {
            final String cdNewProjectName = view.getCodenvyNewProjectName();
            boolean b2 = codenvyProjectNameValidator.isValid(cdNewProjectName,
                                                             cachedCodenvyProjects == null ? Collections.<ProjectReference>emptyList()
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
        updateDelegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void onCodenvyNewProjectNameChanged() {
        updateDelegate.updateControls();
    }

    private Promise<List<ProjectReference>> getCodenvyProjects() {
        return createFromAsyncRequest(getCodenvyProjectsRC());
    }

    private AsyncPromiseHelper.RequestCall<List<ProjectReference>> getCodenvyProjectsRC() {
        return new AsyncPromiseHelper.RequestCall<List<ProjectReference>>() {
            @Override
            public void makeCall(AsyncCallback<List<ProjectReference>> callback) {
                projectServiceClient.getProjects(_callback(callback, dtoUnmarshaller.newListUnmarshaller(ProjectReference.class)));
            }
        };
    }

    private Promise<List<Project>> getOpenShiftProjects() {
        return createFromAsyncRequest(getOpenShiftProjectsRC());
    }

    private AsyncPromiseHelper.RequestCall<List<Project>> getOpenShiftProjectsRC() {
        return new AsyncPromiseHelper.RequestCall<List<Project>>() {
            @Override
            public void makeCall(AsyncCallback<List<Project>> callback) {
                openShiftClient.getProjects(_callback(callback, dtoUnmarshaller.newListUnmarshaller(Project.class)));
            }
        };
    }

    protected <T> AsyncRequestCallback<T> _callback(final AsyncCallback<T> callback, Unmarshallable<T> u) {
        return new AsyncRequestCallback<T>(u) {
            @Override
            protected void onSuccess(T result) {
                callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        };
    }

    private Function<List<Project>, Promise<Void>> showOpenShiftProjects() {
        return new Function<List<Project>, Promise<Void>>() {
            @Override
            public Promise<Void> apply(List<Project> projects) throws FunctionException {
                view.setExistOpenShiftProjects(projects);
                return Promises.resolve(null);
            }
        };
    }

    private Function<List<ProjectReference>, Promise<List<ProjectReference>>> cacheCodenvyProjects() {
        return new Function<List<ProjectReference>, Promise<List<ProjectReference>>>() {
            @Override
            public Promise<List<ProjectReference>> apply(List<ProjectReference> projects) throws FunctionException {
                cachedCodenvyProjects = projects;
                return Promises.resolve(projects);
            }
        };
    }

    private Function<List<Project>, Promise<List<Project>>> cacheOpenShiftProjects() {
        return new Function<List<Project>, Promise<List<Project>>>() {
            @Override
            public Promise<List<Project>> apply(List<Project> projects) throws FunctionException {
                cachedOpenShiftProjects = projects;
                return Promises.resolve(projects);
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

    protected class CodenvyProjectNameValidator extends AbstractProjectNameValidator<ProjectReference> {
        @Override
        String getProjectName(ProjectReference project) {
            return project.getName();
        }
    }

    @Override
    public void onExistProjectSelected() {
        dataObject.setProject(view.getExistedSelectedProject());
        updateDelegate.updateControls();
    }
}
