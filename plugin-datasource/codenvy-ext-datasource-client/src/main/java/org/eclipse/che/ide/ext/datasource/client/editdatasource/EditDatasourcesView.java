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
package org.eclipse.che.ide.ext.datasource.client.editdatasource;

import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.SelectionModel;

/**
 * The view interface for the edit/delete datasource dialog.
 * 
 * @author "Mickaël Leduque"
 */
public interface EditDatasourcesView {

    /** Show the edit/delete datasource dialog. */
    void showDialog();

    /** Close the edit/delete datasource dialog. */
    void closeDialog();

    /** Binds the datasource widget to the datasource list model. */
    void bindDatasourceModel(AbstractDataProvider<DatabaseConfigurationDTO> provider);

    /** Bind the datasource widget to the datasource selection model. */
    void bindSelectionModel(SelectionModel<DatabaseConfigurationDTO> selectionModel);

    /** Sets the view's action delegate. */
    void setDelegate(ActionDelegate delegate);

    /** Enables/disbales the edit datasources button. */
    void setEditEnabled(boolean enabled);

    /** Enables/disbales the delete datasources button. */
    void setDeleteEnabled(boolean enabled);

    /**
     * Interface for this view's action delegate.
     * 
     * @author "Mickaël Leduque"
     */
    public interface ActionDelegate {
        /** The dialog must be closed. */
        void closeDialog();

        /** Datasource deletion requested. */
        void deleteSelectedDatasources();

        /** Datasource edition requested. */
        void editSelectedDatasource();

        /** Datasource creation requested. */
        void createDatasource();
    }
}
