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
package org.eclipse.che.ide.ext.datasource.client.editdatasource.celllist;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.ext.datasource.client.DatasourceUiResources;
import org.eclipse.che.ide.ext.datasource.client.DatasourceUiResources.DatasourceUiStyle;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.connector.NewDatasourceConnector;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.connector.NewDatasourceConnectorAgent;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.inject.Inject;

/**
 * Display of a datasource in the edit/delete dialog list.
 * 
 * @author "Mickaël Leduque"
 */
public class DatasourceCell extends AbstractCell<DatabaseConfigurationDTO> {

    private final DatasourceUiStyle           style;
    private final CellTemplate                template;
    private final NewDatasourceConnectorAgent connectorAgent;

    @Inject
    public DatasourceCell(final @NotNull DatasourceUiResources resource,
                          final @NotNull CellTemplate template,
                          final @NotNull NewDatasourceConnectorAgent connectorAgent) {
        this.style = resource.datasourceUiCSS();
        this.template = template;
        this.connectorAgent = connectorAgent;
    }

    @Override
    public void render(final Context context, final DatabaseConfigurationDTO value, final SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        final SafeHtml id = SafeHtmlUtils.fromString(value.getDatasourceId());
        final String connectorId = value.getConfigurationConnectorId();

        // for old datasources, the connector id was not recorded

        NewDatasourceConnector connector = this.connectorAgent.getConnector(connectorId);
        String dbType;
        if (connector != null) {
            dbType = connector.getTitle();
        } else {
            dbType = value.getDatabaseType().toString();
        }

        final SafeHtml type = SafeHtmlUtils.fromString(dbType);

        sb.append(this.template.datasourceItem(id,
                                               this.style.datasourceIdStyle(),
                                               type,
                                               this.style.datasourceTypeStyle(),
                                               this.style.datasourceIdCellStyle(),
                                               this.style.datasourceTypeCellStyle()));
    }

    /**
     * {@link SafeHtml} template for the datasource cell.
     * 
     * @author "Mickaël Leduque"
     */
    interface CellTemplate extends SafeHtmlTemplates {

        @Template("<table><tr><td class='{4}'> <span class='{1}'>{0}</span> </td><td class='{5}'> <span class='{3}'>{2}</span> </td></tr></table>")
        SafeHtml datasourceItem(SafeHtml datasourceId,
                                String idStyle,
                                SafeHtml datasourceType,
                                String typeStyle,
                                String idCellStyle,
                                String typeCellStyle);
    }
}
