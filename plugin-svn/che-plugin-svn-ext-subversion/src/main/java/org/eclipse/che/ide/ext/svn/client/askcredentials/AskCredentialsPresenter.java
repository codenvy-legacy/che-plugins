/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.svn.client.askcredentials;

import javax.inject.Inject;

import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.ext.svn.client.SubversionClientService;
import org.eclipse.che.ide.ext.svn.client.SubversionExtensionLocalizationConstants;
import org.eclipse.che.ide.ext.svn.client.askcredentials.AskCredentialsView.AskCredentialsDelegate;
import org.eclipse.che.ide.rest.AsyncRequestCallback;

import static org.eclipse.che.ide.api.notification.StatusNotification.Status.PROGRESS;


public class AskCredentialsPresenter implements AskCredentialsDelegate {

    private final AskCredentialsView                       view;
    private final NotificationManager                      notificationManager;
    private final SubversionExtensionLocalizationConstants constants;
    private final SubversionClientService                  clientService;
    private       String                                   repositoryUrl;

    @Inject
    public AskCredentialsPresenter(final AskCredentialsView view,
                                   final NotificationManager notificationManager,
                                   final SubversionExtensionLocalizationConstants constants,
                                   final SubversionClientService clientService) {
        this.notificationManager = notificationManager;
        this.constants = constants;
        this.view = view;
        this.view.setDelegate(this);
        this.clientService = clientService;
    }

    @Override
    public void onSaveClicked() {
        saveCredentials(view.getUsername(), view.getPassword());
        view.clearUsername();
        view.clearPassword();
        view.close();
    }

    @Override
    public void onCancelClicked() {
        view.clearUsername();
        view.clearPassword();
        view.close();
    }

    public void askCredentials(final String repositoryUrl) {
        view.clearUsername();
        view.clearPassword();
        view.setRepositoryUrl(repositoryUrl);
        this.repositoryUrl = repositoryUrl;
        view.showDialog();
    }

    private void saveCredentials(final String username, final String password) {
        StatusNotification notification = notificationManager.notify(constants.notificationSavingCredentials(repositoryUrl), PROGRESS,
                                                                     false);
        clientService.saveCredentials(repositoryUrl, username, password, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(final Void notUsed) {
                notification.setTitle(constants.notificationCredentialsSaved(repositoryUrl));
                notification.setStatus(StatusNotification.Status.SUCCESS);
            }

            @Override
            protected void onFailure(final Throwable exception) {
                notification.setTitle(constants.notificationCredentialsFailed(repositoryUrl));
                notification.setStatus(StatusNotification.Status.FAIL);
            }
        });
    }
}
