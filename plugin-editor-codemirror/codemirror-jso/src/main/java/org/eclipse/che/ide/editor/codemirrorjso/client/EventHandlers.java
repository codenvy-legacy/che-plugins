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
import com.google.gwt.core.client.JsArrayMixed;

/** Interfaces for codemirror event handlers. */
public interface EventHandlers {

    /** Event handlers with no parameters. */
    public interface EventHandlerNoParameters {
        void onEvent();
    }

    /** Event handlers with one parameters. */
    public interface EventHandlerOneParameter<T extends JavaScriptObject> {
        void onEvent(T param);
    }

    /** Event handlers with multiple parameters. */
    public interface EventHandlerMixedParameters {
        void onEvent(JsArrayMixed param);
    }
}
