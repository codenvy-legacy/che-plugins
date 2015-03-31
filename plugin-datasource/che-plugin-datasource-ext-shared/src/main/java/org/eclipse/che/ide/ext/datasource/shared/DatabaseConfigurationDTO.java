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
package org.eclipse.che.ide.ext.datasource.shared;

import java.util.List;

import org.eclipse.che.dto.shared.DTO;

@DTO
public interface DatabaseConfigurationDTO {

    String getDatasourceId();

    void setDatasourceId(String id);

    DatabaseConfigurationDTO withDatasourceId(String type);


    String getConfigurationConnectorId();

    void setConfigurationConnectorId(String connectorId);

    DatabaseConfigurationDTO withConfigurationConnectorId(String connectorId);


    String getDatabaseName();

    void setDatabaseName(String databaseName);

    DatabaseConfigurationDTO withDatabaseName(String databaseName);


    DatabaseType getDatabaseType();

    void setDatabaseType(DatabaseType type);

    DatabaseConfigurationDTO withDatabaseType(DatabaseType type);


    String getUsername();

    DatabaseConfigurationDTO withUsername(String username);

    void setUsername(String username);


    String getPassword();

    void setPassword(String password);

    DatabaseConfigurationDTO withPassword(String password);


    /* should be in child classes */
    String getHostName();

    int getPort();


    void setHostName(String hostname);

    void setPort(int port);


    List<NuoDBBrokerDTO> getBrokers();

    void setBrokers(List<NuoDBBrokerDTO> brokers);


    boolean getUseSSL();

    DatabaseConfigurationDTO withUseSSL(boolean useSSL);

    void setUseSSL(boolean useSSL);


    boolean getVerifyServerCertificate();

    DatabaseConfigurationDTO withVerifyServerCertificate(boolean verifyServerCertificate);

    void setVerifyServerCertificate(boolean verifyServerCertificate);

    void setRunnerProcessId(Long processId);

    Long getRunnerProcessId();

    DatabaseConfigurationDTO withRunnerProcessId(Long runnerProcessId);
}
