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
package org.eclipse.che.ide.editor.codemirrorjso.client.hints;

import org.eclipse.che.ide.editor.codemirrorjso.client.CMEditorOverlay;
import com.google.gwt.core.client.JavaScriptObject;

public class CMHintApplyOverlay extends JavaScriptObject {

    protected CMHintApplyOverlay() {
    }

    public static final native CMHintApplyOverlay create(HintApplyFunction applyFunc) /*-{
        return $entry(function(editor, data, completion) {
            applyFunc.@org.eclipse.che.ide.editor.codemirrorjso.client.hints.CMHintApplyOverlay.HintApplyFunction::applyHint(*)(editor, data, completion);
        });
    }-*/;

    public interface HintApplyFunction {
        void applyHint(CMEditorOverlay editor, CMHintResultsOverlay data, JavaScriptObject completion);
    }
}
