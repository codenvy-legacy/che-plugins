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
 * Argument of a (codemirror) native change event.
 * 
 * @author "Mickaël Leduque"
 */
public class CMChangeEventOverlay extends JavaScriptObject {

    protected CMChangeEventOverlay() {
    }

    public final native CMPositionOverlay getFrom() /*-{
        return this.from;
    }-*/;

    public final native CMPositionOverlay getTo() /*-{
        return this.to;
    }-*/;

    public final native JsArrayString getText() /*-{
        return this.text;
    }-*/;

    public final native JsArrayString getRemoved() /*-{
        return this.removed;
    }-*/;

    /* @see http://codemirror.net/doc/manual.html#selection_origin */
    public final native String getOrigin() /*-{
        return this.origin;
    }-*/;
}
