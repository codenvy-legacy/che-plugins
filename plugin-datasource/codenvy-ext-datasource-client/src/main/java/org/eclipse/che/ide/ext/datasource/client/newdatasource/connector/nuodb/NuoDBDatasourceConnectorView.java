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
package org.eclipse.che.ide.ext.datasource.client.newdatasource.connector.nuodb;

import java.util.Set;

import org.eclipse.che.ide.ext.datasource.client.newdatasource.connector.AbstractNewDatasourceConnectorView;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.ImplementedBy;

@ImplementedBy(NuoDBDatasourceConnectorViewImpl.class)
public interface NuoDBDatasourceConnectorView extends AbstractNewDatasourceConnectorView<NuoDBDatasourceConnectorView.NuoActionDelegate> {

    /** Additional delegate methods. */
    public interface NuoActionDelegate extends AbstractNewDatasourceConnectorView.ActionDelegate {

        void onAddBroker();

        void onDeleteBrokers();
    }

    String getDatabaseName();

    void bindBrokerList(ListDataProvider<NuoDBBroker> dataProvider);

    Set<NuoDBBroker> getBrokerSelection();

    void setNuoDelegate(NuoActionDelegate delegate);

    void setDatabaseName(String databaseName);

    void setUsername(String username);

    void setPassword(String password);
}
