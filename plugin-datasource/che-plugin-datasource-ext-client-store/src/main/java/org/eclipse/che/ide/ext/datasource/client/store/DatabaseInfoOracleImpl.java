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
package org.eclipse.che.ide.ext.datasource.client.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.ext.datasource.shared.ColumnDTO;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseDTO;
import org.eclipse.che.ide.ext.datasource.shared.SchemaDTO;
import org.eclipse.che.ide.ext.datasource.shared.TableDTO;
import com.google.inject.Inject;

public class DatabaseInfoOracleImpl implements DatabaseInfoOracle {

    private DatabaseInfoStore databaseInfoStore;

    @Inject
    public DatabaseInfoOracleImpl(final @NotNull DatabaseInfoStore databaseInfoStore) {
        this.databaseInfoStore = databaseInfoStore;
    }

    private Map<String, SchemaDTO> getSchemaDtosFor(final String datasourceId) {
        final DatabaseDTO database = this.databaseInfoStore.getDatabaseInfo(datasourceId);
        if (database != null && database.getSchemas() != null) {
            return database.getSchemas();
        }
        return new HashMap<String, SchemaDTO>();
    }

    private Map<String, TableDTO> getTableDtosFor(final String datasourceId, final String schema) {
        Map<String, SchemaDTO> schemaDtos = getSchemaDtosFor(datasourceId);
        final SchemaDTO schemaDto = schemaDtos.get(schema);
        if (schemaDto != null && schemaDto.getTables() != null) {
            return schemaDto.getTables();
        }
        return new HashMap<String, TableDTO>();
    }

    private Map<String, ColumnDTO> getColumnDtosFor(final String datasourceId, final String schema, final String table) {
        Map<String, TableDTO> tableDtos = getTableDtosFor(datasourceId, schema);
        final TableDTO tableDto = tableDtos.get(table);
        if (tableDto != null && tableDto.getColumns() != null) {
            return tableDto.getColumns();
        }
        return new HashMap<String, ColumnDTO>();
    }

    @Override
    public Collection<String> getSchemasFor(final String datasourceId) {
        return getSchemaDtosFor(datasourceId).keySet();
    }

    @Override
    public Collection<String> getTablesFor(final String datasourceId, final String schema) {
        return getTableDtosFor(datasourceId, schema).keySet();
    }

    @Override
    public Collection<String> getColumnsFor(final String datasourceId, final String schema, final String table) {
        return getColumnDtosFor(datasourceId, schema, table).keySet();
    }

}
