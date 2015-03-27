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

import org.eclipse.che.ide.editor.codemirrorjso.client.CMEditorOverlay;
import org.eclipse.che.ide.jseditor.client.texteditor.LineStyler;

/** Implementation of {@link LineStyler} for codemirror. */
public class CodeMirrorLineStyler implements LineStyler {

    // see the addLineClass javadoc for the meaning of wrap
    /** Set the style on the whole line, including gutter. */
    private static final String STYLE_LOCATION_BACKGROUND = "background";

    /** The editor object. */
    private final CMEditorOverlay editorOverlay;

    public CodeMirrorLineStyler(final CMEditorOverlay editorOverlay) {
        this.editorOverlay = editorOverlay;
    }

    @Override
    public void addLineStyles(final int lineNumber, final String... styles) {
        for (final String classname: styles) {
            this.editorOverlay.addLineClass(lineNumber, STYLE_LOCATION_BACKGROUND, classname);
        }
    }

    @Override
    public void removeLineStyles(final int lineNumber, final String... styles) {
        for (final String classname: styles) {
            this.editorOverlay.removeLineClass(lineNumber, STYLE_LOCATION_BACKGROUND, classname);
        }
    }

    @Override
    public void clearLineStyles(final int lineNumber) {
        this.editorOverlay.removeLineClass(lineNumber, STYLE_LOCATION_BACKGROUND);
    }

}
