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
package org.eclipse.che.ide.ext.datasource.client.sqleditor;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.api.editor.EditorProvider;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.datasource.client.store.DatabaseInfoOracle;
import org.eclipse.che.ide.jseditor.client.defaulteditor.DefaultEditorProvider;
import org.eclipse.che.ide.jseditor.client.texteditor.ConfigurableTextEditor;
import org.eclipse.che.ide.util.loging.Log;
import com.google.inject.Inject;

public class SqlEditorProvider implements EditorProvider {

    private final DefaultEditorProvider defaultEditorProvider;

    private final NotificationManager notificationManager;

    private final SqlEditorResources resource;

    private final DatabaseInfoOracle databaseInfoOracle;

    private final EditorDatasourceOracle editorDatasourceOracle;

    @Inject
    public SqlEditorProvider(@NotNull final DefaultEditorProvider defaultEditorProvider,
                             @NotNull final NotificationManager notificationManager,
                             @NotNull final DatabaseInfoOracle databaseInfoOracle,
                             @NotNull final EditorDatasourceOracle editorDatasourceOracle,
                             @NotNull final SqlEditorResources resource) {
        this.defaultEditorProvider = defaultEditorProvider;
        this.notificationManager = notificationManager;
        this.databaseInfoOracle = databaseInfoOracle;
        this.editorDatasourceOracle = editorDatasourceOracle;
        this.resource = resource;
    }

    @Override
    public String getId() {
        return "sqlEditor";
    }

    @Override
    public String getDescription() {
        return "SQL Editor";
    }

    @Override
    public ConfigurableTextEditor getEditor() {
        Log.debug(SqlEditorProvider.class, "New instance of SQL editor requested.");
        ConfigurableTextEditor textEditor = this.defaultEditorProvider.getEditor();
        textEditor.initialize(new SqlEditorConfiguration(textEditor, resource, databaseInfoOracle, editorDatasourceOracle),
                              notificationManager);
        return textEditor;
    }

}
