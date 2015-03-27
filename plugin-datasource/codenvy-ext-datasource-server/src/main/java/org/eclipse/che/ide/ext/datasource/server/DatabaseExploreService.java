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
package org.eclipse.che.ide.ext.datasource.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.ext.datasource.shared.ColumnDTO;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseDTO;
import org.eclipse.che.ide.ext.datasource.shared.ExploreRequestDTO;
import org.eclipse.che.ide.ext.datasource.shared.ExploreTableType;
import org.eclipse.che.ide.ext.datasource.shared.SchemaDTO;
import org.eclipse.che.ide.ext.datasource.shared.ServicePaths;
import org.eclipse.che.ide.ext.datasource.shared.TableDTO;
import org.eclipse.che.ide.ext.datasource.shared.exception.DatabaseDefinitionException;
import org.eclipse.che.ide.ext.datasource.shared.exception.ExploreException;
import com.google.common.collect.Lists;
import com.google.common.math.LongMath;
import com.google.inject.Inject;

/**
 * Service for crawling databases.
 * 
 * @author "Mickaël Leduque"
 */
@Path(ServicePaths.DATABASE_EXPLORE_PATH)
public class DatabaseExploreService {

    /** The logger. */
    private static final Logger         LOG                     = LoggerFactory.getLogger(DatabaseExploreService.class);


    /** Table types list for simple mode. */
    private static final List<String>   TABLE_TYPES_SIMPLE      = Lists.newArrayList("TABLE", "VIEW");

    /** Table types list for standard mode. */
    private static final List<String>   TABLE_TYPES_STANDARD    = Lists.newArrayList("TABLE", "VIEW", "MATERIALIZED VIEW",
                                                                                     "ALIAS", "SYNONYM");
    /** Table types list for system mode. */
    private static final List<String>   TABLES_TYPE_WITH_SYSTEM = Lists.newArrayList("TABLE", "VIEW", "MATERIALIZED VIEW",
                                                                                     "ALIAS", "SYNONYM", "SYSTEM TABLE",
                                                                                     "SYSTEM VIEW");
    /** Table types list for 'all' mode. */
    private static final List<String>   TABLES_TYPE_ALL         = Lists.newArrayList();

    /** The connection provider. */
    private final JdbcConnectionFactory jdbcConnectionFactory;

    @Inject
    public DatabaseExploreService(final JdbcConnectionFactory jdbcConnectionFactory) {
        this.jdbcConnectionFactory = jdbcConnectionFactory;
    }

