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

import org.eclipse.che.commons.annotation.Nullable;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Catalog of modes available in codemirror.
 * @see http://codemirror.net/doc/manual.html#addon_meta and http://codemirror.net/mode/meta.js
 */
public class CMModeInfoOverlay extends JavaScriptObject {

    /** JSO mandated protected constructor. */
    protected CMModeInfoOverlay() {}

    /**
     * Returns the human-readable name of the mode.
     * @return the name
     */
    public final native String getName() /*-{
        return this.name;
    }-*/;

    /**
     * Returns the mime-type.
     * @return the mime-type
     */
    public final native String getMime() /*-{
        return this.mime;
    }-*/;

    /**
     * Returns  the name of the mode file that defines this MIME.
     * @return the name of the mode file
     */
    public final native String getMode() /*-{
        return this.mode;
    }-*/;

    /**
     * Returns the optional array of MIME types for modes with multiple MIMEs associated
     * @return mime-types
     */
    @Nullable
    public final native JsArrayString getMimes() /*-{
        return this.mimes;
    }-*/;

    /**
     * Returns the optional array of file extensions associated with this mode.
     * @return file extensions
     */
    @Nullable
    public final native JsArrayString getExt() /*-{
        return this.ext;
    }-*/;
}
