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

import org.eclipse.che.ide.editor.codemirrorjso.client.hints.CMHintFunctionOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.options.CMEditorOptionsOverlay;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;

import elemental.util.MapFromStringTo;

/**
 * Overlay on the CodeMirror javascript object.
 */
public class CodeMirrorOverlay extends JavaScriptObject {

    protected CodeMirrorOverlay() {}

    /**
     * Creates an editor instance using the global CodeMirror object.
     * @param element the element backing the editor
     * @return an editor instance
     */
    public static final native CMEditorOverlay createEditorGlobal(final Element element) /*-{
        return $wnd.CodeMirror(element, {});
    }-*/;

    /**
     * Creates an editor instance using the global CodeMirror object.
     * @param element the element backing the editor
     * @param options the editor options
     * @return an editor instance
     */
    public static final native CMEditorOverlay createEditorGlobal(Element element,
                                                                  JavaScriptObject options) /*-{
        return $wnd.CodeMirror(element, options);
    }-*/;

    /**
     * Creates an editor instance.
     * @param element the element backing the editor
     * @return an editor instance
     */
    public final native CMEditorOverlay createEditor(Element element) /*-{
        return this(element, options);
    }-*/;

    /**
     * Creates an editor instance.
     * 
     * @param element the element backing the editor
     * @return an editor instance
     */
    public final native CMEditorOverlay createEditor(elemental.dom.Element element) /*-{
        return this(element, options);
    }-*/;

    /**
     * Creates an editor instance using the given CodeMirror object.
     * @param element the element backing the editor
     * @param options the editor options
     * @return an editor instance
     */
    public final native CMEditorOverlay createEditor(Element element, JavaScriptObject options) /*-{
        return this(element, options);
    }-*/;

    /**
     * Creates an editor instance using the given CodeMirror object.
     * 
     * @param element the element backing the editor
     * @param options the editor options
     * @return an editor instance
     */
    public final native CMEditorOverlay createEditor(elemental.dom.Element element, JavaScriptObject options) /*-{
        return this(element, options);
    }-*/;

    /**
     * Version of codemirror.
     *
     * @return the version, major.minor.patch (all three are integers)
     */
    public final native String version() /*-{
        return this.version();
    }-*/;

    /**
     * Returns the map of registered commands.
     * @return the commands
     */
    public final native MapFromStringTo<CMCommandOverlay> commands() /*-{
        return this.commands;
    }-*/;

    /**
     * Returns the default configuration object for new codemirror editors.<br>
     * This object properties can be modified to change the default options for new editors (but will not change existing ones).
     *
     * @return the default configuration
     */
    public final native CMEditorOptionsOverlay defaults() /*-{
        return this.defaults;
    }-*/;

    /**
     * CodeMirror modes by name.
     *
     * @return a javascript object such that modes[modeName] is the mode object
     */
    public final native MapFromStringTo<CMModeOverlay> modes() /*-{
        return this.modes;
    }-*/;

    /**
     * Names of the modes loaded in codemirror.
     *
     * @return an array of names of modes
     */
    public final native JsArrayString modeNames() /*-{
        return Object.getOwnPropertyNames(this.modes).sort();
    }-*/;

    /**
     * Codemirror modes by mime-types.
     *
     * @return a javascript object such that mimeModes[mimeType] is the matching mode object
     */
    public final native MapFromStringTo<CMModeOverlay> mimeModes() /*-{
        return this.mimeModes;
    }-*/;

    /**
     * Names of the mime-types known in codemirror.
     *
     * @return an array of names of mime-types
     */
    public final native JsArrayString mimeModeNames() /*-{
        return Object.getOwnPropertyNames(this.mimeModes).sort();
    }-*/;

    /**
     * Return the registered keymaps.
     * @return the keymaps
     */
    public final native CMKeymapSetOverlay keyMap() /*-{
        return this.keyMap;
    }-*/;

