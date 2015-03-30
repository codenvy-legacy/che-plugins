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
 * A codemirror command function.
 */
public class CMCommandOverlay extends JavaScriptObject {

    /** JSO mandated construcotr. */
    protected CMCommandOverlay() {
    }

    /** Creates a command function instance from a {@link CommandFunction} java instance. */
    public static final native CMCommandOverlay create(CommandFunction commandFunc) /*-{
        return $entry(function(editor) {
            commandFunc.@org.eclipse.che.ide.editor.codemirrorjso.client.CMCommandOverlay.CommandFunction::execCommand(Lorg/eclipse/che/ide/editor/codemirrorjso/client/CMEditorOverlay;)(editor);
        });
    }-*/;

    /** Interface describing a command function for the java side. */
    public interface CommandFunction {
        void execCommand(CMEditorOverlay editor);
    }
}
