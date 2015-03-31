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
package org.eclipse.che.ide.ext.datasource.client.discovery;

import org.eclipse.che.api.runner.dto.ApplicationProcessDescriptor;
import org.eclipse.che.api.runner.dto.PortMapping;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.datasource.client.DatasourceClientService;
import org.eclipse.che.ide.ext.datasource.client.events.DatasourceListChangeEvent;
import org.eclipse.che.ide.ext.datasource.client.store.DatasourceManager;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseType;
import org.eclipse.che.ide.ext.datasource.shared.TextDTO;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.common.RunnerApplicationStatusEvent;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.common.RunnerApplicationStatusEventHandler;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.StringUnmarshaller;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Eager singleton service that looks at runners status changes and create/remove datasource according to exposed port. If it detects a
 * known database port, it creates an in memory datasource entry.
 */
@Singleton
public class DatasourceDiscovery {

    protected EventBus                eventBus;
    protected AppContext              appContext;
    protected DatasourceManager       dsManager;
    protected DtoFactory              dtoFactory;
    protected DatasourceClientService dsClientService;

    @Inject
    public DatasourceDiscovery(EventBus eventBus,
                               AppContext appContext,
                               DatasourceManager dsManager,
                               DtoFactory dtoFactory,
                               DatasourceClientService dsClientService) {
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.dsManager = dsManager;
        this.dtoFactory = dtoFactory;
        this.dsClientService = dsClientService;
        configureProcessRunEventHandler();
    }

    /**
     * Add handlers to listen to runners status. Automatically discover and create datasource.
     */
    protected void configureProcessRunEventHandler() {
        eventBus.addHandler(RunnerApplicationStatusEvent.TYPE, new RunnerApplicationStatusEventHandler() {
            @Override
            public void onRunnerStatusChanged(@Nonnull Runner runner) {
                ApplicationProcessDescriptor descriptor = runner.getDescriptor();
                if (descriptor == null) {
                    return;
                }
                switch (descriptor.getStatus()) {
                    case RUNNING:
                        addDatasource(descriptor);
                        break;
                    case FAILED:
                    case STOPPED:
                    case CANCELLED:
                        removeDatasource(descriptor, appContext);
                        break;

                    default:
                }
            }
        });
    }

    private void addDatasource(@Nonnull ApplicationProcessDescriptor applicationProcessDescriptor) {
        PortMapping portMapping = applicationProcessDescriptor.getPortMapping();
        CurrentProject currentProject = appContext.getCurrentProject();
        if (portMapping == null || currentProject == null) {
            return;
        }
        DatabaseConfigurationDTO dsConfig = dtoFactory.createDto(DatabaseConfigurationDTO.class);
        String projectName = appContext.getCurrentProject().getProjectDescription().getName();
        long processId = applicationProcessDescriptor.getProcessId();
        String host = portMapping.getHost();
        for (DatabaseType databaseType : DatabaseType.values()) {
            if (DatabaseType.GOOGLECLOUDSQL.equals(databaseType)) {
                // ignore google sql for now as it has the same port then mysql. Maybe clean up and remove it from the list
                continue;
            }
            String mappedPort = portMapping.getPorts().get(Integer.toString(databaseType.getDefaultPort()));
            if (mappedPort != null) {
                addDatasource(processId, dsConfig, databaseType, projectName, Integer.parseInt(mappedPort), host);
            }
        }
    }

    protected void addDatasource(long processId, final DatabaseConfigurationDTO dsConfig,
                                 DatabaseType databaseType,
                                 String projectName,
                                 int mappedPort,
                                 String host) {
        dsConfig.setDatasourceId("Runner-DS-" + projectName + "-" + mappedPort);
        dsConfig.setConfigurationConnectorId(databaseType.getConnectorId());
        dsConfig.setDatabaseType(databaseType);
        dsConfig.setUsername(databaseType.getDefaultUsername());
        dsConfig.setHostName(host);
        dsConfig.setPort(mappedPort);
        dsConfig.setRunnerProcessId(processId);
        dsConfig.setDatabaseName("");
        try {
            dsClientService.encryptText(databaseType.getDefaultPassword()
                    , new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(final String encryptedText) {
                    TextDTO encryptedTextDTO = dtoFactory.createDtoFromJson(encryptedText, TextDTO.class);
                    dsConfig.setPassword(encryptedTextDTO.getValue());
                    dsManager.add(dsConfig);
                    eventBus.fireEvent(new DatasourceListChangeEvent());
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Log.error(DatasourceDiscovery.class, exception);
                }
            });
        } catch (RequestException e2) {
            Log.error(DatasourceDiscovery.class, e2);
        }


    }

    protected void removeDatasource(ApplicationProcessDescriptor applicationProcessDescriptor,
                                    AppContext appContext) {
        Long processId = applicationProcessDescriptor.getProcessId();
        if (processId == null) {
            return;
        }
        for (DatabaseConfigurationDTO databaseConfigurationDTO : dsManager) {
            if (processId.equals(databaseConfigurationDTO.getRunnerProcessId())) {
                dsManager.remove(databaseConfigurationDTO);
                eventBus.fireEvent(new DatasourceListChangeEvent());
            }
        }

    }
}