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
package org.eclipse.che.ide.editor.codemirrorjso.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Option objects for set selection methods.
 */
public class CMSetSelectionOptions extends JavaScriptObject {

    /** JSO mandated protected constructor. */
    protected CMSetSelectionOptions() {
    }

    /*
     * Should we scroll to the selection head ? Default to true
     */

    /**
     * Tells if we should scroll to the selection head.
     * @return false to not scroll
     */
    public final native boolean getScroll() /*-{
        return this.scroll;
    }-*/;

    /**
     * Sets the flag that tells if we should scroll to the selection head.<br>
     * Defaults to true.
     * @param true to scroll
     */
    public final native void setScroll(boolean newValue) /*-{
        this.scroll = newValue;
    }-*/;

    /*
     * Determines whether the selection history event may be merged with the previous one
     */

    /**
     * Determines whether the selection history event may be merged with the previous one.
     * @see https://codemirror.net/doc/manual.html#setSelection for the meaning of the values
     * @return true if the history event can be merged
     */
    public final native boolean getOrigin() /*-{
        return this.origin;
    }-*/;

    /**
     * Decides if the selection history event may be merged with the previous one.
     * @see https://codemirror.net/doc/manual.html#setSelection for the meaning of the values
     * @param newValue the new value
     */
    public final native void setOrigin(String newValue) /*-{
        this.origin = newValue;
    }-*/;

    /*
     * Adjustement direction when the range is atomic (the cursor can't go inside). 1 or -1. By default, depends on the relative position of
     * the old selection
     */

    /**
     * Returns the adjustement when scrolling if the range is atomic.
     * @return the adjustement
     */
    public final native int getBias() /*-{
        return this.bias;
    }-*/;

    /**
     * Sets the adjustement when scrolling if the range is atomic.<br>
     * Valid values are 1 and -1 (resp. forward and backward)
     * @param newValue the new value
     */
    public final native void setBias(int newValue) /*-{
        this.bias = newValue;
    }-*/;

    /**
     * Creates a new option objects with the default values : scroll.
     * @return a default option object
     */
    public static final native CMSetSelectionOptions create() /*-{
        return {};
    }-*/;

    /**
     * Creates a new option objects with the 'no scroll' option set.
     * @return a default option object
     */
    public static final native CMSetSelectionOptions createNoScroll() /*-{
        return {"scroll": false};
    }-*/;
}
