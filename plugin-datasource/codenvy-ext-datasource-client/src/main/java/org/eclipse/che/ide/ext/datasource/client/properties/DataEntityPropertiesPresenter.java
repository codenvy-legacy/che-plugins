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
package org.eclipse.che.ide.ext.datasource.client.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.che.ide.api.parts.AbstractPartPresenter;
import org.eclipse.che.ide.ext.datasource.client.events.SelectedDatasourceChangeEvent;
import org.eclipse.che.ide.ext.datasource.client.events.SelectedDatasourceChangeHandler;
import org.eclipse.che.ide.ext.datasource.client.selection.DatabaseEntitySelectionEvent;
import org.eclipse.che.ide.ext.datasource.client.selection.DatabaseEntitySelectionHandler;
import org.eclipse.che.ide.ext.datasource.client.selection.DatabaseInfoErrorEvent;
import org.eclipse.che.ide.ext.datasource.client.selection.DatabaseInfoErrorHandler;
import org.eclipse.che.ide.ext.datasource.client.selection.DatabaseInfoReceivedEvent;
import org.eclipse.che.ide.ext.datasource.client.selection.DatabaseInfoReceivedHandler;
import org.eclipse.che.ide.ext.datasource.client.store.DatabaseInfoStore;
import org.eclipse.che.ide.ext.datasource.shared.ColumnDTO;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseDTO;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseMetadataEntityDTO;
import org.eclipse.che.ide.ext.datasource.shared.SchemaDTO;
import org.eclipse.che.ide.ext.datasource.shared.TableDTO;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The presenter part for the database item properties display.
 * 
 * @author "Mickaël Leduque"
 */
