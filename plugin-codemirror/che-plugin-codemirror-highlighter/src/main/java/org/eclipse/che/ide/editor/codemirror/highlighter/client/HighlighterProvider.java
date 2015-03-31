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

import java.util.Collections;

import javax.inject.Inject;

import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.editor.codemirrorjso.client.CodeMirrorOverlay;

public class HighlighterProvider {

    private final HighlighterInstance highlightInstance;
    private final HighlighterInitializer highlighterInitializer;

    @Inject
    public HighlighterProvider(final HighlighterInitializer highlighterInitializer,
                               final HighlighterInstance highlightInstance) {
        this.highlightInstance = highlightInstance;
        this.highlighterInitializer = highlighterInitializer;
        if (this.highlightInstance.getPromise() == null) {
            this.highlighterInitializer.init();
        }
    }

    public void get(final String mode, final int tabSize, final HighlightModeCallback callback) {
        final Promise<CodeMirrorOverlay> modePromise = highlighterInitializer.initModes(Collections.singletonList(mode));
        modePromise.then(new Operation<CodeMirrorOverlay>() {
            @Override
            public void apply(final CodeMirrorOverlay codemirror) throws OperationException {
                highlighterInitializer.initModes(Collections.singletonList(mode));
            }
        });
        modePromise.then(new Operation<CodeMirrorOverlay>() {
            @Override
            public void apply(final CodeMirrorOverlay codemirror) throws OperationException {
                callback.onReady(new HighlightMode(mode, tabSize, codemirror));
            }
        });
        modePromise.catchError(new Operation<PromiseError>() {
            @Override
            public void apply(final PromiseError cause) throws OperationException {
                callback.onFailure();
            }
        });
    }

    public interface HighlightModeCallback {
        void onReady(HighlightMode runmode);

        void onFailure();
    }
}
