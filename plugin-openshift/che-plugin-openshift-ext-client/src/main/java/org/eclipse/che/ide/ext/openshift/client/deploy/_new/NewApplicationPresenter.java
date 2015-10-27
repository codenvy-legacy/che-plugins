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
package org.eclipse.che.ide.ext.openshift.client.deploy._new;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.git.gwt.client.GitServiceClient;
import org.eclipse.che.api.git.shared.Remote;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper.RequestCall;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.shared.dto.ImageStream;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper.createFromAsyncRequest;
import static org.eclipse.che.api.promises.client.callback.PromiseHelper.newCallback;

/**
 * @author Vlad Zhukovskiy
 */
@Singleton
public class NewApplicationPresenter implements NewApplicationView.ActionDelegate {
    private final NewApplicationView            view;
    private final AppContext                    appContext;
    private final DialogFactory                 dialogFactory;
    private final OpenshiftLocalizationConstant locale;
    private final GitServiceClient              gitService;
    private final DtoUnmarshallerFactory        dtoUnmarshaller;
    private final OpenshiftServiceClient        openshiftService;

    private List<Remote>      cachedRemotes;
    private List<Project>     cachedProjects;
    private List<ImageStream> imageStreams;

    @Inject
    public NewApplicationPresenter(final NewApplicationView view,
                                   final AppContext appContext,
                                   final DialogFactory dialogFactory,
                                   final OpenshiftLocalizationConstant locale,
                                   final GitServiceClient gitService,
                                   final DtoUnmarshallerFactory dtoUnmarshaller,
                                   final OpenshiftServiceClient openshiftService) {
        this.view = view;
        this.appContext = appContext;
        this.dialogFactory = dialogFactory;
        this.locale = locale;
        this.gitService = gitService;
        this.dtoUnmarshaller = dtoUnmarshaller;
        this.openshiftService = openshiftService;
        view.setDelegate(this);
    }

    public void show() {

        if (appContext.getCurrentProject() == null) {
            return;
        }

        ProjectDescriptor descriptor = appContext.getCurrentProject().getProjectDescription();

        if (!hasGitVcsProvider(descriptor)) {
            dialogFactory.createMessageDialog("", locale.notGitRepositoryWarning(descriptor.getName()), null).show();
            return;
        }

        getGitRemoteRepositories(descriptor)
                .thenPromise(processRemotes())
                .thenPromise(loadProjects())
                .thenPromise(processProjects())
                .then(loadImageStreams());


        view.show();
        view.setApplicationName(appContext.getCurrentProject().getProjectDescription().getName());
    }

    @Override
    public void onCancelClicked() {
        view.hide();
    }

    @Override
    public void onDeployClicked() {

    }

    @Override
    public void onProjectNameChanged() {

    }

    @Override
    public void onDisplayNameChanged() {

    }

    @Override
    public void onDescriptionChanged() {

    }

    @Override
    public void onApplicationNameChanged() {

    }

    private boolean hasGitVcsProvider(ProjectDescriptor descriptor) {
        List<String> listVcsProvider = descriptor.getAttributes().get("vcs.provider.name");
        return listVcsProvider != null && !listVcsProvider.isEmpty() && listVcsProvider.contains("git");
    }

    private Promise<List<Remote>> getGitRemoteRepositories(ProjectDescriptor descriptor) {
        return createFromAsyncRequest(getGitRepositoriesRC(descriptor));
    }

    private RequestCall<List<Remote>> getGitRepositoriesRC(final ProjectDescriptor descriptor) {
        return new RequestCall<List<Remote>>() {
            @Override
            public void makeCall(AsyncCallback<List<Remote>> callback) {
                gitService.remoteList(descriptor, null, true, newCallback(callback, dtoUnmarshaller.newListUnmarshaller(Remote.class)));
            }
        };
    }

    private Function<List<Remote>, Promise<Void>> processRemotes() {
        return new Function<List<Remote>, Promise<Void>>() {
            @Override
            public Promise<Void> apply(List<Remote> remotes) throws FunctionException {
                if (remotes == null || remotes.isEmpty()) {
                    throw new FunctionException("Remotes not provided.");
                }

                cachedRemotes = remotes;

                return Promises.resolve(null);
            }
        };
    }

    private Function<Void, Promise<List<Project>>> loadProjects() {
        return new Function<Void, Promise<List<Project>>>() {
            @Override
            public Promise<List<Project>> apply(Void arg) throws FunctionException {
                return openshiftService.getProjects();
            }
        };
    }

    private Function<List<Project>, Promise<Void>> processProjects() {
        return new Function<List<Project>, Promise<Void>>() {
            @Override
            public Promise<Void> apply(List<Project> projects) throws FunctionException {
                if (projects == null || projects.isEmpty()) {
                    return Promises.resolve(null);
                }

                cachedProjects = projects;

                List<String> projectNames = new ArrayList<>(projects.size());
                for (Project project : projects) {
                    projectNames.add(project.getMetadata().getName());
                }

                Collections.sort(projectNames);
                view.setProjectList(projectNames);

                return Promises.resolve(null);
            }
        };
    }

    private Promise<List<ImageStream>> loadImageStreams() {
        return createFromAsyncRequest(loadImageStreamsRC());
    }

    private RequestCall<List<ImageStream>> loadImageStreamsRC() {
        return new RequestCall<List<ImageStream>>() {
            @Override
            public void makeCall(AsyncCallback<List<ImageStream>> callback) {
            }
        };
    }
}
