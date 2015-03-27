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
package org.eclipse.che.ide.editor.codemirrorjso.client.marks;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.dom.Element;

/**
 * Options for the text mark.
 */
public class CMTextMarkerOptionOverlay extends JavaScriptObject {

    protected CMTextMarkerOptionOverlay() {
    }

    /**
     * Assigns a CSS class to the marked stretch of text.
     * 
     * @param className the class name
     */
    public final native void setClassName(String className) /*-{
        this.className = className;
    }-*/;

    public final native String getClassName() /*-{
        return this.className;
    }-*/;

    /**
     * Determines whether text inserted on the left of the marker will end up inside or outside of it.
     * 
     * @param newValue true to include new text
     */
    public final native void setInclusiveLeft(boolean newValue) /*-{
        this.inclusiveLeft = newValue;
    }-*/;

    public final native boolean getInclusiveLeft() /*-{
        return this.inclusiveLeft;
    }-*/;

    /**
     * Determines whether text inserted on the right of the marker will end up inside or outside of it.
     * 
     * @param newValue true to include new text
     */
    public final native void setInclusiveRight(boolean newValue) /*-{
        this.inclusiveRight = newValue;
    }-*/;

    public final native boolean getInclusiveRight() /*-{
        return this.inclusiveRight;
    }-*/;

    /**
     * Atomic ranges act as a single unit when cursor movement is concerned—i.e. it is impossible to place the<br>
     * cursor inside of them.<br>
     * In atomic ranges, inclusiveLeft and inclusiveRight have a different meaning—they will prevent the cursor from<br>
     * being placed respectively directly before and directly after the range.
     * 
     * @param newValue true to make the marked text atomic
     */
    public final native void setAtomic(boolean newValue) /*-{
        this.atomic = newValue;
    }-*/;

    public final native boolean getAtomic() /*-{
        return this.atomic;
    }-*/;

    /**
     * Collapsed ranges do not show up in the display.<br>
     * Setting a range to be collapsed will automatically make it atomic.
     * 
     * @param newValue true to collapse the range
     */
    public final native void setCollapsed(boolean newValue) /*-{
        this.collapsed = newValue;
    }-*/;

    public final native boolean getCollapsed() /*-{
        return this.collapsed;
    }-*/;

    /**
     * When enabled, will cause the mark to clear itself whenever the cursor enters its range. This is mostly useful<br>
     * for text-replacement widgets that need to 'snap open' when the user tries to edit them.<br>
     * The "clear" event fired on the range handle can be used to be notified when this happens.
     * 
     * @param newValue true to clear on enter
     */
    public final native void setClearOnEnter(boolean newValue) /*-{
        this.clearOnEnter = newValue;
    }-*/;

    public final native boolean getClearOnEnter() /*-{
        return this.clearOnEnter;
    }-*/;

    /**
     * Determines whether the mark is automatically cleared when it becomes empty.<br>
     * Default is true.
     * 
     * @param newValue true to clear when region is empty
     */
    public final native void setClearWhenEmpty(boolean newValue) /*-{
        this.clearWhenEmpty = newValue;
    }-*/;

    public final native boolean getClearWhenEmpty() /*-{
        return this.clearWhenEmpty;
    }-*/;

    /**
     * Use a given node to display this range. Implies both collapsed and atomic.<br>
     * The given DOM node must be an inline element (as opposed to a block element).
     * 
     * @param newValue the element
     */
    public final native void setReplacedWith(Element newValue) /*-{
        this.replacedWith = newValue;
    }-*/;

    public final native Element getReplacedWith() /*-{
        return this.replacedWith;
    }-*/;

    /**
     * When replacedWith is given, this determines whether the editor will capture mouse and drag events occurring<br>
     * in this widget. Default is false—the events will be left alone for the default browser handler, or specific<br>
     * handlers on the widget, to capture.
     * 
     * @param newValue true to capture mouse events
     */
    public final native void setHandleMouseEvents(boolean newValue) /*-{
        this.handleMouseEvents = newValue;
    }-*/;

    public final native boolean getHandleMouseEvents() /*-{
        return this.handleMouseEvents;
    }-*/;

    /**
     * Set the marked text to be non-modifiable (except by setValue).
     * 
     * @param newValue true to make the text unmodifiable
     */
    public final native void setReadOnly(boolean newValue) /*-{
        this.readOnly = newValue;
    }-*/;

    public final native boolean getReadOnly() /*-{
        return this.readOnly;
    }-*/;

    /**
     * When set to true (default is false), adding this marker will create an event in the undo history that can<br>
     * be individually undone (clearing the marker).
     * 
     * @param newValue true to create an event in the undo history
     */
    public final native void setAddToHistory(boolean newValue) /*-{
        this.addToHistory = newValue;
    }-*/;

    public final native boolean getAddToHistory() /*-{
        return this.addToHistory;
    }-*/;

    /**
     * Can be used to specify an extra CSS class to be applied to the leftmost span that is part of the marker.
     * 
     * @param newValue the style
     */
    public final native void setStartStyle(String newValue) /*-{
        this.startStyle = newValue;
    }-*/;

    public final native String getStartStyle() /*-{
        return this.startStyle;
    }-*/;

    /**
     * Can be used to specify an extra CSS class to be applied to the rightmost span that is part of the marker.
     * 
     * @param newValue the style
     */
    public final native void setEndStyle(String newValue) /*-{
        this.endStyle = newValue;
    }-*/;

    public final native String getEndStyle() /*-{
        return this.endStyle;
    }-*/;

    /**
     * When given, will give the nodes created for this span a HTML title attribute with the given value.
     * 
     * @param newValue the title
     */
    public final native void setTitle(String newValue) /*-{
        this.title = newValue;
    }-*/;

    public final native String getTitle() /*-{
        return this.title;
    }-*/;

    /**
     * When the target document is linked to other documents, you can set shared to true to make the marker<br>
     * appear in all documents. By default, a marker appears only in its target document.
     * 
     * @param newValue true to shared among linked documents
     */
    public final native void setShared(boolean newValue) /*-{
        this.shared = newValue;
    }-*/;

    public final native boolean getShared() /*-{
        return this.shared;
    }-*/;
}
