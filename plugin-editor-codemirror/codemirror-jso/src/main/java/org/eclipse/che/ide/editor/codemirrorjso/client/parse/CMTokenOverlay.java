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
package org.eclipse.che.ide.editor.codemirrorjso.client.parse;

import com.google.gwt.core.client.JavaScriptObject;

/** A codemirror token object. */
public class CMTokenOverlay extends JavaScriptObject {

    /** JSO mandated protected constructor. */
    protected CMTokenOverlay() {
    }

    public final native int getStart() /*-{
        return this.start;
    }-*/;

    public final native int getEnd() /*-{
        return this.end;
    }-*/;

    public final native String getString() /*-{
        return this.string;
    }-*/;

    public final native String getType() /*-{
        return this.type;
    }-*/;

    public final native CMStateOverlay getState() /*-{
        return this.state;
    }-*/;
}
