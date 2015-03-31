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
package org.eclipse.che.ide.editor.codemirrorjso.client.dialog;

import com.google.gwt.core.client.JavaScriptObject;

public class CMDialogOptionsOverlay extends JavaScriptObject {

    protected CMDialogOptionsOverlay() {}

    public final native boolean getBottom() /*-{
        return this.bottom;
    }-*/;

    /**
     * Location of the dialog. top by default, bottom if bottom is set to true.
     * @param bottom set to true to show at bottom
     */
    public final native void setBottom(boolean bottom) /*-{
        this.bottom = bottom;
    }-*/;

    public final native int getDuration() /*-{
        return this.duration;
    }-*/;

    /**
     * The duration the notification is shown (when it's a notification).
     * @param duration the duration
     */
    public final native void setDuration(int duration) /*-{
        this.duration = duration;
    }-*/;

    public final native String getValue() /*-{
        return this.value;
    }-*/;

    /**
     * The original value in the <input> element when the dialog is a 'openDialog' one.
     * @param value he original input value
     */
    public final native void setValue(String value) /*-{
        this.value = value;
    }-*/;
}
