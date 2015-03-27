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

import org.eclipse.che.ide.ext.datasource.client.DatasourceUiResources;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.inject.Inject;

/**
 * Implementation of the view for data item properties display.
 * 
 * @author Mickaël LEDUQUE
 */
public class DataEntityPropertiesViewImpl extends Composite implements DataEntityPropertiesView {

    /** The action delegate for the view. */
    private ActionDelegate delegate;

    @UiField(provided = true)
    CellTable<Property>    propertiesDisplay;

    @UiField(provided = true)
    DatasourceUiResources  datasourceUiResources;

    @Inject
    public DataEntityPropertiesViewImpl(final DataEntityPropertiesViewUiBinder uiBinder,
                                        final CellTableResourcesProperties cellTableResources,
                                        final DatasourceUiResources datasourceUiResources) {
        this.propertiesDisplay = new CellTable<Property>(15, cellTableResources);
        this.propertiesDisplay.setWidth("100%", true);

        this.datasourceUiResources = datasourceUiResources;
        propertiesDisplay.addColumn(new TextColumn<Property>() {

            @Override
            public String getValue(final Property property) {
                return property.getName();
            }

            @Override
            public String getCellStyleNames(final Context context, final Property object) {
                return datasourceUiResources.datasourceUiCSS().propertiesTableFirstColumn();
            }
        });
        propertiesDisplay.addColumn(new TextColumn<Property>() {

            @Override
            public String getValue(final Property property) {
                return property.getValue(); // goes through a SafeHtmlRenderer
            }
        });
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void bindDataProvider(final AbstractDataProvider<Property> dataProvider) {
        dataProvider.addDataDisplay(this.propertiesDisplay);
    }

    @Override
    public void setDelegate(final ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setShown(final boolean shown) {
        this.propertiesDisplay.setVisible(shown);
    }

    /**
     * The UiBinder interface for this widget.
     * 
     * @author "Mickaël Leduque"
     */
    interface DataEntityPropertiesViewUiBinder extends UiBinder<Widget, DataEntityPropertiesViewImpl> {
    }

    interface PropertiesStyle extends CssResource {
        String keyColumnText();

        String valueColumnText();
    }
}
