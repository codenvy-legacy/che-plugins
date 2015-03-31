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

public class BaseCodemirrorInitializer extends AbstractCodemirrorInitializer {

    private final BaseCodemirrorPromise baseCodemirrorPromise;
    private final MinimalCodemirrorInitializer minimalCodemirrorInitializer;

    @Inject
    public BaseCodemirrorInitializer(final RequireJsLoader requireJsLoader,
                                     final BasePathConstant basePathConstant,
                                     final BaseCodemirrorPromise baseCodemirrorPromise,
                                     final MinimalCodemirrorInitializer minimalCodemirrorInitializer) {
        super(requireJsLoader, basePathConstant);

        this.baseCodemirrorPromise = baseCodemirrorPromise;
        this.minimalCodemirrorInitializer = minimalCodemirrorInitializer;
    }

    public Promise<CodeMirrorOverlay> init() {
        if (this.baseCodemirrorPromise.getPromise() == null) {
            this.minimalCodemirrorInitializer.init();
            // can be done separately, if the scripts are already loading, requirejs will take that into account
            final Promise<CodeMirrorOverlay> result = initScripts(getScripts());

            this.baseCodemirrorPromise.setPromise(result);
        }
        return this.baseCodemirrorPromise.getPromise();
    }

    @Override
    protected List<String> getScripts() {
        return Arrays.asList(new String[]{
                                          // /lib/codemirror added elsewhere

                                          // hints
                                          "addon/hint/show-hint",

                                          // pair matching
                                          "addon/edit/closebrackets",
                                          "addon/edit/closetag",
                                          "addon/edit/matchbrackets",
                                          "addon/edit/matchtags",

                                          "addon/selection/mark-selection",
                                          "addon/selection/active-line",

                                          // for search
                                          "addon/search/search",
                                          "addon/dialog/dialog",
                                          "addon/search/searchcursor",
                                          "addon/search/match-highlighter",

                                          // folding
                                          "addon/fold/foldcode",
                                          "addon/fold/foldgutter",

                                          "addon/scroll/simplescrollbars",
                                          "addon/scroll/annotatescrollbar",
                                          "addon/scroll/scrollpastend",
                                          "addon/search/matchesonscrollbar",
        });
    }
}
