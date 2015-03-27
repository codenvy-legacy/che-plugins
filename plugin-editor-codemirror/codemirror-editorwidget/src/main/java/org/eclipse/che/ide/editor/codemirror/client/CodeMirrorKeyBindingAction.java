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

/**
 * An action executed on a keybinding in the editor.
 * @param <T> the type of the 'this' instance
 */
public interface CodeMirrorKeyBindingAction<T> {

    /** The triggered action. */
    void action(T thisInstance);
}
