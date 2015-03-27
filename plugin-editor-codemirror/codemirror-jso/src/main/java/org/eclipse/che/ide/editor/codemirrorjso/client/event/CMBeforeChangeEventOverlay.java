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
package org.eclipse.che.ide.editor.codemirrorjso.client.event;

import org.eclipse.che.ide.editor.codemirrorjso.client.CMPositionOverlay;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Argument of a (codemirror) native beforechange event.
 */
public class CMBeforeChangeEventOverlay extends JavaScriptObject {

    /** JSO mandated protected constructor. */
    protected CMBeforeChangeEventOverlay() {
    }

    /**
     * Return the from field of the event.
     * @return from
     */
    public final native CMPositionOverlay getFrom() /*-{
        return this.from;
    }-*/;

    /**
     * Return the to field of the event.
     * @return to
     */
    public final native CMPositionOverlay getTo() /*-{
        return this.to;
    }-*/;

    /**
     * Return the text field of the event i.e. the added text.
     * @return text
     */
    public final native JsArrayString getText() /*-{
        return this.text;
    }-*/;

    /** Cancels the change. */
    public final native void cancel() /*-{
        this.cancel();
    }-*/;

    /**
     * Tells if the change is updatable.
     * @return true iff the change is updatable
     */
    public final native boolean hasUpdate() /*-{
        return (typeof this.update === "function");
    }-*/;

    /** Sets the change as updated. */
    public final native void update() /*-{
        return this.update();
    }-*/;

    /** Sets the change as updated with a new 'from' value. */
    public final native void update(CMPositionOverlay from) /*-{
        this.update(from);
    }-*/;

    /** Sets the change as updated with a new 'from' and 'to' values. */
    public final native void update(CMPositionOverlay from, CMPositionOverlay to) /*-{
        this.update(from, to);
    }-*/;

    /** Sets the change as updated with a new 'from', 'to' and 'text' values. */
    public final native void update(CMPositionOverlay from, CMPositionOverlay to, JsArrayString text) /*-{
        this.update(from, to, text);
    }-*/;
}
