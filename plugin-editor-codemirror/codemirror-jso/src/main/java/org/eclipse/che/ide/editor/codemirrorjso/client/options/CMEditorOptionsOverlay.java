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
package org.eclipse.che.ide.editor.codemirrorjso.client.options;

import org.eclipse.che.ide.editor.codemirrorjso.client.CMDocumentOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.CMKeymapOverlay;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class CMEditorOptionsOverlay extends JavaScriptObject {

    private static final String READONLY_VALUE_NOFOCUS = "nofocus";

    protected CMEditorOptionsOverlay() {
    }

    public static final native CMEditorOptionsOverlay create() /*-{
        return {};
    }-*/;

    // two variants of setValue

    public final native void setValue(String contents) /*-{
        this.value = contents;
    }-*/;

    public final native void setValue(CMDocumentOverlay doc) /*-{
        this.value = doc;
    }-*/;

    public final native boolean valueIsString() /*-{
        return typeof (this.value) === "string";
    }-*/;

    public final native String getValueAsString() /*-{
        return this.value;
    }-*/;

    public final native CMDocumentOverlay getValueAsDocument() /*-{
        return this.value;
    }-*/;

    // two variants of setMode

    public final native void setMode(String mode) /*-{
        this.mode = mode;
    }-*/;

    public final native void setMode(JavaScriptObject mode) /*-{
        this.mode = mode;
    }-*/;

    public final native boolean modeIsString() /*-{
        return typeof (this.mode) === "string";
    }-*/;

    public final native String getModeAsString() /*-{
        return this.mode;
    }-*/;

    public final native JavaScriptObject getModeAsObject() /*-{
        return this.mode;
    }-*/;

    // theme

    public final native void setTheme(String theme) /*-{
        this.theme = theme;
    }-*/;

    public final native String getTheme() /*-{
        return this.theme;
    }-*/;

    // indent unit = number of spaces a bloc must be indented (default 2)

    public final native void setIndentUnit(int unit) /*-{
        this.indentUnit = unit;
    }-*/;

    public final native String getIndentUnit() /*-{
        return this.indentUnit;
    }-*/;

    // smart indent ? = if the context-sensitive indentation mus be used if available (default true)

    public final native void setSmartIndent(boolean smartIndent) /*-{
        this.smartIndent = smartIndent;
    }-*/;

    public final native boolean getSmartIndent() /*-{
        return this.smartIndent;
    }-*/;

    // tabSize = the tab width as spaces equivalence (default 4)

    public final native void setTabSize(int tabSize) /*-{
        this.tabSize = tabSize;
    }-*/;

    public final native int setTabSize() /*-{
        return this.tabSize;
    }-*/;

    // indentWithTabs = whether the first n*tabSize spaces should be replaced by n tabs (default false)

    public final native void setIndentWithTabs(boolean indentWithTabs) /*-{
        this.indentWithTabs = indentWithTabs;
    }-*/;

    public final native boolean getIndentWithTabs() /*-{
        return this.indentWithTabs;
    }-*/;

    // electricChars = whether the editor should re-indent the current line when a character is typed
    // that might change its proper indentation if the mode supports it (default true)

    public final native void setElectricChars(boolean electricChars) /*-{
        this.electricChars = electricChars;
    }-*/;

    public final native boolean getElectricChars() /*-{
        return this.electricChars;
    }-*/;

    // specialChars = regular expression used to determine which characters should be replaced by a special placeholder

    public final native void setSpecialChars(String specialChars) /*-{
        this.specialChars = specialChars;
    }-*/;

    public final native String getSpecialChars() /*-{
        return this.specialChars;
    }-*/;

    // specialCharPlaceholder - function that creates the visual representation for a specialChar (default: red dot with tooltip)

    public final native void setSpecialCharPlaceholder(CMSpecialCharPlaceHolder specialCharPlaceholder) /*-{
        this.specialCharPlaceholder = specialCharPlaceholder;
    }-*/;

    public final native CMSpecialCharPlaceHolder getSpecialCharPlaceholder() /*-{
        return this.specialCharPlaceholder;
    }-*/;

    // rtlMoveVisually = whether horizontal cursor movement through right-to-left is visual or logical (default false on windows, true else)

    public final native void setRtlMoveVisually(boolean rtlMoveVisually) /*-{
        this.rtlMoveVisually = rtlMoveVisually;
    }-*/;

    public final native boolean getRtlMoveVisually() /*-{
        return this.rtlMoveVisually;
    }-*/;

    // keyMap = key map to use (default: 'default')

    public final native void setKeyMap(String keyMap) /*-{
        this.keyMap = keyMap;
    }-*/;

    public final native String getKeyMap() /*-{
        return this.keyMap;
    }-*/;

    // extraKeys = additional key bindings, added to keymap (default: null)

    public final native void setExtraKeys(CMKeymapOverlay extraKeys) /*-{
        this.extraKeys = extraKeys;
    }-*/;

    public final native CMKeymapOverlay getExtraKeys() /*-{
        return this.extraKeys;
    }-*/;

    // lineWrapping = whether to wrap lines (true) or scroll horizontally (false) (default false)

    public final native void setLineWrapping(boolean lineWrapping) /*-{
        this.lineWrapping = lineWrapping;
    }-*/;

    public final native boolean getLineWrapping() /*-{
        return this.lineWrapping;
    }-*/;

    // lineNumbers = whether to show line numbers (default false)

    public final native void setLineNumbers(boolean lineNumbers) /*-{
        this.lineNumbers = lineNumbers;
    }-*/;

    public final native boolean getLineNumbers() /*-{
        return this.lineNumbers;
    }-*/;

    // firstLineNumber = index of first line (default 1)

    public final native void setFirstLineNumber(boolean firstLineNumber) /*-{
        this.firstLineNumber = firstLineNumber;
    }-*/;

    public final native boolean getFirstLineNumber() /*-{
        return this.firstLineNumber;
    }-*/;

    // lineNumberFormatter = a function to format line numbers shown

    public final native void setLineNumberFormatter(CMLineNumberFormatter lineNumberFormatter) /*-{
        this.lineNumberFormatter = lineNumberFormatter;
    }-*/;

    public final native CMLineNumberFormatter setLineNumberFormatter() /*-{
        return this.lineNumberFormatter;
    }-*/;

    // gutters = an array of classnames, each describing a gutter (default [CodeMirror-linenumbers])
    // classnames are used as index of setGutterMarker

    public final native void setGutters(JsArrayString gutters) /*-{
        this.gutters = gutters;
    }-*/;

    public final native JsArrayString getGutters() /*-{
        return this.gutters;
    }-*/;

    // fixedGutter = whether the gutter scroll horizontally(false) or stays fixed (true) (default true)

    public final native void setFixedGutter(boolean fixedGutter) /*-{
        this.fixedGutter = fixedGutter;
    }-*/;

    public final native boolean getFixedGutter() /*-{
        return this.fixedGutter;
    }-*/;

    /**
     * Sets the scrollbar style.<br>
     * The current options are "native", "simple" or "overlay" (the last two iff simplescrollbar.js is loaded).
     * @param style the value
     */
    public final native void setScrollbarStyle(String style) /*-{
        this.scrollbarStyle = style;
    }-*/;

    /**
     * Returns the current scrollbar style.<br>
     * @see #setScrollbarStyle(String)
     * @return the scrollbar style
     */
    public final native String getScrollbarStyle() /*-{
        return this.scrollbarStyle;
    }-*/;

    // coverGutterNextToScrollbar - not implemented

    // readOnly = true (readonly), false (edit allowed) or "nocursor" (readonly + disallow focus) (default false)

    /**
     * Set the read only attribute of the editor.
     * @param readOnly the new value
     */
    public final void setReadOnly(final ReadOnlyOptions readOnly) {
        final String name = readOnly.name();
        switch (name) {
            case "READONLY":
                this.setReadOnly(true);
                return;
            case "EDIT":
                this.setReadOnly(false);
                return;
            case "NOFOCUS":
                this.setReadOnly(READONLY_VALUE_NOFOCUS);
                return;
            default:
                break;
        }
    }

    /**
     * Set the read only attribute of the editor to one of the boolean values.
     * 
     * @param readOnly the new value
     */
    public final native void setReadOnly(boolean readonly) /*-{
        this.readOnly = readonly;
    }-*/;

    /**
     * Set the read only attribute of the editor to one of the String values.<br>
     * Currently, the only String value is "nofocus".
     * @param readOnly the new value
     */
    public final native void setReadOnly(String readonly) /*-{
        this.readOnly = readonly;
    }-*/;

    private final native boolean isReadOnlyString() /*-{
        return typeof (this.readonly) === "string";
    }-*/;

    private final native boolean getReadOnlyAsBoolean() /*-{
        return this.readonly;
    }-*/;

    private final native String getReadOnlyAsString() /*-{
        return this.readonly;
    }-*/;

    public final ReadOnlyOptions getReadOnly() {
        if (isReadOnlyString()) {
            if (READONLY_VALUE_NOFOCUS.equals(getReadOnlyAsString())) {
                return ReadOnlyOptions.NOFOCUS;
            } else {
                throw new RuntimeException("unknown value for readOnly");
            }
        } else {
            if (getReadOnlyAsBoolean()) {
                return ReadOnlyOptions.READONLY;
            } else {
                return ReadOnlyOptions.EDIT;
            }
        }
    }

    public enum ReadOnlyOptions {
        READONLY,
        EDIT,
        NOFOCUS
    }

    // showCursorWhenSelecting = whether to show the cursor when there is a selection (default false)

    public final native void setShowCursorWhenSelecting(boolean showCursorWhenSelecting) /*-{
        this.showCursorWhenSelecting = showCursorWhenSelecting;
    }-*/;

    public final native boolean getShowCursorWhenSelecting() /*-{
        return this.showCursorWhenSelecting;
    }-*/;

    // undoDepth = number of undo levels (includes selection changes) (default 200)

    public final native void setUndoDepth(int undoDepth) /*-{
        this.undoDepth = undoDepth;
    }-*/;

    public final native int getUndoDepth() /*-{
        return this.undoDepth;
    }-*/;

    // historyEventDelay = inactivity time before a new history event is started in ms (default 1250)
    // events of the same type are merged when done in fast succession ; this value is this "fast succession"

    public final native void setHistoryEventDelay(int delay) /*-{
        this.historyEventDelay = delay;
    }-*/;

    public final native int getHistoryEventDelay() /*-{
        return this.historyEventDelay;
    }-*/;

    // tabindex = the tabindex of the editor (default: no value)

    public final native void setTabindex(int tabindex) /*-{
        this.tabindex = tabindex;
    }-*/;

    public final native int getTabindex() /*-{
        return this.tabindex;
    }-*/;

    // autofocus = whether the editor is focused on creation (default false with special cases when created with fromTextArea)

    public final native void setAutofocus(boolean autofocus) /*-{
        this.autofocus = autofocus;
    }-*/;

    public final native boolean getAutofocus() /*-{
        return this.autofocus;
    }-*/;

    // dragDrop = whether drag and drop is enabled (default true)

    public final native void setDragDrop(boolean dragDrop) /*-{
        this.dragDrop = dragDrop;
    }-*/;

    public final native boolean getDragDrop() /*-{
        return this.dragDrop;
    }-*/;

    // cursorBlinkRate = half period of cursor blink rate in ms ; 0 disable blink and negative hides the cursor (default: 530)

    public final native void setCursorBlinkRate(int cursorBlinkRate) /*-{
        this.cursorBlinkRate = cursorBlinkRate;
    }-*/;

    public final native int getCursorBlinkRate() /*-{
        return this.cursorBlinkRate;
    }-*/;

    // cursorScrollMargin = space above and below the cursor to keep visible when scrolling, pixels (default: 0)

    public final native void setCursorScrollMargin(int cursorScrollMargin) /*-{
        this.cursorScrollMargin = cursorScrollMargin;
    }-*/;

    public final native int getCursorScrollMargin() /*-{
        return this.cursorScrollMargin;
    }-*/;

    // cursorHeight = height of the cursor, multiplicator of the line height (default 1, for the same size as the line)

    public final native void setCursorHeight(double cursorHeight) /*-{
        this.cursorHeight = cursorHeight;
    }-*/;

    public final native double getCursorHeight() /*-{
        return this.cursorHeight;
    }-*/;

    // resetSelectionOnContextMenu - not implemented


    // workTime = duration of highlighting background work unit, ms (default 200)
    // workDelay = duration of pauses between highlighting background work unit, ms (default 300)

    public final native void setWorkTime(int workTime) /*-{
        this.workTime = workTime;
    }-*/;

    public final native int getWorkTime() /*-{
        return this.workTime;
    }-*/;

    public final native void setWorkDelay(int workDelay) /*-{
        this.workDelay = workDelay;
    }-*/;

    public final native int getWorkDelay() /*-{
        return this.workDelay;
    }-*/;

    // pollInterval = period of textinput polling for changes, ms (default 100)

    public final native void setPollInterval(int pollInterval) /*-{
        this.pollInterval = pollInterval;
    }-*/;

    public final native int getPollInterval() /*-{
        return this.pollInterval;
    }-*/;

    // flattenSpans = whether cm will merge two spans with the same class (default true)

    public final native void setFlattenSpans(boolean flattenSpans) /*-{
        this.flattenSpans = flattenSpans;
    }-*/;

    public final native boolean getFlattenSpans() /*-{
        return this.flattenSpans;
    }-*/;

    // addModeClass = whether cm add cm-m-<innermode> class on all tokens(default false)

    public final native void setAddModeClass(boolean addModeClass) /*-{
        this.addModeClass = addModeClass;
    }-*/;

    public final native boolean getAddModeClass() /*-{
        return this.addModeClass;
    }-*/;

    // maxHighlightLength = max position of highlighting per line; Infinity disable limitation(default 10 000)

    public final native void setMaxHighlightLength(int maxHighlightLength) /*-{
        this.maxHighlightLength = maxHighlightLength;
    }-*/;

    public final native int getMaxHighlightLength() /*-{
        return this.maxHighlightLength;
    }-*/;

    // crudeMeasuringFrom = position on the line above which size calculations are an approximation (default 10 000)

    public final native void getCrudeMeasuringFrom(int crudeMeasuringFrom) /*-{
        this.crudeMeasuringFrom = crudeMeasuringFrom;
    }-*/;

    public final native int setCrudeMeasuringFrom() /*-{
        return this.crudeMeasuringFrom;
    }-*/;

    // viewportMargin = number of line above and below the visible viewport that are rendered (default 10)

    public final native void setViewportMargin(int viewportMargin) /*-{
        this.viewportMargin = viewportMargin;
    }-*/;

    public final native int getViewportMargin() /*-{
        return this.viewportMargin;
    }-*/;

    // for additional options added by addons
    public final native void setProperty(final String propertyName, final int value) /*-{
        this[propertyName] = value;
    }-*/;

    public final native void setProperty(final String propertyName, final String value) /*-{
        this[propertyName] = value;
    }-*/;

    public final native void setProperty(final String propertyName, final boolean value) /*-{
        this[propertyName] = value;
    }-*/;

    public final native void setProperty(final String propertyName, final double value) /*-{
        this[propertyName] = value;
    }-*/;

    public final native void setProperty(final String propertyName, final JavaScriptObject value) /*-{
        this[propertyName] = value;
    }-*/;

    public final native String getStringProperty(String propertyName) /*-{
        return this[propertyName];
    }-*/;

    public final native boolean getBooleanProperty(String propertyName) /*-{
        return this[propertyName];
    }-*/;

    public final native int getIntProperty(String propertyName) /*-{
        return this[propertyName];
    }-*/;

    public final native double getDoubleProperty(String propertyName) /*-{
        return this[propertyName];
    }-*/;

    public final native JavaScriptObject getObjectProperty(String propertyName) /*-{
        return this[propertyName];
    }-*/;

}
