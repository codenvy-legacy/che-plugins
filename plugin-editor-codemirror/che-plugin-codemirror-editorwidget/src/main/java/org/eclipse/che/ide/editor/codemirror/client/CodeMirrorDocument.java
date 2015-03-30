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

import static org.eclipse.che.ide.editor.codemirrorjso.client.EventTypes.BEFORE_CHANGE;
import static org.eclipse.che.ide.editor.codemirrorjso.client.EventTypes.CHANGE;

import org.eclipse.che.ide.api.text.Region;
import org.eclipse.che.ide.editor.codemirrorjso.client.CMDocumentOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.CMPositionOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.CMSetSelectionOptions;
import org.eclipse.che.ide.editor.codemirrorjso.client.CodeMirrorOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.EventHandlers;
import org.eclipse.che.ide.editor.codemirrorjso.client.event.CMBeforeChangeEventOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.event.CMChangeEventOverlay;
import org.eclipse.che.ide.jseditor.client.changeintercept.TextChange;
import org.eclipse.che.ide.jseditor.client.document.AbstractEmbeddedDocument;
import org.eclipse.che.ide.jseditor.client.document.EmbeddedDocument;
import org.eclipse.che.ide.jseditor.client.events.CursorActivityHandler;
import org.eclipse.che.ide.jseditor.client.events.DocumentChangeEvent;
import org.eclipse.che.ide.jseditor.client.events.HasCursorActivityHandlers;
import org.eclipse.che.ide.jseditor.client.events.TextChangeEvent;
import org.eclipse.che.ide.jseditor.client.events.TextChangeEvent.ChangeUpdater;
import org.eclipse.che.ide.jseditor.client.text.LinearRange;
import org.eclipse.che.ide.jseditor.client.text.TextPosition;
import org.eclipse.che.ide.jseditor.client.text.TextRange;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * CodeMirror implementation of {@link EmbeddedDocument}.
 *
 * @author "Mickaël Leduque"
 */
public class CodeMirrorDocument extends AbstractEmbeddedDocument {

    /** The internal document representation for CodeMirror. */
    private final CMDocumentOverlay         documentOverlay;

    private final HasCursorActivityHandlers hasCursorActivityHandlers;

    public CodeMirrorDocument(final CMDocumentOverlay documentOverlay,
                              final CodeMirrorOverlay codeMirror,
                              final HasCursorActivityHandlers hasCursorActivityHandlers) {
        this.documentOverlay = documentOverlay;
        this.hasCursorActivityHandlers = hasCursorActivityHandlers;

        codeMirror.on(this.documentOverlay, CHANGE, new EventHandlers.EventHandlerMixedParameters() {
            @Override
            public void onEvent(final JsArrayMixed params) {

                // first parameter is editor instance, second is the change
                final CMChangeEventOverlay change = params.getObject(1);
                fireDocumentChangeEvent(change);
            }
        });
        codeMirror.on(this.documentOverlay, BEFORE_CHANGE, new EventHandlers.EventHandlerMixedParameters() {
            @Override
            public void onEvent(final JsArrayMixed params) {

                // first parameter is editor instance, second is the change
                final CMBeforeChangeEventOverlay change = params.getObject(1);
                // undo/redo changes are not modifiable
                if (change.hasUpdate()) {
                    fireTextChangeEvent(change);
                }
            }
        });
    }

    private void fireDocumentChangeEvent(final CMChangeEventOverlay param) {
        int startOffset = 0;
        if (param.getFrom() != null) {
            startOffset = this.documentOverlay.indexFromPos(param.getFrom());
        }
        int endOffset;
        if (param.getTo() != null) {
            endOffset = this.documentOverlay.indexFromPos(param.getTo());
        } else {
            endOffset = this.documentOverlay.getValue().length();
        }
        final int length = endOffset - startOffset;
        final String text = param.getText().join("\n");

        final DocumentChangeEvent event = new DocumentChangeEvent(this, startOffset, length, text);
        getDocEventBus().fireEvent(event);
    }

    private void fireTextChangeEvent(final CMBeforeChangeEventOverlay param) {
        final TextPosition from = new TextPosition(param.getFrom().getLine(), param.getFrom().getCharacter());
        final TextPosition to = new TextPosition(param.getTo().getLine(), param.getTo().getCharacter());
        final TextChange change = new TextChange.Builder().from(from)
                                                            .to(to)
                                                            .insert(param.getText().join("\n"))
                                                            .build();
        final TextChangeEvent event = new TextChangeEvent(change, new ChangeUpdater() {
            @Override
            public void update(TextChange updatedChange) {
                final CMPositionOverlay from = CMPositionOverlay.create(updatedChange.getFrom().getLine(),
                                                                        updatedChange.getFrom().getCharacter());
                final CMPositionOverlay to = CMPositionOverlay.create(updatedChange.getTo().getLine(),
                                                                      updatedChange.getTo().getCharacter());
                final String newText = updatedChange.getNewText();
                final String[] split = newText.split("\n");
                final JsArrayString text = JavaScriptObject.createArray().cast();
                for (final String s: split) {
                    text.push(s);
                }
                param.update(from, to, text);
            }
        });
        getDocEventBus().fireEvent(event);
    }

