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
package org.eclipse.che.ide.editor.codemirror.base.client;

import java.util.Arrays;
import java.util.List;

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.editor.codemirror.resources.client.BasePathConstant;
import org.eclipse.che.ide.editor.codemirrorjso.client.CodeMirrorOverlay;
import org.eclipse.che.ide.jseditor.client.requirejs.RequireJsLoader;
import com.google.inject.Inject;

public class MinimalCodemirrorInitializer extends AbstractCodemirrorInitializer {

    private final MinimalCodemirrorPromise minimalCodemirrorPromise;

    @Inject
    public MinimalCodemirrorInitializer(final RequireJsLoader requireJsLoader,
                                        final BasePathConstant basePathConstant,
                                        final MinimalCodemirrorPromise minimalCodemirrorPromise) {
        super(requireJsLoader, basePathConstant);

        this.minimalCodemirrorPromise = minimalCodemirrorPromise;
    }

    public Promise<CodeMirrorOverlay> init() {
        if (this.minimalCodemirrorPromise == null) {
            Promise<CodeMirrorOverlay> result = initScripts(getScripts());
            this.minimalCodemirrorPromise.setPromise(result);
        }
        return this.minimalCodemirrorPromise.getPromise();
    }

    @Override
    protected List<String> getScripts() {
        return Arrays.asList(new String[]{
                                          // /lib/codemirror added elsewhere

                                          // library of modes
                                          "mode/meta",

                                          "addon/selection/mark-selection",
                                          "addon/selection/active-line",

                                          "addon/scroll/simplescrollbars",
                                          "addon/scroll/scrollpastend",
        });
    }
}
