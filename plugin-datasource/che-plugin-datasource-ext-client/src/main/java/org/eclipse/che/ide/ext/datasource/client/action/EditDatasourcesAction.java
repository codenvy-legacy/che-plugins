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
package org.eclipse.che.ide.ext.datasource.client.action;

import javax.validation.constraints.NotNull;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.ext.datasource.client.DatasourceUiResources;
import org.eclipse.che.ide.ext.datasource.client.editdatasource.EditDatasourceMessages;
import org.eclipse.che.ide.ext.datasource.client.editdatasource.EditDatasourcesPresenter;
import org.eclipse.che.ide.ext.datasource.client.editdatasource.EditDatasourcesPresenterFactory;
import com.google.inject.Inject;

/**
 * IDE action to edit and delete datasources.
 * 
 * @author "Mickaël Leduque"
 */
public class EditDatasourcesAction extends Action {

    /** The factory to instanciate the dialg presenter. */
    private final EditDatasourcesPresenterFactory dialogFactory;

    private final AnalyticsEventLogger eventLogger;

    @Inject
    public EditDatasourcesAction(@NotNull final EditDatasourcesPresenterFactory dialogFactory,
                                 @NotNull final EditDatasourceMessages messages,
                                 @NotNull DatasourceUiResources resources,
                                 AnalyticsEventLogger eventLogger) {
        super(messages.editDatasourcesMenuText(), messages.editDatasourcesMenuDescription(), null,
              resources.manageDatasourceMenuIcon());
        this.dialogFactory = dialogFactory;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        eventLogger.log(this);
        final EditDatasourcesPresenter dialogPresenter = this.dialogFactory.createEditDatasourcesPresenter();
        dialogPresenter.showDialog();
    }
}
