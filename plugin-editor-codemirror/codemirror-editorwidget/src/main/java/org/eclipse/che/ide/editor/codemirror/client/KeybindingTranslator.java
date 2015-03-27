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
package org.eclipse.che.ide.editor.codemirror.client;

import org.eclipse.che.ide.editor.codemirrorjso.client.CodeMirrorOverlay;
import org.eclipse.che.ide.jseditor.client.keymap.Keybinding;
import com.google.gwt.core.client.JsArrayString;

/**
 * Converts Keybindings {@link Keybinding} representation to codemirror internal representation.
 *
 */
public class KeybindingTranslator {

    public static final String SHIFT = "Shift";
    public static final String CTRL= "Ctrl";
    public static final String CMD = "Cmd";
    public static final String ALT = "Alt";

    public static final String translateKeyBinding(final Keybinding keybinding,
                                                   final CodeMirrorOverlay codemirror) {
        final StringBuilder sb = new StringBuilder();

        /*
         * From CodeMirror doc at https://codemirror.net/doc/manual.html#keymaps
         *
         * Keys are identified either by name or by character. The CodeMirror.keyNames object defines names
         * for common keys and associates them with their key codes. Examples of names defined here are
         * Enter, F5, and Q. These can be prefixed with Shift-, Cmd-, Ctrl-, and Alt- (in that order!) to
         * specify a modifier. So for example, Shift-Ctrl-Space would be a valid key identifier.
         */

        // handle modifiers
        if (keybinding.isShift()) {
            sb.append(SHIFT).append("-");
        }
        if (keybinding.isCmd()) {
            sb.append(CMD).append("-");
        }
        if (keybinding.isControl()) {
            sb.append(CTRL).append("-");
        }
        if (keybinding.isAlt()) {
            sb.append(ALT).append("-");
        }
        // now add the key
        final JsArrayString keyNames = codemirror.keyNames();
        final String keyname = keyNames.get(keybinding.getKeyCode());
        if (keyname == null) {
            return null;
        }
        sb.append(keyname);

        return sb.toString();
    }
}
