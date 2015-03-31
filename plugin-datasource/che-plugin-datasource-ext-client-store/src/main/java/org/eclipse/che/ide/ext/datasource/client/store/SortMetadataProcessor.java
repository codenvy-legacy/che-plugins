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

import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.datasource.shared.ColumnDTO;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseDTO;
import org.eclipse.che.ide.ext.datasource.shared.SchemaDTO;
import org.eclipse.che.ide.ext.datasource.shared.TableDTO;

/**
 * {@link PreStoreProcessor} that sorts schemas, tables and columns in lexicographical order.
 * 
 * @author "Mickaël Leduque"
 */
public class SortMetadataProcessor implements PreStoreProcessor {

    /** The client version of the DTO factory. */
    private final DtoFactory dtoFactory;

    @Inject
    public SortMetadataProcessor(final @NotNull DtoFactory dtoFactory) {
        this.dtoFactory = dtoFactory;
    }

    @Override
    public DatabaseDTO execute(final DatabaseDTO databaseDto) throws PreStoreProcessorException {
        if (databaseDto == null) {
            return null;
        }
        // create a copy
        final String json = this.dtoFactory.toJson(databaseDto);
        final DatabaseDTO modified = this.dtoFactory.createDtoFromJson(json, DatabaseDTO.class);

        sortSchemas(modified);

        return modified;
    }

    /**
     * Sort the schemas in the database metadata DTO.
     * 
     * @param database the metadata DTO.
     */
    private void sortSchemas(final DatabaseDTO database) {
        for (final SchemaDTO schema : database.getSchemas().values()) {
            sortTables(schema);
        }

        SortedMap<String, SchemaDTO> sortedSchemas = new TreeMap<String, SchemaDTO>(database.getSchemas());
        database.setSchemas(sortedSchemas);
    }

    /**
     * Sort the tables in the schema metadata DTO.
     * 
     * @param schema the metadata DTO
     */
    private void sortTables(final SchemaDTO schema) {
        for (final TableDTO table : schema.getTables().values()) {
            sortColumns(table);
        }

        SortedMap<String, TableDTO> sortedTables = new TreeMap<String, TableDTO>(schema.getTables());
        schema.setTables(sortedTables);
    }

    /**
     * Sort the columns in the table metadata DTO.
     * 
     * @param table the metadata DTO
     */
    private void sortColumns(final TableDTO table) {
        SortedMap<String, ColumnDTO> sortedColumn = new TreeMap<String, ColumnDTO>(table.getColumns());
        table.setColumns(sortedColumn);
    }
}
