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

public class CMDialogCallbackOverlay extends JavaScriptObject {

    protected CMDialogCallbackOverlay() {}

    public static final native CMDialogCallbackOverlay create(DialogCallback callback) /*-{
        return function(value) {
            callback.@org.eclipse.che.ide.editor.codemirrorjso.client.dialog.CMDialogCallbackOverlay.DialogCallback::onInputDone(*)(value);
        };
    }-*/;

    public interface DialogCallback {
        void onInputDone(String value);
    }
}
