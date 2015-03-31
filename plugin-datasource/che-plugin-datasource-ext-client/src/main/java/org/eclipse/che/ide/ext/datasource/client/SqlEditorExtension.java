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

import static org.eclipse.che.ide.api.action.IdeActions.GROUP_FILE_NEW;

import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.editor.EditorRegistry;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.filetypes.FileTypeRegistry;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.ext.datasource.client.action.NewSqlFileAction;
import org.eclipse.che.ide.ext.datasource.client.sqleditor.SqlEditorResources;
import org.eclipse.che.ide.ext.datasource.client.sqllauncher.SqlLauncherEditorProvider;
import org.eclipse.che.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Extension definition for the sql editor.
 */
@Singleton
@Extension(title = "SQL Editor", version = "1.0.0")
public class SqlEditorExtension {

    public static final String GENERIC_SQL_MIME_TYPE = "text/x-sql";
    public static final String ORACLE_SQL_MIME_TYPE  = "text/x-plsql";
    public static final String MYSQL_SQL_MIME_TYPE   = "text/x-mysql";
    public static final String MSSQL_SQL_MIME_TYPE   = "text/x-mssql";

    public static final String SQL_FILE_EXTENSION    = "sql";

    @Inject
    public SqlEditorExtension(final WorkspaceAgent workspaceAgent,
                              final ActionManager actionManager,
                              final SqlEditorResources sqlEditorResources,
                              final FileTypeRegistry fileTypeRegistry,
                              final EditorRegistry editorRegistry,
                              final SqlLauncherEditorProvider sqlEditorProvider,
                              final NewSqlFileAction newSqlFileAction,
                              final IconRegistry iconRegistry,
                              @Named("SQLFileType") final FileType sqlFile) {

        Log.debug(SqlEditorExtension.class, "Initialization of SQL editor extension.");

        fileTypeRegistry.registerFileType(sqlFile);
        editorRegistry.register(sqlFile, sqlEditorProvider);

        // add action for creating new SQL file in "File-New" submenu
        DefaultActionGroup newGroup = (DefaultActionGroup)actionManager.getAction(GROUP_FILE_NEW);
        newGroup.addSeparator();
        actionManager.registerAction("newSqlFileAction", newSqlFileAction);
        newGroup.add(newSqlFileAction);

        // register the sql file icon
        iconRegistry.registerIcon(new Icon("default.sqlfile.icon", "org/eclipse/che/ide/ext/datasource/client/sqleditor/sql-icon.png"));
    }

}
