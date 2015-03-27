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

import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.ext.datasource.client.sqleditor.codeassist.SqlCodeAssistProcessor;
import org.eclipse.che.ide.ext.datasource.client.store.DatabaseInfoOracle;
import org.eclipse.che.ide.jseditor.client.codeassist.CodeAssistProcessor;
import org.eclipse.che.ide.jseditor.client.editorconfig.DefaultTextEditorConfiguration;
import org.eclipse.che.ide.jseditor.client.partition.DocumentPartitioner;
import org.eclipse.che.ide.jseditor.client.texteditor.ConfigurableTextEditor;

public class SqlEditorConfiguration extends DefaultTextEditorConfiguration {

    private SqlCodeAssistProcessor codeAssistProcessor;
    private final SqlEditorResources resource;
    private final DatabaseInfoOracle databaseInfoOracle;
    private final ConfigurableTextEditor textEditor;
    private final EditorDatasourceOracle editorDatasourceOracle;

    public SqlEditorConfiguration(@NotNull final ConfigurableTextEditor textEditor,
                                  @NotNull final SqlEditorResources resource,
                                  @NotNull final DatabaseInfoOracle databaseInfoOracle,
                                  @NotNull final EditorDatasourceOracle editorDatasourceOracle) {
        this.textEditor = textEditor;
        this.resource = resource;
        this.databaseInfoOracle = databaseInfoOracle;
        this.editorDatasourceOracle = editorDatasourceOracle;
    }

    @Override
    public StringMap<CodeAssistProcessor> getContentAssistantProcessors() {
        StringMap<CodeAssistProcessor> map = Collections.createStringMap();
        map.put(DocumentPartitioner.DEFAULT_CONTENT_TYPE, getOrCreateCodeAssistProcessor());
        return map;
    }

    private SqlCodeAssistProcessor getOrCreateCodeAssistProcessor() {
        if (codeAssistProcessor == null) {
            codeAssistProcessor = new SqlCodeAssistProcessor(textEditor, resource, databaseInfoOracle, editorDatasourceOracle);
        }
        return codeAssistProcessor;
    }
}
