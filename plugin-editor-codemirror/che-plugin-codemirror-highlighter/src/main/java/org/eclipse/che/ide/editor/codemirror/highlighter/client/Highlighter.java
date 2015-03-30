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
package org.eclipse.che.ide.editor.codemirror.highlighter.client;

import org.eclipse.che.ide.editor.codemirrorjso.client.CMEditorOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.CodeMirrorOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.options.CMEditorOptionsOverlay;

import elemental.dom.Element;

public class Highlighter {

    private final Element element;
    private final String mode;
    private final int tabSize;
    private final CodeMirrorOverlay codemirror;

    Highlighter(final Element element, final String mode, final int tabSize,
                final CodeMirrorOverlay codemirror) {
        this.element = element;
        this.mode = mode;
        this.tabSize = tabSize;
        this.codemirror = codemirror;
    }

    public void highlightText(final String text) {
        final CMEditorOptionsOverlay options = CMEditorOptionsOverlay.create();
        options.setTheme("codenvy");
        options.setScrollbarStyle("simple");
        options.setTabSize(this.tabSize);
        options.setMode(mode);
        options.setReadOnly(true);

        while (this.element.getChildElementCount() > 0) {
            this.element.removeChild(this.element.getFirstChild());
        }
        final CMEditorOverlay editor = codemirror.createEditor(this.element, options);
        editor.setValue(text);
        editor.setSize("100%", "100%");
    }
}
