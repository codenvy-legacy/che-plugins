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

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;

/**
 * Internationalizable messages for the "Manage Datasource"-related dialogs.
 * 
 * @author "Mickaël Leduque"
 */
@DefaultLocale("en")
public interface EditDatasourceMessages extends Messages {

    @DefaultMessage("Create Datasource")
    String createButtonText();

    @DefaultMessage("Edit Datasource")
    String editButtonText();

    @DefaultMessage("Delete Datasources")
    String deleteButtonText();

    @DefaultMessage("Close")
    String closeButtonText();

    @DefaultMessage("Manage Datasources")
    String editDatasourcesMenuText();

    @DefaultMessage("Create, edit and delete datasources")
    String editDatasourcesMenuDescription();

    @DefaultMessage("Manage datasources")
    String editDatasourcesDialogText();

    @DefaultMessage("Datasources")
    String datasourcesListLabel();

    @DefaultMessage("More than one datasource selected")
    String editMultipleSelectionTitle();

    @DefaultMessage("Cannot edit more than one datasource.")
    String editMultipleSelectionMessage();

    @DefaultMessage("Select the datasources to delete.")
    String deleteNoSelectionMessage();

    @DefaultMessage("Select the datasource to edit.")
    String editNoSelectionMessage();

    @DefaultMessage("No datasource selected")
    String editOrDeleteNoSelectionTitle();

    @DefaultMessage("Confirm datasource deletion")
    String confirmDeleteDatasourcesTitle();

    @DefaultMessage("Delete these {0} datasources ?")
    @AlternateMessage({"one", "Delete the selected datasource ?"})
    String confirmDeleteDatasources(@PluralCount int count);

    @DefaultMessage("No datasources configured")
    String emptyDatasourceList();
}
