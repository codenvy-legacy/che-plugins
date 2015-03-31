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
 * A description of a pixel/coordinate position.
 */
public class CMPixelCoordinatesOverlay extends JavaScriptObject {

    protected CMPixelCoordinatesOverlay() {}

    public final native int getLeft() /*-{
        return this.left;
    }-*/;

    public final native int getTop() /*-{
        return this.top;
    }-*/;

    public static final native CMPixelCoordinatesOverlay create(int left, int top) /*-{
        return {"left": left, "top": top};
    }-*/;
}
