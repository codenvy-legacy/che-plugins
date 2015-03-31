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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.callback.CallbackPromiseHelper;
import org.eclipse.che.api.promises.client.callback.CallbackPromiseHelper.Call;
import org.eclipse.che.ide.editor.codemirror.resources.client.BasePathConstant;
import org.eclipse.che.ide.editor.codemirrorjso.client.CodeMirrorOverlay;

import org.eclipse.che.ide.jseditor.client.requirejs.RequireJsLoader;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;

public class HighlighterInitializer {

    /** The logger. */
    private static final Logger  LOG = Logger.getLogger(HighlighterInitializer.class.getSimpleName());

    /** The base path for codemirror resources. */
    private final String codemirrorBase;

    private final RequireJsLoader requireJsLoader;

    private final HighlighterInstance highlighterInstance;

    @Inject
    public HighlighterInitializer(final RequireJsLoader requireJsLoader,
                            final BasePathConstant basePathConstant,
                            final HighlighterInstance runmodeInstance) {

        this.codemirrorBase = basePathConstant.basePath();
        this.requireJsLoader = requireJsLoader;
        this.highlighterInstance = runmodeInstance;
    }

    public void init() {
        this.highlighterInstance.setPromise(initModes(Collections.<String> emptyList()));
    }

    public Promise<CodeMirrorOverlay> initModes(final List<String> modes) {

        final String[] baseScripts = new String[]{codemirrorBase + "lib/codemirror",
                                              codemirrorBase + "mode/meta",
                                              codemirrorBase + "addon/mode/loadmode",
                                              codemirrorBase + "addon/scroll/simplescrollbars",
                                              codemirrorBase + "addon/scroll/scrollpastend",
                                              };
        final List<String> scriptList = new ArrayList<>();
        for (final String script : baseScripts) {
            scriptList.add(script);
        }
        for (final String mode : modes) {
            scriptList.add(codemirrorBase + "mode/" + mode + "/" + mode);
        }

        final Call<JavaScriptObject[], Throwable> requireCall = new Call<JavaScriptObject[], Throwable>() {
            @Override
            public void makeCall(final Callback<JavaScriptObject[], Throwable> callback) {
                requireJsLoader.require(callback, scriptList.toArray(new String[scriptList.size()]));
            }
        };
        final Promise<JavaScriptObject[]> requirePromise = CallbackPromiseHelper.createFromCallback(requireCall);

        requirePromise.then(new Operation<JavaScriptObject[]>() {
            @Override
            public void apply(final JavaScriptObject[] result) throws OperationException {
                LOG.log(Level.INFO, "Obtained codemirror instance with runmode: " + result[0]);
            }
        });
        requirePromise.catchError(new Operation<PromiseError>() {
            @Override
            public void apply(final PromiseError result) throws OperationException {
                LOG.log(Level.SEVERE, "Failed to obtain codemirror instance");
                LOG.fine(result.toString());
            }
        });
        final Promise<CodeMirrorOverlay> highlightPromise = requirePromise.then(new Function<JavaScriptObject[], CodeMirrorOverlay>() {
            @Override
            public CodeMirrorOverlay apply(final JavaScriptObject[] args) throws FunctionException {
                return args[0].cast();
            }
        });
        return highlightPromise;
    }
}
