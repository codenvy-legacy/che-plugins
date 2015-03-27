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
package org.eclipse.che.ide.editor.codemirrorjso.client.scroll;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Object that represents the current scroll position, the size of the scrollable area, and the size of
 * the visible area (minus scrollbars).
 */
public class CMScrollInfoOverlay extends JavaScriptObject {

    protected CMScrollInfoOverlay() {}

    public final native int getLeft() /*-{
        return this.left;
    }-*/;

    public final native int getTop() /*-{
        return this.top;
    }-*/;

    public final native int getWidth() /*-{
        return this.width;
    }-*/;

    public final native int getHeight() /*-{
        return this.height;
    }-*/;

    public final native int getClientWidth() /*-{
        return this.clientWidth;
    }-*/;

    public final native int getClientHeight() /*-{
        return this.clientHeight;
    }-*/;
}
