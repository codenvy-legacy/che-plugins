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
 * A description of a pixel/coordinate range for use with scrollIntoView.
 */
public class CMPixelRangeOverlay extends JavaScriptObject {

    protected CMPixelRangeOverlay() {}

    public final native int getLeft() /*-{
        return this.left;
    }-*/;

    public final native int getTop() /*-{
        return this.top;
    }-*/;

    public final native int getRight() /*-{
        return this.width;
    }-*/;

    public final native int getBottom() /*-{
        return this.height;
    }-*/;

    public final native int setLeft(int left) /*-{
        this.left = left;
    }-*/;

    public final native int setTop(int top) /*-{
        this.top = top;
    }-*/;

    public final native int setRight(int right) /*-{
        this.right = right;;
    }-*/;

    public final native int setBottom(int bottom) /*-{
        this.bottom = bottom;
    }-*/;

    public static final native CMPixelRangeOverlay create() /*-{
        return {};
    }-*/;
}
