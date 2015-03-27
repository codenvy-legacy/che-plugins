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
package org.eclipse.che.ide.editor.orion.client;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.debug.BreakpointManager;
import org.eclipse.che.ide.jseditor.client.JsEditorConstants;
import org.eclipse.che.ide.jseditor.client.codeassist.CodeAssistantFactory;
import org.eclipse.che.ide.jseditor.client.debug.BreakpointRendererFactory;
import org.eclipse.che.ide.jseditor.client.document.DocumentStorage;
import org.eclipse.che.ide.jseditor.client.filetype.FileTypeIdentifier;
import org.eclipse.che.ide.jseditor.client.quickfix.QuickAssistantFactory;
import org.eclipse.che.ide.jseditor.client.texteditor.EditorModule;
import org.eclipse.che.ide.jseditor.client.texteditor.EditorWidgetFactory;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPartView;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * {@link EmbeddedTextEditorPresenter} using orion.
 * This class is only defined to allow the Gin binding to be performed.
 */
public class OrionEditorPresenter extends EmbeddedTextEditorPresenter<OrionEditorWidget> {

    @AssistedInject
    public OrionEditorPresenter(final CodeAssistantFactory codeAssistantFactory,
                                     final BreakpointManager breakpointManager,
                                     final BreakpointRendererFactory breakpointRendererFactory,
                                     final DialogFactory dialogFactory,
                                     final DocumentStorage documentStorage,
                                     final JsEditorConstants constant,
                                     @Assisted final EditorWidgetFactory<OrionEditorWidget> editorWigetFactory,
                                     final EditorModule<OrionEditorWidget> editorModule,
                                     final EmbeddedTextEditorPartView editorView,
                                     final EventBus eventBus,
                                     final FileTypeIdentifier fileTypeIdentifier,
                                     final QuickAssistantFactory quickAssistantFactory,
                                     final Resources resources,
                                     final WorkspaceAgent workspaceAgent) {
        super(codeAssistantFactory, breakpointManager, breakpointRendererFactory, dialogFactory, documentStorage, constant, editorWigetFactory,
              editorModule, editorView, eventBus, fileTypeIdentifier, quickAssistantFactory, resources, workspaceAgent);
    }
}
