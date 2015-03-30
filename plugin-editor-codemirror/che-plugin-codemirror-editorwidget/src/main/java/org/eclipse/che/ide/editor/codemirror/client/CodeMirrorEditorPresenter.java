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

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.debug.BreakpointManager;
import org.eclipse.che.ide.jseditor.client.JsEditorConstants;
import org.eclipse.che.ide.jseditor.client.codeassist.CodeAssistantFactory;
import org.eclipse.che.ide.jseditor.client.debug.BreakpointRendererFactory;
import org.eclipse.che.ide.jseditor.client.document.DocumentStorage;
import org.eclipse.che.ide.jseditor.client.filetype.FileTypeIdentifier;
import org.eclipse.che.ide.jseditor.client.gutter.Gutter;
import org.eclipse.che.ide.jseditor.client.gutter.HasGutter;
import org.eclipse.che.ide.jseditor.client.minimap.HasMinimap;
import org.eclipse.che.ide.jseditor.client.minimap.Minimap;
import org.eclipse.che.ide.jseditor.client.quickfix.QuickAssistantFactory;
import org.eclipse.che.ide.jseditor.client.texteditor.EditorModule;
import org.eclipse.che.ide.jseditor.client.texteditor.EditorWidget;
import org.eclipse.che.ide.jseditor.client.texteditor.EditorWidgetFactory;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPartView;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * {@link EmbeddedTextEditorPresenter} using codemirror.
 * This class is only defined to allow the Gin binding to be performed.
 */
public class CodeMirrorEditorPresenter extends EmbeddedTextEditorPresenter<CodeMirrorEditorWidget> implements HasMinimap,
                                                                                                              HasGutter {

    @AssistedInject
    public CodeMirrorEditorPresenter(final CodeAssistantFactory codeAssistantFactory,
                                     final BreakpointManager breakpointManager,
                                     final BreakpointRendererFactory breakpointRendererFactory,
                                     final DialogFactory dialogFactory,
                                     final DocumentStorage documentStorage,
                                     final JsEditorConstants constant,
                                     @Assisted final EditorWidgetFactory<CodeMirrorEditorWidget> editorWigetFactory,
                                     final EditorModule<CodeMirrorEditorWidget> editorModule,
                                     final EmbeddedTextEditorPartView editorView,
                                     final EventBus eventBus,
                                     final FileTypeIdentifier fileTypeIdentifier,
                                     final QuickAssistantFactory quickAssistantFactory,
                                     final Resources resources,
                                     final WorkspaceAgent workspaceAgent) {
        super(codeAssistantFactory, breakpointManager, breakpointRendererFactory, dialogFactory, documentStorage, constant, editorWigetFactory,
              editorModule, editorView, eventBus, fileTypeIdentifier, quickAssistantFactory, resources, workspaceAgent);
    }

    @Override
    public Minimap getMinimap() {
        final EditorWidget editorWidget = getEditorWidget();
        if (editorWidget instanceof HasMinimap) {
            return ((HasMinimap)editorWidget).getMinimap();
        } else {
            throw new IllegalStateException("incorrect editor state");
        }
    }

    @Override
    public Gutter getGutter() {
        final EditorWidget editorWidget = getEditorWidget();
        if (editorWidget instanceof HasGutter) {
            return ((HasGutter)editorWidget).getGutter();
        } else {
            throw new IllegalStateException("incorrect editor state");
        }
    }
}
