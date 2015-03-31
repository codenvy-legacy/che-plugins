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
package org.eclipse.che.ide.ext.datasource.client.sqleditor.codeassist;

import org.eclipse.che.ide.ext.datasource.client.sqleditor.SqlEditorResources;
import org.eclipse.che.ide.jseditor.client.texteditor.TextEditor;

public class InvocationContext {
    private final SqlCodeQuery query;

    private final int offset;

    private final SqlEditorResources resources;

    private final TextEditor editor;

    public InvocationContext(SqlCodeQuery query, int offset, SqlEditorResources resources, TextEditor editor) {
        super();
        this.query = query;
        this.offset = offset;
        this.resources = resources;
        this.editor = editor;
    }

    public SqlCodeQuery getQuery() {
        return query;
    }

    public int getOffset() {
        return offset;
    }

    public SqlEditorResources getResources() {
        return resources;
    }

    public TextEditor getEditor() {
        return editor;
    }
}
