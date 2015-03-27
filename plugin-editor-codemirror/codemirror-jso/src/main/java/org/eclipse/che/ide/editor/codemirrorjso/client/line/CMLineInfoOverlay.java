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
import com.google.gwt.core.client.JsArray;

/**
 * Information on a given line.
 */
public class CMLineInfoOverlay extends JavaScriptObject {

    protected CMLineInfoOverlay() {}

    /**
     * Returns the line number.
     * @return the line number
     */
    public final native int getLine() /*-{
        return this.line;
    }-*/;

    /**
     * Returns the line handle.
     * @return the line handle
     */
    public final native CMLineHandleOverlay getHandle() /*-{
        return this.handle;
    }-*/;

    /**
     * Returns the line contents.
     * @return the lines contents
     */
    public final native String getText() /*-{
        return this.text;
    }-*/;

    /**
     * Returns the gutter markers for the line.<br>
     * They are given as a map gutterId(classname) -> gutter element
     * @return the gutter markers
     */
    public final native CMGutterMarkersOverlay getGutterMarkers() /*-{
        return this.gutterMarkers;
    }-*/;

    /**
     * Returns the CSS class name set on the text.
     * @return the text class
     */
    public final native String getTextClass() /*-{
        return this.textClass;
    }-*/;

    /**
     * Returns the CSS class name set on the background.
     * @return the background class
     */
    public final native String getBgClass() /*-{
        return this.bgClass;
    }-*/;

    /**
     * Returns the CSS class name set on the line wrapper.
     * @return the wrapper class
     */
    public final native String getWrapClass() /*-{
        return this.wrapClass;
    }-*/;

    /**
     * Returns the widgets added to the line.
     * @return the widgets
     */
    public final native JsArray<CMLineWidgetOverlay> getWidgets() /*-{
        return this.widgets;
    }-*/;
}
