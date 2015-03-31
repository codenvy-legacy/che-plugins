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
package org.eclipse.che.ide.ext.datasource.client.newdatasource;

import static org.eclipse.che.ide.api.notification.Notification.Type.ERROR;

import javax.validation.constraints.NotNull;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.datasource.client.DatasourceUiResources;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.presenter.NewDatasourceWizardPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class NewDatasourceWizardAction extends Action {

    /** The {@link NotificationManager} used to show start, completion or error messages to the user. */
    protected NotificationManager              notificationManager;

    private final NewDatasourceWizardPresenter wizard;

    private final AnalyticsEventLogger eventLogger;

    /** The messages interface. */
    private final NewDatasourceWizardMessages messages;

    @Inject
    public NewDatasourceWizardAction(@NotNull final DatasourceUiResources resources,
                                     @NotNull NewDatasourceWizardPresenter wizard,
                                     @NotNull final NotificationManager notificationManager,
                                     @NotNull final NewDatasourceWizardMessages messages,
                                     AnalyticsEventLogger eventLogger) {
        super(messages.newDatasourceMenuText(), messages.newDatasourceMenuDescription(), null,
              resources.newDatasourceMenuIcon());
        this.wizard = wizard;
        this.messages = messages;
        this.notificationManager = notificationManager;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actionPerformed();
    }

    /**
     * Reaction to action activation.
     */
    public void actionPerformed() {
        try {
            eventLogger.log(this);
            wizard.show();
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
