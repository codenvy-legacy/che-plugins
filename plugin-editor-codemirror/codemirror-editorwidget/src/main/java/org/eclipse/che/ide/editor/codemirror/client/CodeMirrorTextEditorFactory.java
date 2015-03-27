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

import javax.inject.Inject;

import org.eclipse.che.ide.jseditor.client.texteditor.EditorWidgetFactory;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenterFactory;

public class CodeMirrorTextEditorFactory {

    @Inject
    private EditorWidgetFactory<CodeMirrorEditorWidget> widgetFactory;

    @Inject
    private EmbeddedTextEditorPresenterFactory<CodeMirrorEditorWidget> presenterFactory;

    public EmbeddedTextEditorPresenter<CodeMirrorEditorWidget> createTextEditor() {
        return this.presenterFactory.createTextEditor(this.widgetFactory);
    }
}
