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

import org.eclipse.che.dto.shared.DTO;

@DTO
public interface RequestParameterDTO {

    DatabaseConfigurationDTO getDatabase();

    void setDatabase(DatabaseConfigurationDTO database);

    RequestParameterDTO withDatabase(DatabaseConfigurationDTO database);

    String getSqlRequest();

    void setSqlRequest(String sqlRequest);

    RequestParameterDTO withSqlRequest(String sqlRequest);

    int getResultLimit();

    void setResultLimit(int resultLimit);

    RequestParameterDTO withResultLimit(int resultLimit);


    MultipleRequestExecutionMode getMultipleRequestExecutionMode();

    void setMultipleRequestExecutionMode(MultipleRequestExecutionMode mode);

    RequestParameterDTO withMultipleRequestExecutionMode(MultipleRequestExecutionMode mode);
}
