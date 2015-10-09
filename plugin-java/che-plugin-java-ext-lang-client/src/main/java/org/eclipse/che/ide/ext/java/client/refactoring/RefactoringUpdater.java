/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.java.client.refactoring;

import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.project.node.HasStorablePath;
import org.eclipse.che.ide.api.project.node.Node;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.jseditor.client.document.Document;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter;
import org.eclipse.che.ide.part.explorer.project.ProjectExplorerPresenter;
import org.eclipse.che.ide.project.node.FileReferenceNode;

/**
 * Utility class for the refactoring operations.
 * It is needed for refreshing the project tree, updating content of the opening editors.
 *
 * @author Valeriy Svydenko
 */
@Singleton
public class RefactoringUpdater {
    private final EditorAgent              editorAgent;
    private final EventBus                 eventBus;
    private final ProjectExplorerPresenter projectExplorer;

    private String pathToMove;

    @Inject
    public RefactoringUpdater(EditorAgent editorAgent,
                              EventBus eventBus,
                              ProjectExplorerPresenter projectExplorer) {
        this.editorAgent = editorAgent;
        this.eventBus = eventBus;
        this.projectExplorer = projectExplorer;
    }

    /** Refreshes all open editors. */
    public void refreshOpenEditors() {
        Promise<Void> promise = Promises.resolve(null);

        for (final EditorPartPresenter editor : editorAgent.getOpenedEditors().values()) {
            final VirtualFile file = editor.getEditorInput().getFile();

            promise.thenPromise(getContent(file))
                   .thenPromise(updateEditor(editor))
                   .catchErrorPromise(onGetContentFailed(file));
        }
    }

    private Function<Void, Promise<String>> getContent(final VirtualFile file) {
        return new Function<Void, Promise<String>>() {
            @Override
            public Promise<String> apply(Void arg) throws FunctionException {
                return file.getContent();
            }
        };
    }

    private Function<String, Promise<Object>> updateEditor(final EditorPartPresenter editor) {
        return new Function<String, Promise<Object>>() {
            @Override
            public Promise<Object> apply(String content) throws FunctionException {
                Document document = ((EmbeddedTextEditorPresenter)editor).getDocument();
                document.replace(0, document.getContents().length(), content);

                return Promises.resolve(null);
            }
        };
    }

    private Function<PromiseError, Promise<Object>> onGetContentFailed(final VirtualFile file) {
        return new Function<PromiseError, Promise<Object>>() {
            @Override
            public Promise<Object> apply(PromiseError arg) throws FunctionException {
                return reopenMovedFile(file);
            }
        };
    }

    private Function<Node, Promise<Object>> openFile() {
        return new Function<Node, Promise<Object>>() {
            @Override
            public Promise<Object> apply(Node node) throws FunctionException {
                if (node instanceof FileReferenceNode) {
                    eventBus.fireEvent(new FileEvent((FileReferenceNode)node, FileEvent.FileOperation.OPEN));
                }
                return Promises.resolve(null);
            }
        };
    }

    /** Sets destinations for moving resources. */
    public void setPathToMove(String pathToMove) {
        this.pathToMove = pathToMove;
    }

    /** Refreshes project tree. */
    public void refreshProjectTree() {
        projectExplorer.reloadChildren();
    }

    private Promise<Object> reopenMovedFile(final VirtualFile file) {
        eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.CLOSE));

        final String newPathToFile = pathToMove + file.getPath().substring(file.getPath().lastIndexOf('/'));

        //TODO It is temporary solution. This timer need for waiting when file will moved.
        new Timer() {
            @Override
            public void run() {
                projectExplorer.getNodeByPath(new HasStorablePath.StorablePath(newPathToFile), true).thenPromise(openFile());
            }
        }.schedule(300);

        return Promises.resolve(null);
    }
}
