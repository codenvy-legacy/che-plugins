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

import java.util.Map;

import org.eclipse.che.dto.shared.DTO;

/**
 * Describe a database structure: basic informations, schemas, tables and columns.
 */
@DTO
public interface DatabaseDTO extends DatabaseMetadataEntityDTO {

    /* Database overrides. */
    DatabaseDTO withName(String name);

    DatabaseDTO withLookupKey(String lookupKey);

    DatabaseDTO withComment(String comment);

    /* JDBC driver. */
    DatabaseDTO withJdbcDriverName(String driverName);

    void setJdbcDriverName(String driverName);

    String getJdbcDriverName();

    DatabaseDTO withJdbcDriverVersion(String driverVersion);

    void setJdbcDriverVersion(String driverVersion);

    String getJdbcDriverVersion();

    /* Database product. */
    DatabaseDTO withDatabaseProductName(String productName);

    void setDatabaseProductName(String productName);

    String getDatabaseProductName();

    DatabaseDTO withDatabaseProductVersion(String productVersion);

    void setDatabaseProductVersion(String productVersion);

    String getDatabaseProductVersion();

    /* Datasource user name. */
    DatabaseDTO withUserName(String userName);

    void setUserName(String userName);

    String getUserName();

    /* Schemas. */
    DatabaseDTO withSchemas(Map<String, SchemaDTO> schemas);

    Map<String, SchemaDTO> getSchemas();

    void setSchemas(Map<String, SchemaDTO> schemas);

}
