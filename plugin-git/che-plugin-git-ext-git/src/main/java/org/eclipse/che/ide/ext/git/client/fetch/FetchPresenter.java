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
package org.eclipse.che.ide.ext.git.client.fetch;

import org.eclipse.che.api.core.rest.shared.dto.ServiceError;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.ext.git.client.GitLocalizationConstant;
import org.eclipse.che.api.git.gwt.client.GitServiceClient;
import org.eclipse.che.api.git.shared.Branch;
import org.eclipse.che.api.git.shared.Remote;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.git.client.BranchSearcher;
import org.eclipse.che.ide.ext.git.client.GitOutputPartPresenter;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.RequestCallback;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.eclipse.che.api.git.shared.BranchListRequest.LIST_LOCAL;
import static org.eclipse.che.api.git.shared.BranchListRequest.LIST_REMOTE;
import static org.eclipse.che.ide.api.notification.Notification.Status.FINISHED;
import static org.eclipse.che.ide.api.notification.Notification.Status.PROGRESS;
import static org.eclipse.che.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for fetching changes from remote repository.
 *
 * @author Ann Zhuleva
 */
@Singleton
public class FetchPresenter implements FetchView.ActionDelegate {
    private final DtoFactory              dtoFactory;
    private final GitOutputPartPresenter  console;
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private final NotificationManager     notificationManager;
    private final BranchSearcher          branchSearcher;
    private       FetchView               view;
    private       GitServiceClient        service;
    private       AppContext              appContext;
    private       GitLocalizationConstant constant;
    private       CurrentProject          project;

    @Inject
    public FetchPresenter(DtoFactory dtoFactory,
                          FetchView view,
                          GitOutputPartPresenter console,
                          GitServiceClient service,
                          AppContext appContext,
                          GitLocalizationConstant constant,
                          NotificationManager notificationManager,
                          DtoUnmarshallerFactory dtoUnmarshallerFactory,
                          BranchSearcher branchSearcher) {
        this.dtoFactory = dtoFactory;
        this.view = view;
        this.console = console;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.branchSearcher = branchSearcher;
        this.view.setDelegate(this);
        this.service = service;
        this.appContext = appContext;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        project = appContext.getCurrentProject();
        view.setRemoveDeleteRefs(false);
        view.setFetchAllBranches(true);
        updateRemotes();
    }

    /**
     * Update the list of remote repositories for local one. If remote repositories are found, then update the list of branches (remote and
     * local).
     */
    private void updateRemotes() {
        service.remoteList(project.getRootProject(), null, true,
                           new AsyncRequestCallback<List<Remote>>(dtoUnmarshallerFactory.newListUnmarshaller(Remote.class)) {
                               @Override
                               protected void onSuccess(List<Remote> result) {
                                   view.setRepositories(result);
                                   updateBranches(LIST_REMOTE);
                                   view.setEnableFetchButton(!result.isEmpty());
                                   view.showDialog();
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   String errorMessage =
                                           exception.getMessage() != null ? exception.getMessage() : constant.remoteListFailed();
                                   handleError(errorMessage);
                                   view.setEnableFetchButton(false);
                               }
                           });
    }

    private void handleError(@NotNull String errorMessage) {
        console.printError(errorMessage);
        notificationManager.showError(errorMessage);
    }

    /**
     * Update the list of branches.
     *
     * @param remoteMode is a remote mode
     */
    private void updateBranches(@NotNull final String remoteMode) {
        service.branchList(project.getRootProject(), remoteMode,
                           new AsyncRequestCallback<List<Branch>>(dtoUnmarshallerFactory.newListUnmarshaller(Branch.class)) {
                               @Override
                               protected void onSuccess(List<Branch> result) {
                                   if (LIST_REMOTE.equals(remoteMode)) {
                                       view.setRemoteBranches(branchSearcher.getRemoteBranchesToDisplay(view.getRepositoryName(), result));
                                       updateBranches(LIST_LOCAL);
                                   } else {
                                       view.setLocalBranches(branchSearcher.getLocalBranchesToDisplay(result));
                                       for (Branch branch : result) {
                                           if (branch.isActive()) {
                                               view.selectRemoteBranch(branch.getDisplayName());
                                               break;
                                           }
                                       }
                                   }
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   String errorMessage =
                                           exception.getMessage() != null ? exception.getMessage() : constant.branchesListFailed();
                                   console.printError(errorMessage);
                                   notificationManager.showError(errorMessage);
                                   view.setEnableFetchButton(false);
                               }
                           });
    }

    /** {@inheritDoc} */
    @Override
    public void onFetchClicked() {
        final String remoteUrl = view.getRepositoryUrl();
        String remoteName = view.getRepositoryName();
        boolean removeDeletedRefs = view.isRemoveDeletedRefs();

        final Notification notification = new Notification(constant.fetchProcess(), PROGRESS, true);
        notificationManager.showNotification(notification);
        try {
            service.fetch(project.getRootProject(), remoteName, getRefs(), removeDeletedRefs,
                          new RequestCallback<String>() {
                              @Override
                              protected void onSuccess(String result) {
                                  console.printInfo(constant.fetchSuccess(remoteUrl));
                                  notification.setStatus(FINISHED);
                                  notification.setMessage(constant.fetchSuccess(remoteUrl));
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  handleError(exception, remoteUrl, notification);
                              }
                          }
                         );
        } catch (WebSocketException e) {
            handleError(e, remoteUrl, notification);
        }
        view.close();
    }

    /** @return list of refs to fetch */
    @NotNull
    private List<String> getRefs() {
        if (view.isFetchAllBranches()) {
            return new ArrayList<>();
        }

        String localBranch = view.getLocalBranch();
        String remoteBranch = view.getRemoteBranch();
        String remoteName = view.getRepositoryName();
        String refs = localBranch.isEmpty() ? remoteBranch
                                            : "refs/heads/" + localBranch + ":" + "refs/remotes/" + remoteName + "/" + remoteBranch;
        return new ArrayList<>(Arrays.asList(refs));
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param throwable
     *         exception what happened
     */
    private void handleError(@NotNull Throwable throwable, @NotNull String remoteUrl, Notification notification) {
        String errorMessage = throwable.getMessage();
        notification.setType(ERROR);
        if (errorMessage == null) {
            console.printError(constant.fetchFail(remoteUrl));
            notification.setMessage(constant.fetchFail(remoteUrl));
            return;
        }

        try {
            errorMessage = dtoFactory.createDtoFromJson(errorMessage, ServiceError.class).getMessage();
            if (errorMessage.equals("Unable get private ssh key")) {
                console.printError(constant.messagesUnableGetSshKey());
                notification.setMessage(constant.messagesUnableGetSshKey());
                return;
            }
            console.printError(errorMessage);
            notification.setMessage(errorMessage);
        } catch (Exception e) {
            console.printError(errorMessage);
            notification.setMessage(errorMessage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        boolean isFetchAll = view.isFetchAllBranches();
        view.setEnableLocalBranchField(!isFetchAll);
        view.setEnableRemoteBranchField(!isFetchAll);
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoteBranchChanged() {
        view.selectLocalBranch(view.getRemoteBranch());
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoteRepositoryChanged() {
        updateBranches(LIST_REMOTE);
    }
}