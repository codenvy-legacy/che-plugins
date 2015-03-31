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
package org.eclipse.che.ide.editor.codemirror.highlighter.client;

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.editor.codemirrorjso.client.CodeMirrorOverlay;

public class HighlighterInstance {

    private Promise<CodeMirrorOverlay> highlighterPromise;

    public void setPromise(final Promise<CodeMirrorOverlay> runmodePromise) {
        this.highlighterPromise = runmodePromise;
    }

    public Promise<CodeMirrorOverlay> getPromise() {
        return this.highlighterPromise;
    }
}
