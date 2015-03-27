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
import org.eclipse.che.ide.editor.codemirrorjso.client.CodeMirrorOverlay;
import com.google.gwt.core.client.JavaScriptObject;

public class CMHintFunctionOverlay extends JavaScriptObject {

    protected CMHintFunctionOverlay() {
    }

    public final native CMHintResultsOverlay apply(CMEditorOverlay editor, CMHintOptionsOverlay options) /*-{
        return this(editor, options);
    }-*/;

    public final native CMHintResultsOverlay apply(CMEditorOverlay editor) /*-{
        return this(editor);
    }-*/;

    public static final native CMHintFunctionOverlay createFromName(CodeMirrorOverlay codeMirror, String funcName) /*-{
        return codeMirror.hint[funcName];
    }-*/;

    public static final native CMHintFunctionOverlay createFromHintFunction(HintFunction hintFunction) /*-{
        return function(editor, options) {
            return hintFunction.@org.eclipse.che.ide.editor.codemirrorjso.client.hints.CMHintFunctionOverlay.HintFunction::getHints(*)(editor, options);
        };
    }-*/;

    public static final native CMHintFunctionOverlay createFromAsyncHintFunction(AsyncHintFunction hintFunction) /*-{
        var result = function(editor, callback, options) {
            return hintFunction.@org.eclipse.che.ide.editor.codemirrorjso.client.hints.CMHintFunctionOverlay.AsyncHintFunction::getHints(*)(editor, callback, options);
        };
        result.async = true;
        return result;
    }-*/;

    public interface HintFunction {
        CMHintResultsOverlay getHints(CMEditorOverlay editor, CMHintOptionsOverlay options);
    }

    public interface AsyncHintFunction {
        void getHints(CMEditorOverlay editor, CMHintCallback callback,
                                  CMHintOptionsOverlay options);
    }
}
