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
package org.eclipse.che.ide.ext.datasource.client.editdatasource.wizard;

import static org.eclipse.che.ide.api.notification.Notification.Type.ERROR;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.InitializableWizardDialog;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.NewDatasourceWizardMessages;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.presenter.NewDatasourceWizardPresenter;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class EditDatasourceLauncher {

    private final Provider<NewDatasourceWizardPresenter> newDatasourcePageProvider;
    private final NewDatasourceWizardMessages            messages;
    protected NotificationManager                        notificationManager;

    @Inject
    public EditDatasourceLauncher(Provider<NewDatasourceWizardPresenter> newDatasourcePageProvider,
                                  @NotNull final NotificationManager notificationManager,
                                  @NotNull final NewDatasourceWizardMessages messages) {
        this.newDatasourcePageProvider = newDatasourcePageProvider;
        this.messages = messages;
        this.notificationManager = notificationManager;
    }

    public void launch(final DatabaseConfigurationDTO datasource) {
        NewDatasourceWizardPresenter newDatasourceWizardPresenter = newDatasourcePageProvider.get();
        InitializableWizardDialog<DatabaseConfigurationDTO> wizardDialog = (InitializableWizardDialog<DatabaseConfigurationDTO>) newDatasourceWizardPresenter;
        wizardDialog.initData(datasource);
        try {
            newDatasourceWizardPresenter.show();
        } catch (final Exception exception) {
            String errorMessage = messages.defaultNewDatasourceWizardErrorMessage();
            if (exception.getMessage() != null) {
                errorMessage = exception.getMessage();
            }

            final Notification notification = new Notification(errorMessage, ERROR);
            notificationManager.showNotification(notification);
        }
    }
}
