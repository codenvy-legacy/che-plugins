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

/** Description of a codemirror mode. */
public class CMModeOverlay extends JavaScriptObject {

    /** JSO mandated protected constructor. */
    protected CMModeOverlay() {
    }

    public final native String getName() /*-{
        return this.name;
    }-*/;

    /* function startState(), returns a state instance - optional */

    /* function token(stream, state, returns string=classname|null - must consume at lerast one char from stream */

    /* function copyState(state), return state - optional */

    /* function indent(state, textAfter), returns int (number of spaces) - optional */

    /* function lineComment, blockComment, blockCommentStart, blockCommentEnd, blockCommentLead, return strings - optional */

    /* string property electricChars - optional */

    /* regexp property electricInput - optional */
}
