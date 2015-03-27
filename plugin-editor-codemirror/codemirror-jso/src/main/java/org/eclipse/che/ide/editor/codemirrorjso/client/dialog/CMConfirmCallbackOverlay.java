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

import org.eclipse.che.ide.editor.codemirrorjso.client.CMEditorOverlay;
import com.google.gwt.core.client.JavaScriptObject;

public class CMConfirmCallbackOverlay extends JavaScriptObject {

    protected CMConfirmCallbackOverlay() {}

    public static final native CMConfirmCallbackOverlay create(ConfirmCallback callback) /*-{
        return function(editor) {
            callback.@org.eclipse.che.ide.editor.codemirrorjso.client.dialog.CMConfirmCallbackOverlay.ConfirmCallback::onConfirm(*)(editor);
        };
    }-*/;

    public interface ConfirmCallback {
        void onConfirm(CMEditorOverlay editor);
    }
}