    @Override
    public TextPosition getPositionFromIndex(int index) {
        final CMPositionOverlay pos = this.documentOverlay.posFromIndex(index);
        return new TextPosition(pos.getLine(), pos.getCharacter());
    }

    @Override
    public int getIndexFromPosition(TextPosition position) {
        return documentOverlay.indexFromPos(CMPositionOverlay.create(position.getLine(), position.getCharacter()));
    }

    @Override
    public void setCursorPosition(TextPosition position) {
        this.documentOverlay.setCursor(position.getLine(), position.getCharacter());
    }

    @Override
    public TextPosition getCursorPosition() {
        final CMPositionOverlay pos = this.documentOverlay.getCursor();
        return new TextPosition(pos.getLine(), pos.getCharacter());
    }

    public int getCursorOffset() {
        final CMPositionOverlay pos = this.documentOverlay.getCursor();
        return this.documentOverlay.indexFromPos(pos);
    }

    public TextRange getSelectedTextRange() {
        final CMPositionOverlay from = this.documentOverlay.getCursorFrom();
        final CMPositionOverlay to = this.documentOverlay.getCursorTo();
        return new TextRange(new TextPosition(from.getLine(), from.getCharacter()),
                             new TextPosition(to.getLine(), to.getCharacter()));
    }

    public LinearRange getSelectedLinearRange() {
        final CMPositionOverlay from = this.documentOverlay.getCursorFrom();
        final CMPositionOverlay to = this.documentOverlay.getCursorTo();
        final int fromOffset = this.documentOverlay.indexFromPos(from);
        final int toOffset = this.documentOverlay.indexFromPos(to);
        return LinearRange.createWithStart(fromOffset).andEnd(toOffset);
    }

    @Override
    public void setSelectedRange(final TextRange range, final boolean show) {
        CMPositionOverlay from = CMPositionOverlay.create(range.getFrom().getLine(), range.getFrom().getCharacter());
        CMPositionOverlay to = CMPositionOverlay.create(range.getTo().getLine(), range.getTo().getCharacter());
        setSelectedRange(from, to, show);
    }

    @Override
    public void setSelectedRange(final LinearRange range, final boolean show) {
        final CMPositionOverlay from = this.documentOverlay.posFromIndex(range.getStartOffset());
        final CMPositionOverlay to = this.documentOverlay.posFromIndex(range.getStartOffset() + range.getLength());
        setSelectedRange(from, to, show);
    }

    /**
     * Sets the selected range.
     * @param from the start osition of the range
     * @param to the end position of the range
     * @param show whether to show the new selection
     */
    private void setSelectedRange(final CMPositionOverlay from, final CMPositionOverlay to, final boolean show) {
        if (show) {
            this.documentOverlay.setSelection(from, to, CMSetSelectionOptions.create());
        } else {
            this.documentOverlay.setSelection(from, to, CMSetSelectionOptions.createNoScroll());
        }
    }

    @Override
    public int getLineCount() {
        return this.documentOverlay.lineCount();
    }

    @Override
    public HandlerRegistration addCursorHandler(final CursorActivityHandler handler) {
        return this.hasCursorActivityHandlers.addCursorActivityHandler(handler);
    }

    @Override
    public String getContents() {
        return this.documentOverlay.getValue();
    }

    public String getContentRange(final int offset, final int length) {
        final CMPositionOverlay start = this.documentOverlay.posFromIndex(offset);
        final CMPositionOverlay end = this.documentOverlay.posFromIndex(offset + length);
        return this.documentOverlay.getRange(start, end);
    }

    public String getContentRange(final TextRange range) {
        CMPositionOverlay from = CMPositionOverlay.create(range.getFrom().getLine(), range.getFrom().getCharacter());
        CMPositionOverlay to = CMPositionOverlay.create(range.getTo().getLine(), range.getTo().getCharacter());
        return this.documentOverlay.getRange(from, to);
    }

    public void replace(final Region region, final String text) {
        final CMPositionOverlay fromPos = this.documentOverlay.posFromIndex(region.getOffset());
        final CMPositionOverlay toPos = this.documentOverlay.posFromIndex(region.getOffset() + region.getLength());

        this.documentOverlay.replaceRange(text, fromPos, toPos);
    }

    public int getContentsCharCount() {
        // same as last offset
        final int lastLine = this.documentOverlay.lastLine();
        final String lineContent = this.documentOverlay.getLine(lastLine);
        final int lineSize = lineContent.length();
        // zero based char position on the line
        return this.documentOverlay.indexFromPos(CMPositionOverlay.create(lastLine, lineSize - 1));
    }

    public String getLineContent(final int line) {
        return this.documentOverlay.getLine(line);
    }

    public TextRange getTextRangeForLine(final int line) {
        final String content = this.documentOverlay.getLine(line);
        return new TextRange(new TextPosition(line, 0), new TextPosition(line, content.length() - 1));
    }

    public LinearRange getLinearRangeForLine(final int line) {
        final String content = this.documentOverlay.getLine(line);
        int start = this.documentOverlay.indexFromPos(CMPositionOverlay.create(line, 0));
        return LinearRange.createWithStart(start).andLength(content.length());
    }
}
