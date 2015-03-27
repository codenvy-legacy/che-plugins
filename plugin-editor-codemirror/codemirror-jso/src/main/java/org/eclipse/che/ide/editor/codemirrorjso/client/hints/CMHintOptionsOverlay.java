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

import org.eclipse.che.ide.editor.codemirrorjso.client.CMKeymapOverlay;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Overlay class over CodeMirror's completion options objects.
 */
public class CMHintOptionsOverlay extends JavaScriptObject {

    protected CMHintOptionsOverlay() {
    }

    public final native CMHintFunctionOverlay getHint() /*-{
        return this.hint;
    }-*/;

    /**
     * Hinting function.
     * @param hintFunction hinting function
     */
    public final native void setHint(CMHintFunctionOverlay hintFunction) /*-{
        this.hint = hintFunction;
    }-*/;

    public final native boolean getCompleteSingle() /*-{
        return this.completeSingle;
    }-*/;

    /**
     * Determines whether, when only a single completion is available, it is completed without showing
     * the dialog.<br>
     * Defaults to true.
     * @param newValue true to autocomplete single proposals
     */
    public final native void setCompleteSingle(boolean newValue) /*-{
        this.completeSingle = newValue;
    }-*/;

    public final native boolean getAlignWithWord() /*-{
        return this.alignWithWord;
    }-*/;

    /**
     * Whether the pop-up should be horizontally aligned with the start of the word (true, default), or
     * with the cursor (false).
     * @param newValue popup align mode
     */
    public final native void setAlignWithWord(boolean newValue) /*-{
        this.alignWithWord = newValue;
    }-*/;

    public final native boolean getCloseOnUnfocus() /*-{
        return this.closeOnUnfocus;
    }-*/;

    /**
     * When enabled (which is the default), the pop-up will close when the editor is unfocused.
     * @param newValue true to autoclose on blur
     */
    public final native void setCloseOnUnfocus(boolean newValue) /*-{
        this.closeOnUnfocus = newValue;
    }-*/;

    public final native CMKeymapOverlay getCustomKeys() /*-{
        return this.customKeys;
    }-*/;

    /**
     * Allows you to provide a custom key map of keys to be active when the pop-up is active.
     * @param newValue custom keymap
     */
    public final native void setCustomKeys(CMKeymapOverlay newValue) /*-{
        this.customKeys = newValue;
    }-*/;

    public final native CMKeymapOverlay getExtraKeys() /*-{
        return this.extraKeys;
    }-*/;

    /**
     * Like customKeys above, but the bindings will be added to the set of default bindings,
     * instead of replacing them.
     * @param newValue extra keys
     */
    public final native void setExtraKeys(CMKeymapOverlay newValue) /*-{
        this.extraKeys = newValue;
    }-*/;

    /**
     * Define which element will be the parent of the popup.<br>
     * <em>Optional</em>. If not set, the document will be the parent.
     * @param container the parent of the popup
     */
    public final native void setContainer(Element container) /*-{
        this.container = container;
    }-*/;

    public final native Element getContainer() /*-{
        return this.container;
    }-*/;

    public static final native CMHintOptionsOverlay create() /*-{
        return {};
    }-*/;
}