    /**
     * Returns the list of key names by code.
     * @return the key names
     */
    public final native JsArrayString keyNames() /*-{
        return this.keyNames;
    }-*/;

    /**
     * Tells in the showHint method is available on the CodeMirror object.
     * @param module the CodeMirror object
     * @return true iff CodeMirror.showHint is defined
     */
    public static final native boolean hasShowHint(JavaScriptObject module) /*-{
        return (("showHint" in module) && !(typeof(module[showHint]) === 'undefined'));
    }-*/;

    /**
     * Returns the hint function matching the given name.
     * @param name the name of the function
     * @return the hint function
     */
    public final native CMHintFunctionOverlay getHintFunction(String name) /*-{
        return this.hint[name];
    }-*/;

    public final native <T extends JavaScriptObject> void on(JavaScriptObject instance,
                                                            String eventType,
                                                            EventHandlers.EventHandlerOneParameter<T> handler) /*-{
        this.on(instance, eventType,
                    function(param) {
                        handler.@org.eclipse.che.ide.editor.codemirrorjso.client.EventHandlers.EventHandlerOneParameter::onEvent(*)(param);
                    });
    }-*/;

    public final native <T extends JavaScriptObject> void on(JavaScriptObject instance,
                                                            String eventType,
                                                            EventHandlers.EventHandlerNoParameters handler) /*-{
        this.on(instance, eventType, function(param) {
            handler.@org.eclipse.che.ide.editor.codemirrorjso.client.EventHandlers.EventHandlerNoParameters::onEvent()();
        });
    }-*/;

    public final native <T extends JavaScriptObject> void on(JavaScriptObject instance,
                                                            String eventType,
                                                            EventHandlers.EventHandlerMixedParameters handler) /*-{
        this.on(instance, eventType,
                function() {
                    var params = [];
                    for (var i = 0; i < arguments.length; i++) {
                        params.push(arguments[i]);
                    }
                    handler.@org.eclipse.che.ide.editor.codemirrorjso.client.EventHandlers.EventHandlerMixedParameters::onEvent(*)(params);
                });
    }-*/;

    /**
     * Ensures the mode is loaded and causes the given editor instance to refresh its mode when the loading succeeded.
     * @param instance the editor instance
     * @param mode the mode
     */
    public final native void autoLoadMode(CMEditorOverlay instance, String mode) /*-{
        this.autoLoadMode(instance, mode);
    }-*/;

    /* methods related to mode auto-loading. */

    /**
     * Ensures the mode is loaded and causes the given editor instance to refresh its mode when the loading succeeded.
     * @param instance the editor instance
     * @param mode the mode
     */
    public final native void autoLoadMode(CMEditorOverlay instance, CMModeOverlay mode) /*-{
        this.autoLoadMode(instance, mode);
    }-*/;

    /**
     * Sets the mode url pattern.<br>
     * The mode URL is a string that mode paths can be constructed from.<br>
     * For example "mode/%N/%N.js"—the %N's will be replaced with the mode name.
     * @param url the mode ur pattern
     */
    public final native void setModeURL(String url) /*-{
        this.modeURL = url;
    }-*/;

    /* mime-type catalog methods. */

    /**
     * Search a mode description by mime-type.
     * @param mime the mime-type
     * @return a mode info object or null
     */
    @Nullable
    public final native CMModeInfoOverlay findModeByMIME(String mime) /*-{
        return this.findModeByMIME(mime);
    }-*/;

    /**
     * Search a mode description by name.
     * @param name the name
     * @return a mode info object or null
     */
    @Nullable
    public final native CMModeInfoOverlay findModeByName(String name) /*-{
        return this.findModeByName(name);
    }-*/;

    /**
     * Search a mode description by extension.
     * @param ext the extension
     * @return a mode info object or null
     */
    @Nullable
    public final native CMModeInfoOverlay findModeByExtension(String ext) /*-{
        return this.findModeByExtension(ext);
    }-*/;
}
