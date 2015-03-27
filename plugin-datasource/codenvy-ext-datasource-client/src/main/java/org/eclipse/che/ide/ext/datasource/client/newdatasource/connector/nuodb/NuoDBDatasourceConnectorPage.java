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

import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.datasource.client.DatasourceClientService;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.InitializableWizardPage;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.NewDatasourceWizard;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.NewDatasourceWizardMessages;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.connector.AbstractNewDatasourceConnectorPage;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseType;
import org.eclipse.che.ide.ext.datasource.shared.NuoDBBrokerDTO;
import org.eclipse.che.ide.ext.datasource.shared.NuoDBDatasourceDefinitionDTO;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * This connector page is using JTDS JDBC Driver to connect to MS SQLserver.
 */
public class NuoDBDatasourceConnectorPage extends AbstractNewDatasourceConnectorPage implements
                                                                                    NuoDBDatasourceConnectorView.NuoActionDelegate,
                                                                                    InitializableWizardPage {

    private final ListDataProvider<NuoDBBroker> brokersProvider           = new ListDataProvider<>();
    private final DtoFactory                    dtoFactory;

    @Inject
    public NuoDBDatasourceConnectorPage(final NuoDBDatasourceConnectorView view,
                                        final NotificationManager notificationManager,
                                        final DtoFactory dtoFactory,
                                        final DatasourceClientService service,
                                        final NewDatasourceWizardMessages messages) {

        super(view, service, notificationManager, dtoFactory, messages);
        view.setNuoDelegate(this);

        this.dtoFactory = dtoFactory;

        final NuoDBBroker firstBroker = createNewBroker(0);
        brokersProvider.getList().add(firstBroker);

        view.bindBrokerList(brokersProvider);
    }

    private NuoDBBroker createNewBroker(final int id) {
        final NuoDBBroker newBroker = NuoDBBroker.create(id);
        newBroker.setHost("localhost");
        newBroker.setPort(DatabaseType.NUODB.getDefaultPort());
        return newBroker;
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(getView());
        updateView();
    }

    @Override
    public NuoDBDatasourceConnectorView getView() {
        return (NuoDBDatasourceConnectorView)super.getView();
    }

    /**
     * Returns the currently configured database.
     *
     * @return the database
     */
    @Override
    protected DatabaseConfigurationDTO getConfiguredDatabase() {
        String datasourceId = context.get(NewDatasourceWizard.DATASOURCE_NAME_KEY);

        final List<NuoDBBrokerDTO> brokersConf = new ArrayList<>();
        for (final NuoDBBroker broker : brokersProvider.getList()) {
            if (broker.getHost() != null && !"".equals(broker.getHost()) && broker.getPort() != null) {
                final NuoDBBrokerDTO brokerDto = dtoFactory.createDto(NuoDBBrokerDTO.class)
                                                           .withHostName(broker.getHost())
                                                           .withPort(broker.getPort());
                brokersConf.add(brokerDto);
            }
        }

        NuoDBDatasourceDefinitionDTO result = dtoFactory.createDto(NuoDBDatasourceDefinitionDTO.class)
                                                        .withDatabaseName(getView().getDatabaseName())
                                                        .withDatabaseType(getDatabaseType())
                                                        .withDatasourceId(datasourceId)
                                                        .withUsername(getView().getUsername())
                                                        .withPassword(getView().getEncryptedPassword())
                                                        .withBrokers(brokersConf)
                                                        .withRunnerProcessId(getView().getRunnerProcessId());

        result.withConfigurationConnectorId(dataObject.getConfigurationConnectorId());

        return result;
    }

    @Override
    public Integer getDefaultPort() {
        return DatabaseType.NUODB.getDefaultPort();
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.NUODB;
    }

    @Override
    public void onAddBroker() {
        Log.debug(NuoDBDatasourceConnectorPage.class, "Adding a broker.");
        int brokerCount = brokersProvider.getList().size();
        final NuoDBBroker newBroker = createNewBroker(brokerCount);
        // insert the new row ; the display should be updated automatically
        brokersProvider.getList().add(newBroker);

        dataObject.getBrokers().add(dtoFactory.createDto(NuoDBBrokerDTO.class)
                                              .withHostName(newBroker.getHost())
                                              .withPort(newBroker.getPort()));
    }

    @Override
    public void onDeleteBrokers() {
        Log.debug(NuoDBDatasourceConnectorPage.class, "Deleting selected brokers.");
        final Set<NuoDBBroker> selection = getView().getBrokerSelection();
        // remove selected items from the list provider
        // the list wrapper should update the view by itself
        brokersProvider.getList().removeAll(selection);
        dataObject.getBrokers().remove(selection);
    }

    @Override
    public void initPage(final Object data) {
        // should set exactly the same fields as those read in getConfiguredDatabase except those configured in first page
        if (!(data instanceof DatabaseConfigurationDTO)) {
            clearPage();
            return;
        }
        DatabaseConfigurationDTO initData = (DatabaseConfigurationDTO)data;
        dataObject.setDatabaseName(initData.getDatabaseName());
        dataObject.setUsername(initData.getUsername());
        dataObject.setRunnerProcessId(initData.getRunnerProcessId());
        dataObject.setBrokers(initData.getBrokers());
    }

    @Override
    public void clearPage() {
        getView().setDatabaseName("");
        getView().setUsername("");
        getView().setPassword("");
        brokersProvider.getList().clear();
        brokersProvider.flush();
        updateDelegate.updateControls();
    }

    public void updateView() {
        getView().setDatabaseName(dataObject.getDatabaseName());
        getView().setUsername(dataObject.getUsername());
        getView().setEncryptedPassword(dataObject.getPassword(), true);
        getView().setRunnerProcessId(dataObject.getRunnerProcessId());

        brokersProvider.getList().clear();
        int id = 0;
        for (final NuoDBBrokerDTO brokerDto : dataObject.getBrokers()) {
            final NuoDBBroker broker = NuoDBBroker.create(id);
            broker.setHost(brokerDto.getHostName());
            broker.setPort(brokerDto.getPort());
            brokersProvider.getList().add(broker);
            id++;
        }
        brokersProvider.flush();
        updateDelegate.updateControls();
    }
}
