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
import com.google.gwt.core.client.JsArrayString;

import elemental.dom.Element;

/**
 * The map of gutter markers for a line, included in the result of lineInfo().
 */
public class CMGutterMarkersOverlay extends JavaScriptObject {

    /** JSO mandated protected constructor. */
    protected CMGutterMarkersOverlay() {
    }

    /**
     * Returns the marker for the given gutter.
     * 
     * @param gutterId the gutter
     * @return the marker element
     */
    public final native Element getMarker(String gutterId) /*-{
        return this[gutterId];
    }-*/;

    /**
     * Tells if there is a marker for this line in the identified gutter.
     * @param gutterId the gutter
     * @return true iff there is a mark in the gutter
     */
    public final native boolean hasMarker(String gutterId) /*-{
        return this.hasOwnProperty(gutterId);
    }-*/;

    /**
     * Returns the list of gutters.
     * 
     * @return the gutters
     */
    public final native JsArrayString getIds() /*-{
        return this.getOwnPropertyNames();
    }-*/;
}