public class DataEntityPropertiesPresenter extends AbstractPartPresenter implements DataEntityPropertiesView.ActionDelegate,
                                                                        SelectedDatasourceChangeHandler,
                                                                        DatabaseEntitySelectionHandler,
                                                                        DatabaseInfoReceivedHandler,
                                                                        DatabaseInfoErrorHandler {

    /** The view component. */
    private final DataEntityPropertiesView      view;

    private final ListDataProvider<Property>    dataProvider = new ListDataProvider<Property>(new PropertyKeyProvider());

    private final DataEntityPropertiesConstants constants;

    private final DatabaseInfoStore             databaseInfoStore;

    private String                              selectedDatabaseId;

    @Inject
    public DataEntityPropertiesPresenter(final DataEntityPropertiesView view,
                                         final EventBus eventBus,
                                         final DataEntityPropertiesConstants constants,
                                         final DatabaseInfoStore databaseInfoStore) {
        super();
        this.view = view;
        this.view.setDelegate(this);
        this.view.bindDataProvider(this.dataProvider);
        this.constants = constants;
        this.databaseInfoStore = databaseInfoStore;
        eventBus.addHandler(DatabaseEntitySelectionEvent.getType(), this);
        eventBus.addHandler(DatabaseInfoReceivedEvent.getType(), this);
        eventBus.addHandler(DatabaseInfoErrorEvent.getType(), this);
        eventBus.addHandler(SelectedDatasourceChangeEvent.getType(), this);
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public String getTitle() {
        return "Datasource properties";
    }

    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    @Override
    public String getTitleToolTip() {
        return null;
    }

    @Override
    public void onDatabaseEntitySelection(final DatabaseEntitySelectionEvent event) {
        final DatabaseMetadataEntityDTO newSelection = event.getSelection();
        if (newSelection == null) {
            if (this.selectedDatabaseId == null) {
                updateDisplay(null);
            } else {
                final DatabaseDTO metadata = this.databaseInfoStore.getDatabaseInfo(this.selectedDatabaseId);
                updateDisplay(metadata);
            }
        } else {
            updateDisplay(newSelection);
        }
    }

    /**
     * Update the properties table display.
     * 
     * @param newSelection the selected object
     */
    private void updateDisplay(final DatabaseMetadataEntityDTO newSelection) {

        boolean showProperties = true;

        if (newSelection == null) {
            Log.debug(DataEntityPropertiesPresenter.class, "No selection, hiding properties display.");
            showProperties = false;
            setPropertyList(null);
        }

        if (newSelection instanceof DatabaseDTO) {
            Log.debug(DataEntityPropertiesPresenter.class, "Show properties for database entity.");
            setPropertyList(getPropertiesForDatabase((DatabaseDTO)newSelection));
        } else if (newSelection instanceof SchemaDTO) {
            Log.debug(DataEntityPropertiesPresenter.class, "Show properties for schema entity.");
            setPropertyList(getPropertiesForSchema((SchemaDTO)newSelection));
        } else if (newSelection instanceof TableDTO) {
            Log.debug(DataEntityPropertiesPresenter.class, "Show properties for table entity.");
            setPropertyList(getPropertiesForTable((TableDTO)newSelection));
        } else if (newSelection instanceof ColumnDTO) {
            Log.debug(DataEntityPropertiesPresenter.class, "Show properties for column entity.");
            setPropertyList(getPropertiesForColumn((ColumnDTO)newSelection));
        } else {
            Log.debug(DataEntityPropertiesPresenter.class, "Unknown selection, hiding properties display.");
            showProperties = false;
            setPropertyList(null);
        }
        this.view.setShown(showProperties);
    }

    /**
     * Push the data in the property table.
     * 
     * @param properties the properties displayed in the table
     */
    private void setPropertyList(final List<Property> properties) {
        this.dataProvider.getList().clear();
        if (properties != null) {
            this.dataProvider.getList().addAll(properties);
        }
    }

    /**
     * Create the part of the properties to be fidplayed that are common to all database object types.
     * 
     * @param entityDTO the database entity object
     * @return the common properties
     */
    private List<Property> getCommonProperties(final DatabaseMetadataEntityDTO entityDTO) {
        List<Property> result = new ArrayList<Property>();
        result.add(new Property(constants.objectNameLabel(), entityDTO.getName()));
        result.add(new Property(constants.commentLabel(), entityDTO.getComment()));
        return result;
    }

    /**
     * Build the table data for a database display.
     * 
     * @param schemaDTO the database object
     * @return the table data
     */
    private List<Property> getPropertiesForDatabase(final DatabaseDTO databaseDTO) {
        List<Property> result = getCommonProperties(databaseDTO);
        result.add(new Property(constants.objectTypeLabel(), constants.objectTypeDatabase()));
        result.add(new Property(constants.productNameLabel(), databaseDTO.getDatabaseProductName()));
        result.add(new Property(constants.productVersionLabel(), databaseDTO.getDatabaseProductVersion()));
        result.add(new Property(constants.usernameLabel(), databaseDTO.getUserName()));
        result.add(new Property(constants.schemaCountLabel(), Integer.toString(databaseDTO.getSchemas().size())));
        return result;
    }

    /**
     * Build the table data for a schema display.
     * 
     * @param schemaDTO the schema object
     * @return the table data
     */
    private List<Property> getPropertiesForSchema(final SchemaDTO schemaDTO) {
        List<Property> result = getCommonProperties(schemaDTO);
        result.add(new Property(constants.objectTypeLabel(), constants.objectTypeSchema()));
        result.add(new Property(constants.tableCountLabel(), Integer.toString(schemaDTO.getTables().size())));
        return result;
    }

    /**
     * Build the table data for a table display.
     * 
     * @param tableDTO the table object
     * @return the table data
     */
    private List<Property> getPropertiesForTable(final TableDTO tableDTO) {
        List<Property> result = getCommonProperties(tableDTO);
        result.add(new Property(constants.objectTypeLabel(), constants.objectTypeTable()));
        result.add(new Property(constants.tableTypeLabel(), tableDTO.getType()));
        String primaryKeyDisplay = constants.noValue();
        List<String> primaryKey = tableDTO.getPrimaryKey();
        if (primaryKey != null && !primaryKey.isEmpty()) {
            primaryKeyDisplay = formatPrimaryKey(primaryKey);
        }
        result.add(new Property(constants.primaryKeyLabel(), primaryKeyDisplay));
        result.add(new Property(constants.columnCountLabel(), Integer.toString(tableDTO.getColumns().size())));
        return result;
    }

    private String formatPrimaryKey(List<String> primaryKey) {
        if (primaryKey.size() == 1) {
            return primaryKey.get(0);
        } else if (primaryKey.size() > 1) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < primaryKey.size(); i++) {
                sb.append(primaryKey.get(i));
                if (i < primaryKey.size()) {
                    sb.append(",");
                }
            }
            sb.append("]");
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * Build the table data for a column display.
     * 
     * @param columnDTO the column object
     * @return the table data
     */
    private List<Property> getPropertiesForColumn(final ColumnDTO columnDTO) {
        final List<Property> result = getCommonProperties(columnDTO);
        result.add(new Property(constants.objectTypeLabel(), constants.objectTypeColumn()));
        result.add(new Property(constants.dataTypeLabel(), columnDTO.getColumnDataType()));
        result.add(new Property(constants.columnSizeLabel(), Integer.toString(columnDTO.getDataSize())));
        result.add(new Property(constants.decimalDigitsLabel(), Integer.toString(columnDTO.getDecimalDigits())));
        result.add(new Property(constants.nullableLabel(), Boolean.toString(columnDTO.getNullable())));
        result.add(new Property(constants.ordinalPositionLabel(), Integer.toString(columnDTO.getOrdinalPositionInTable())));
        result.add(new Property(constants.defaultValueLabel(), columnDTO.getDefaultValue()));
        return result;
    }

    @Override
    public void onDatabaseInfoReceived(final DatabaseInfoReceivedEvent event) {
        if (this.selectedDatabaseId != null && this.selectedDatabaseId.equals(event.getDatabaseId())) {
            Log.debug(DataEntityPropertiesPresenter.class, "Metadata info received for currently selected datasource "
                                                           + this.selectedDatabaseId + " - updating display.");
            final DatabaseDTO dbInfo = this.databaseInfoStore.getDatabaseInfo(this.selectedDatabaseId);
            updateDisplay(dbInfo);
        }
    }

    @Override
    public void onDatabaseInfoError(final DatabaseInfoErrorEvent event) {
        // Keep old database info or show an empty tree ?
        if (this.selectedDatabaseId != null && this.selectedDatabaseId.equals(event.getDatabaseId())) {
            updateDisplay(null);
        }
    }

    @Override
    public void onSelectedDatasourceChange(final SelectedDatasourceChangeEvent event) {
        this.selectedDatabaseId = event.getSelectedDatasourceId();
        final DatabaseDTO metadata = this.databaseInfoStore.getDatabaseInfo(this.selectedDatabaseId);
        updateDisplay(metadata);
    }
}
