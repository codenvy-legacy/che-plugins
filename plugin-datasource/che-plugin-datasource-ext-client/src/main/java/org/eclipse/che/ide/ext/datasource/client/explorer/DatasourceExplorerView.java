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
package org.eclipse.che.ide.ext.datasource.client.explorer;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseMetadataEntityDTO;
import org.eclipse.che.ide.ext.datasource.shared.ExploreTableType;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Interface of datasource tree view.
 */
public interface DatasourceExplorerView extends
                                       View<DatasourceExplorerView.ActionDelegate> {

    /**
     * Sets items into tree.
     * 
     * @param resource The root resource item
     */
    void setItems(@NotNull DatabaseMetadataEntityDTO databaseMetadataEntyDTO);

    /**
     * Sets title of part.
     * 
     * @param title title of part
     */
    void setTitle(@NotNull String title);

    /**
     * Fills the datasource selection component.
     * 
     * @param datasourceIds the ids (keys) of the datasources
     */
    void setDatasourceList(Collection<String> datasourceIds);

    /**
     * Returns the component used for datasource properties display.
     * 
     * @return the properties display component
     */
    AcceptsOneWidget getPropertiesDisplayContainer();


    void setTableTypesList(Collection<String> tableTypes);

    void setTableTypes(ExploreTableType tableType);

    /**
     * The action delegate for this view.
     */
    public interface ActionDelegate extends BaseActionDelegate {
        /**
         * Performs any actions in response to node selection.
         * 
         * @param resource node
         */
        void onDatabaseMetadataEntitySelected(@NotNull DatabaseMetadataEntityDTO dbMetadataEntity);

        /**
         * Performs any actions in response to some node action.
         * 
         * @param resource node
         */
        void onDatabaseMetadataEntityAction(@NotNull DatabaseMetadataEntityDTO dbMetadataEntity);

        /**
         * Performs any actions appropriate in response to the user having clicked right button on mouse.
         * 
         * @param mouseX the mouse x-position within the browser window's client area.
         * @param mouseY the mouse y-position within the browser window's client area.
         */
        void onContextMenu(int mouseX, int mouseY);

        void onClickExploreButton(String datasourceId);

        void onSelectedDatasourceChanged(String datasourceId);

        void onSelectedTableTypesChanged(int selectedIndex);
    }
}
