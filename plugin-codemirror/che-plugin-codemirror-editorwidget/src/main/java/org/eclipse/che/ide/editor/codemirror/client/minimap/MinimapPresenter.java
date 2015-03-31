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
package org.eclipse.che.ide.editor.codemirror.client.minimap;

import org.eclipse.che.ide.jseditor.client.document.Document;
import org.eclipse.che.ide.jseditor.client.minimap.Minimap;
import org.eclipse.che.ide.jseditor.client.text.TextPosition;

/**
 * Implementation of miinimap for the right gutter.
 */
public class MinimapPresenter implements MinimapView.Delegate, Minimap {

    /**
     * The view.
     */
    private final MinimapView view;

    /**
     * The associated document.
     */
    private Document document;

    public MinimapPresenter(final MinimapView view) {
        this.view = view;
    }

    @Override
    public void handleClick(final double position) {
        if (this.document != null) {
            final int lines = this.document.getLineCount();
            long longLine = Math.round(lines * position);
            if (longLine > (long)Integer.MAX_VALUE) {
                throw new IllegalStateException("Can't handle line count");
            }
            int line = (int)longLine;
            this.document.setCursorPosition(new TextPosition(line, 0));
        }
    }

    public void setDocument(final Document document) {
        this.document = document;
    }

    public void addMark(final int line, final String style) {
        if (this.document != null) {
            final int lineCount = this.document.getLineCount();
            if (lineCount == 0) {
                return;
            }
            final double ratio = (double)line / lineCount;
            this.view.addMark(ratio, style, line);
        }
    }

    @Override
    public void addMark(final int line, final String style, final int level) {
        if (this.document != null) {
            final int lineCount = this.document.getLineCount();
            if (lineCount == 0) {
                return;
            }
            final double ratio = (double)line / lineCount;
            this.view.addMark(ratio, style, line, level);
        }
    }

    @Override
    public void removeMarks(final int lineStart, final int lineEnd) {
        this.view.removeMarks(lineStart, lineEnd);
    }

    public void clearMarks() {
        this.view.clearMarks();
    }

    @Override
    public void handleMarkClick(final int line) {
        if (this.document != null) {
            this.document.setCursorPosition(new TextPosition(line, 0));
        }
    }
}
