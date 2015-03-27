/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.angularjs.core.client.editor;


import org.eclipse.che.ide.jseditor.client.texteditor.TextEditor;

/**
 * Store data for Angular JS codeassist processor.
 * 
 * @author Florent Benoit
 */
public class InvocationContext {
    private final AngularJSQuery query;

    private final int offset;

    private final AngularJSResources resources;

    private final TextEditor editor;

    public InvocationContext(AngularJSQuery query, int offset, AngularJSResources resources, TextEditor editor) {
        super();
        this.query = query;
        this.offset = offset;
        this.resources = resources;
        this.editor = editor;
    }

    public AngularJSQuery getQuery() {
        return query;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @return the resourcess
     */
    public AngularJSResources getResources() {
        return resources;
    }

    /**
     * @return the editor
     */
    public TextEditor getEditor() {
        return editor;
    }
}
