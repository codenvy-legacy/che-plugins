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
import com.google.gwt.dom.client.Node;

/**
 * Result of a addLineWidget operation.
 */
public class CMLineWidgetOverlay extends JavaScriptObject {

    protected CMLineWidgetOverlay() {}

    /**
     * Returns the line handle where the widget was added.
     * @return the line handle
     */
    public final native CMLineHandleOverlay getLine() /*-{
        return this.line;
    }-*/;

    /**
     * Clear method, calling this removes the widget.
     */
    public final native void clear() /*-{
        this.clear();
    }-*/;

    /**
     * Forces CodeMirror to update the height of the line that contains the widget.
     * To be called when you made some change to the widget DOM tha might affect its height.
     */
    public final native void changed() /*-{
        this.changed();
    }-*/;

    /**
     * Returns the options used when inserting the widget.
     * @return the options
     */
    public final native CMLineWidgetOptionOverlay getOptions() /*-{
        return this;
    }-*/;

    /**
     * Returns the DOM node for the widget.
     * @return the node
     */
    public final native Node getNode() /*-{
        return this.node;
    }-*/;

}
