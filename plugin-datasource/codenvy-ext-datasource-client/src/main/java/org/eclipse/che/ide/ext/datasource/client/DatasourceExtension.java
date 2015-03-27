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
package org.eclipse.che.ide.ext.datasource.client;


import static org.eclipse.che.ide.api.action.IdeActions.GROUP_MAIN_MENU;
import static org.eclipse.che.ide.api.action.IdeActions.GROUP_WINDOW;
import static org.eclipse.che.ide.api.constraints.Anchor.BEFORE;

import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.keybinding.KeyBindingAgent;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.ext.datasource.client.action.EditDatasourcesAction;
import org.eclipse.che.ide.ext.datasource.client.explorer.DatasourceExplorerPartPresenter;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.NewDatasourceWizardAction;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.connector.NewDatasourceConnectorAgent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Extension definition for the datasource plugin.
 */
@Singleton
@Extension(title = "Datasource", version = "1.0.0")
public class DatasourceExtension {

    public static boolean       SHOW_ITEM                  = true;
    public static final String  DS_GROUP_MAIN_MENU         = "datasourceMainMenu";

    @Inject
    public DatasourceExtension(WorkspaceAgent workspaceAgent,
                               DatasourceExplorerPartPresenter dsExplorer,
                               ActionManager actionManager,
                               NewDatasourceWizardAction newDSConnectionAction, 
                               ConnectorsInitializer connectorsInitializer,
                               NewDatasourceConnectorAgent connectorAgent,
                               DatasourceUiResources resources,
                               AvailableJdbcDriversService availableJdbcDrivers,
                               EditDatasourcesAction editDatasourcesAction,
                               KeyBindingAgent keyBindingAgent) {

        workspaceAgent.openPart(dsExplorer, PartStackType.NAVIGATION);

        // create de "Datasource" menu in menubar and insert it
        DefaultActionGroup mainMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_MENU);
        DefaultActionGroup defaultDatasourceMainGroup = new DefaultActionGroup("Datasource", true, actionManager);
        actionManager.registerAction(DS_GROUP_MAIN_MENU, defaultDatasourceMainGroup);
        Constraints beforeWindow = new Constraints(BEFORE, GROUP_WINDOW);
        mainMenu.add(defaultDatasourceMainGroup, beforeWindow);

        // add submenu "New datasource" to Datasource menu
        actionManager.registerAction("newDSConnection", newDSConnectionAction);
        defaultDatasourceMainGroup.add(newDSConnectionAction);

        // do after adding new datasource page provider to keep page order
        connectorsInitializer.initConnectors();

        // add submenu "Edit datasource" to Datasource menu
        actionManager.registerAction("editDSConnections", editDatasourcesAction);
        defaultDatasourceMainGroup.add(editDatasourcesAction);

        // fetching available drivers list from the server
        availableJdbcDrivers.fetch();

        // inject CSS
        resources.datasourceUiCSS().ensureInjected();
    }
}
