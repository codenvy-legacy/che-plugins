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
package org.eclipse.che.ide.ext.datasource.client.newdatasource.connector.mysql;

import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.datasource.client.DatasourceClientService;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.NewDatasourceWizardMessages;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.connector.DefaultNewDatasourceConnectorPage;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.connector.DefaultNewDatasourceConnectorView;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseType;
import com.google.inject.Inject;

/**
 * Created by Wafa on 20/01/14.
 */
public class MysqlDatasourceConnectorPage extends DefaultNewDatasourceConnectorPage {

    @Inject
    public MysqlDatasourceConnectorPage(final DefaultNewDatasourceConnectorView view,
                                        final NotificationManager notificationManager,
                                        final DtoFactory dtoFactory,
                                        final DatasourceClientService service,
                                        final NewDatasourceWizardMessages messages) {
        super(view, service, notificationManager, dtoFactory, messages, DatabaseType.MYSQL.getDefaultPort(), DatabaseType.MYSQL);
    }
}
