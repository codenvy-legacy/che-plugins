/*******************************************************************************
 * Copyright (c) 2014-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.editor.codemirror.client;

import org.eclipse.che.ide.api.texteditor.HandlesUndoRedo;
import org.eclipse.che.ide.editor.codemirrorjso.client.CMDocumentOverlay;

/**
 * Undo/redo handler for codemirror editors.
 */
class CodeMirrorUndoRedo implements HandlesUndoRedo {

    /** The document. */
    private final CMDocumentOverlay document;

    public CodeMirrorUndoRedo(final CMDocumentOverlay document) {
        this.document = document;
    }

    @Override
    public boolean redoable() {
        return (this.document.historyRedoSize() > 0);
    }

    @Override
    public boolean undoable() {
        return (this.document.historyUndoSize() > 0);
    }

    @Override
    public void redo() {
        this.document.redo();
    }

    @Override
    public void undo() {
        this.document.undo();
    }
}
