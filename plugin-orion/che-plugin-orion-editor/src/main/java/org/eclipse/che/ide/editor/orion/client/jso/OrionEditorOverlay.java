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
package org.eclipse.che.ide.editor.orion.client.jso;

import com.google.gwt.core.client.JavaScriptObject;

public class OrionEditorOverlay extends JavaScriptObject {

    protected OrionEditorOverlay() {
    }

    public final native String getText() /*-{
        return this.getText();
    }-*/;

    public final native void setText(final String newValue) /*-{
        this.setText(newValue);
    }-*/;

    public final native OrionTextViewOverlay getTextView() /*-{
        return this.getTextView();
    }-*/;

    /** Returns the content assist of the editor. */
    public final native OrionContentAssistOverlay getContentAssist() /*-{
        return this.getContentAssist();
    }-*/;

    public final native void focus() /*-{
        this.focus();
    }-*/;

    public final native OrionTextModelOverlay getModel() /*-{
        return this.getModel();
    }-*/;

    public final native void setCaretOffset(int offset) /*-{
        this.setCaretOffset(offset, true);
    }-*/;

    public final native int getCaretOffset() /*-{
        return this.getCaretOffset();
    }-*/;

    public final native void setText(String text, int start, int end) /*-{
        this.setText(text, start, end);
    }-*/;

    public final native OrionUndoStackOverlay getUndoStack() /*-{
        return this.getUndoStack();
    }-*/;

    public final native OrionSelectionOverlay getSelection() /*-{
        return this.getSelection();
    }-*/;

    /**
     * Sets the selection.
     * @param start offset of the start of range
     * @param end offset of the end of range (can be before the start)
     */
    public final native void setSelection(int start, int end) /*-{
        this.setSelection(start, end);
    }-*/;

    /**
     * Sets the selection.
     * @param start offset of the start of range
     * @param end offset of the end of range (can be before the start)
     * @param show scroll to show the range iff the value is true
     */
    public final native void setSelection(int start, int end, boolean show) /*-{
        this.setSelection(start, end, show);
    }-*/;

    /**
     * Sets the selection.
     * @param start offset of the start of range
     * @param end offset of the end of range (can be before the start)
     * @param show additional percentage ([0,1] that must also be shown
     */
    public final native void setSelection(int start, int end, double show) /*-{
        this.setSelection(start, end, show);
    }-*/;

    /**
     * Sets the selection.
     * @param start offset of the start of range
     * @param end offset of the end of range (can be before the start)
     * @param options an option object
     */
    public final native void setSelection(int start, int end, OrionTextViewShowOptionsOverlay options) /*-{
        this.setSelection(start, end, show);
    }-*/;

    public final native boolean isDirty() /*-{
        return this.isDirty();
    }-*/;

    public final native void setDirty(final boolean newValue) /*-{
        this.setDirty(newValue);
    }-*/;

    /**
     * Report the message to the user.
     * @param message the message
     */
    public final native void reportStatus(String message) /*-{
        this.reportStatus(message);
    }-*/;

    /**
     * Report the message to the user.
     * @param message the message
     * @param type either normal or "progress" or "error";
     */
    public final native void reportStatus(String message, String type) /*-{
        this.reportStatus(message, type);
    }-*/;

    /**
     * Report the message to the user.
     * @param message the message
     * @param type either normal or "progress" or "error"
     * @param accessible if true, a screen reader will read this message
     */
    public final native void reportStatus(String message, String type, boolean accessible) /*-{
        this.reportStatus(message, type, accessible);
    }-*/;
}