    /**
     * Explores a database.
     * 
     * @param exploreRequest the parameters for the explore request
     * @return a description of the contents of the database
     * @throws DatabaseDefinitionException if the datasource configuration is incorrect
     * @throws SQLException if the database connection could not be created
     * @throws ExploreException on crawling error
     * @throws SchemaCrawlerException if database exploration failed
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public String getDatabase(final ExploreRequestDTO exploreRequest) throws SQLException,
                                                                     DatabaseDefinitionException,
                                                                     ExploreException {

        if (exploreRequest == null) {
            throw new ExploreException("The explore request parameter is null");
        }

        final DatabaseConfigurationDTO databaseConfig = exploreRequest.getDatasourceConfig();
        if (databaseConfig == null) {
            throw new DatabaseDefinitionException("Database definition is null");
        }

        ExploreTableType exploreMode = exploreRequest.getExploreTableType();
        // by default, only crawl simple entities
        if (exploreMode == null) {
            exploreMode = ExploreTableType.STANDARD;
        }

        Database database = null;

        final long startTime = System.currentTimeMillis();
        long endSetupTime;
        try (final Connection connection = this.jdbcConnectionFactory.getDatabaseConnection(databaseConfig)) {
            final SchemaCrawlerOptions options = new SchemaCrawlerOptions();

            // info level set to standard + additional table and column attributes
            SchemaInfoLevel customized = SchemaInfoLevel.standard();
            customized.setRetrieveAdditionalTableAttributes(true);
            customized.setRetrieveAdditionalColumnAttributes(true);
            options.setSchemaInfoLevel(customized);

            // exclude procedures and function for now
            options.setRoutineInclusionRule(new ExcludeAll());

            List<String> tabletypes = null;
            switch (exploreMode) {
                case SIMPLE:
                    tabletypes = TABLE_TYPES_SIMPLE;
                    break;
                case SYSTEM:
                    tabletypes = TABLES_TYPE_WITH_SYSTEM;
                    break;
                case ALL:
                    tabletypes = TABLES_TYPE_ALL;
                    break;
                case STANDARD:
                default:
                    tabletypes = TABLE_TYPES_STANDARD;
                    break;
            }
            options.setTableTypes(tabletypes);

            endSetupTime = System.currentTimeMillis();
            database = SchemaCrawlerUtility.getDatabase(connection, options);
        } catch (final SchemaCrawlerException e) {
            throw new ExploreException("Couldn't explore database - " + e.getLocalizedMessage());
        }
        final long endCrawlingTime = System.currentTimeMillis();

        DatabaseDTO databaseDTO = DtoFactory.getInstance().createDto(DatabaseDTO.class)
                                            .withName(database.getName())
                                            .withLookupKey(database.getLookupKey())
                                            .withDatabaseProductName(database.getDatabaseInfo().getProductName())
                                            .withDatabaseProductVersion(database.getDatabaseInfo().getProductVersion())
                                            .withUserName(database.getDatabaseInfo().getUserName())
                                            .withJdbcDriverName(database.getJdbcDriverInfo().getDriverName())
                                            .withJdbcDriverVersion(database.getJdbcDriverInfo().getDriverVersion())
                                            .withComment(database.getRemarks());
        final Map<String, SchemaDTO> schemaToInject = new HashMap<String, SchemaDTO>();
        databaseDTO = databaseDTO.withSchemas(schemaToInject);
        for (final Schema schema : database.getSchemas()) {
            final SchemaDTO schemaDTO = DtoFactory.getInstance().createDto(SchemaDTO.class)
                                                  // TODO maybe clean up this (schema.getName() can be null with mysql and the json
                                                  // serializer has
                                                  // a constraint on it)
                                                  // TODO do we always want to display the fullname ? rather than the name ?
                                                  .withName((schema.getName() == null) ? schema.getFullName() : schema.getName())
                                                  .withLookupKey(schema.getLookupKey())
                                                  .withComment(database.getRemarks());
            final Map<String, TableDTO> tables = new HashMap<String, TableDTO>();
            for (final Table table : database.getTables(schema)) {
                TableDTO tableDTO = DtoFactory.getInstance().createDto(TableDTO.class)
                                              .withName(table.getName())
                                              .withLookupKey(table.getLookupKey())
                                              .withType(table.getTableType().getTableType())
                                              .withComment(database.getRemarks());
                if (table instanceof View) {
                    tableDTO = tableDTO.withIsView(true);
                }

                final PrimaryKey primaryKey = table.getPrimaryKey();
                if (primaryKey != null) {
                    final List<IndexColumn> primaryKeyColumns = primaryKey.getColumns();
                    final List<String> pkColumnNames = new ArrayList<>();
                    for (final IndexColumn indexColumn : primaryKeyColumns) {
                        pkColumnNames.add(indexColumn.getName());
                    }
                    tableDTO.setPrimaryKey(pkColumnNames);
                }


                final Map<String, ColumnDTO> columns = new HashMap<String, ColumnDTO>();
                for (final Column column : table.getColumns()) {
                    final ColumnDTO columnDTO = DtoFactory.getInstance().createDto(ColumnDTO.class)
                                                          .withName(column.getName())
                                                          .withLookupKey(column.getLookupKey())
                                                          .withColumnDataType(column.getColumnDataType().getName())
                                                          .withDefaultValue(column.getDefaultValue())
                                                          .withNullable(column.isNullable())
                                                          .withDataSize(column.getSize())
                                                          .withDecimalDigits(column.getDecimalDigits())
                                                          .withPartOfForeignKey(column.isPartOfForeignKey())
                                                          .withPartOfPrimaryKey(column.isPartOfPrimaryKey())
                                                          .withOrdinalPositionInTable(column.getOrdinalPosition())
                                                          .withComment(database.getRemarks());
                    columns.put(columnDTO.getName(), columnDTO);
                }
                tableDTO = tableDTO.withColumns(columns);
                tables.put(tableDTO.getName(), tableDTO);
            }
            schemaDTO.withTables(tables);
            schemaToInject.put(schemaDTO.getName(), schemaDTO);
        }
        final long endDtoTime = System.currentTimeMillis();
        final String jsonResult = DtoFactory.getInstance().toJson(databaseDTO);
        final long endJsonTime = System.currentTimeMillis();

        try {
            LOG.debug("Schema metadata obtained - setup {}ms ; crawl {}ms ; dto conversion {}ms ; json conversion {}ms - total {}ms",
                      LongMath.checkedSubtract(endSetupTime, startTime),
                      LongMath.checkedSubtract(endCrawlingTime, endSetupTime),
                      LongMath.checkedSubtract(endDtoTime, endCrawlingTime),
                      LongMath.checkedSubtract(endJsonTime, endDtoTime),
                      LongMath.checkedSubtract(endJsonTime, startTime));
        } catch (final ArithmeticException e) {
            LOG.debug("Schema metadata obtained - no time info");
        }

        return jsonResult;
    }
}
