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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Overlay class over CodeMirror.keyMap the global object that contains all keymaps.
 * 
 * @author "Mickaël Leduque"
 */
public class CMKeymapSetOverlay extends JavaScriptObject {

    protected CMKeymapSetOverlay() {
    }

    public final native CMKeymapOverlay get(String key) /*-{
        return this[key];
    }-*/;

    private static final native JsArrayString keys(CMKeymapSetOverlay jsObject) /*-{
        return Object.keys(jsObject);
    }-*/;

    public final List<String> getKeys() {
        final JsArrayString jsObject = keys(this);
        final List<String> result = new ArrayList<>();
        for (int i = 0; i < jsObject.length(); i++) {
            result.add(jsObject.get(i));
        }
        return result;
    }
}

