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
public interface NuoDBDatasourceDefinitionDTO extends DatabaseConfigurationDTO {

    // List<NuoDBBrokerDTO> getBrokers();
    //
    // void setBrokers(List<NuoDBBrokerDTO> brokers);
    //
    NuoDBDatasourceDefinitionDTO withBrokers(List<NuoDBBrokerDTO> brokers);


    /* Change type of parent with* methods */
    @Override
    NuoDBDatasourceDefinitionDTO withDatabaseType(DatabaseType type);

    @Override
    NuoDBDatasourceDefinitionDTO withDatabaseName(String databaseName);

    @Override
    NuoDBDatasourceDefinitionDTO withDatasourceId(String type);

    @Override
    NuoDBDatasourceDefinitionDTO withUsername(String username);

    @Override
    NuoDBDatasourceDefinitionDTO withPassword(String password);

    @Override
    NuoDBDatasourceDefinitionDTO withRunnerProcessId(Long runnerProcessId);
}
