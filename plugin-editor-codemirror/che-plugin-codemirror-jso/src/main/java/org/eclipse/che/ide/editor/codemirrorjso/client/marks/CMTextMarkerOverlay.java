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

import org.eclipse.che.ide.editor.codemirrorjso.client.CMDocumentOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.CMRangeOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.line.CMLineHandleOverlay;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Marker object.<br>
 * Represents marks on text in the editor.
 */
public class CMTextMarkerOverlay extends JavaScriptObject {

    protected CMTextMarkerOverlay() {
    }

    public final native String getType() /*-{
        return this.type;
    }-*/;

    public final native CMDocumentOverlay getDoc() /*-{
        return this.doc;
    }-*/;

    public final native JsArray<CMLineHandleOverlay> getLines() /*-{
        return this.lines;
    }-*/;

    public final native void clear() /*-{
        this.clear();
    }-*/;

    public final native CMRangeOverlay find() /*-{
        return this.find();
    }-*/;

    public final native void changed() /*-{
        this.changed();
    }-*/;
}
