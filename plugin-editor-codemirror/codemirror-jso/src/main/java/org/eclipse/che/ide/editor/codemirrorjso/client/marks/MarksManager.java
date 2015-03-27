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

import org.eclipse.che.ide.editor.codemirrorjso.client.CMPositionOverlay;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Handles marks on the text.
 */
public class MarksManager extends JavaScriptObject {

    protected MarksManager() {
    }

    /**
     * Mark a range of text with a specific CSS class name.
     * 
     * @param from start of the range
     * @param to end of the range
     * @return a descriptor for the mark
     */
    public final native CMTextMarkerOverlay markText(CMPositionOverlay from, CMPositionOverlay to) /*-{
        return this.markText(from, to);
    }-*/;

    /**
     * Mark a range of text with a specific CSS class name.
     * 
     * @param from start of the range
     * @param to end of the range
     * @param options the mark options
     * @return a descriptor for the mark
     */
    public final native CMTextMarkerOverlay markText(CMPositionOverlay from, CMPositionOverlay to, CMTextMarkerOptionOverlay options) /*-{
        return this.markText(from, to, options);
    }-*/;

    /**
     * Returns an array of all the bookmarks and marked ranges found between the given positions.
     * 
     * @param from start of the range
     * @param to end of the range
     * @return the marks in the range
     */
    public final native JsArray<CMTextMarkerOverlay> findMarks(CMPositionOverlay from, CMPositionOverlay to) /*-{
        return this.findMarks(from, to);
    }-*/;

    /**
     * Returns an array of all the bookmarks and marked ranges present at the given position.
     * 
     * @param pos the position
     * @return the marks on the position
     */
    public final native JsArray<CMTextMarkerOverlay> findMarksAt(CMPositionOverlay pos) /*-{
        return this.findMarksAt(pos);
    }-*/;

    /**
     * Returns an array of all the bookmarks and marked ranges found between the given positions
     * 
     * @return all marks in the document
     */
    public final native JsArray<CMTextMarkerOverlay> getAllMarks() /*-{
        return this.getAllMarks();
    }-*/;
}
