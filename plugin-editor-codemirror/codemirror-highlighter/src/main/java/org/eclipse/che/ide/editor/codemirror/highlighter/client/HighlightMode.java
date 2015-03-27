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

import org.eclipse.che.ide.editor.codemirrorjso.client.CodeMirrorOverlay;

import elemental.dom.Element;
import elemental.js.dom.JsElement;

public class HighlightMode {

    private static final int DEFAULT_TAB_SIZE = 4;

    private final String mode;
    private final int tabSize;

    private final CodeMirrorOverlay codemirror;


    public HighlightMode(final String mode, final int tabSize, final CodeMirrorOverlay codemirror) {
        this.mode = mode;
        this.tabSize = tabSize;
        this.codemirror = codemirror;
    }

    public HighlightMode(final String mode, final CodeMirrorOverlay codemirror) {
        this(mode, DEFAULT_TAB_SIZE, codemirror);
    }

    public Highlighter forElement(final Element element) {
        return new Highlighter(element, this.mode, this.tabSize, this.codemirror);
    }

    public Highlighter forElement(final com.google.gwt.dom.client.Element element) {
        return forElement(element.<JsElement> cast());
    }
}
