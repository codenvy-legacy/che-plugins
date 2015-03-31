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
package org.eclipse.che.ide.editor.codemirrorjso.client.line;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Options for adding a widget to a line.
 */
public class CMLineWidgetOptionOverlay extends JavaScriptObject {

    protected CMLineWidgetOptionOverlay() {}

    public final native boolean getCoverGutter() /*-{
        return this.coverGutter;
    }-*/;

    /**
     * Whether the widget should cover the gutter.
     * @param newValue true to cover the gutter (default false)
     */
    public final native void setCoverGutter(boolean newValue) /*-{
        this.coverGutter = newValue;
    }-*/;

    public final native boolean getNoHScroll() /*-{
        return this.noHScroll;
    }-*/;

    /**
     * Whether the widget should stay fixed in the face of horizontal scrolling.
     * @param newValue true to stay fixed (default false)
     */
    public final native void setNoHScroll(boolean newValue) /*-{
        this.noHScroll = newValue;
    }-*/;

    public final native boolean getAbove() /*-{
        return this.above;
    }-*/;

    /**
     * Causes the widget to be placed above instead of below the text of the line.
     * @param newValue true to place the widget above the line (default false)
     */
    public final native void setAbove(boolean newValue) /*-{
        this.above = newValue;
    }-*/;

    public final native boolean getHandleMouseEvents() /*-{
        return this.handleMouseEvents;
    }-*/;

    /**
     * Determines whether the editor will capture mouse and drag events occurring in this widget.
     * Default is false—the events will be left alone for the default browser handler, or specific
     * handlers on the widget, to capture.
     * @param newValue true to capture mouse events (default false)
     */
    public final native void setHandleMouseEvents(boolean newValue) /*-{
        this.handleMouseEvents = newValue;
    }-*/;

    /**
     * Returns the insertion position of the widget relative to the other widgets for this line.
     * @return the insertion position
     */
    public final native int getInsertAt() /*-{
        return this.insertAt;
    }-*/;

    /**
     * Sets the insertion position of the widget relative to the other widgets for this line.
     * @param position the insertion position
     */
    public final native void setInsertAt(int position) /*-{
        this.insertAt = position;
    }-*/;
}
