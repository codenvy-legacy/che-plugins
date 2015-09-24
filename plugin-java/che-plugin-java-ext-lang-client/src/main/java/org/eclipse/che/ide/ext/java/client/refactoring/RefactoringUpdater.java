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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.event.RefreshProjectTreeEvent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.api.project.tree.generic.StorableNode;
import org.eclipse.che.ide.api.selection.SelectionAgent;
import org.eclipse.che.ide.jseditor.client.document.Document;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter;

import java.util.List;

/**
 * Utility class for the refactoring operations.
 * It is needed for refreshing the project tree, updating content of the opening editors.
 *
 * @author Valeriy Svydenko
 */
@Singleton
public class RefactoringUpdater {
    private final EditorAgent         editorAgent;
    private final SelectionAgent      selectionAgent;
    private final EventBus            eventBus;
    private final NotificationManager notificationManager;
    private final AppContext          appContext;

    private String pathToMove;

    @Inject
    public RefactoringUpdater(EditorAgent editorAgent,
                              SelectionAgent selectionAgent,
                              EventBus eventBus,
                              NotificationManager notificationManager,
                              AppContext appContext) {
        this.editorAgent = editorAgent;
        this.selectionAgent = selectionAgent;
        this.eventBus = eventBus;
        this.notificationManager = notificationManager;
        this.appContext = appContext;
    }

    /** Refreshes all open editors. */
    public void refreshOpenEditors() {
        for (final EditorPartPresenter editor : editorAgent.getOpenedEditors().values()) {
            final VirtualFile file = editor.getEditorInput().getFile();
            String path = file.getPath();
            file.getContent(new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    reopenMovedFile(file);
                }

                @Override
                public void onSuccess(String result) {
                    Document document = ((EmbeddedTextEditorPresenter)editor).getDocument();
                    document.replace(0, document.getContents().length(), result);
                }
            });
        }
    }

    /** Sets destinations for moving resources. */
    public void setPathToMove(String pathToMove) {
        this.pathToMove = pathToMove;
    }

    /** Refreshes project tree. */
    public void refreshProjectTree() {
        List<?> selectionItems = selectionAgent.getSelection().getAllElements();
        for (Object selectionItem : selectionItems) {
            eventBus.fireEvent(new RefreshProjectTreeEvent(((StorableNode)selectionItem).getParent()));
        }

        appContext.getCurrentProject().getCurrentTree().getNodeByPath(pathToMove, new AsyncCallback<TreeNode<?>>() {
            @Override
            public void onFailure(Throwable caught) {
                notificationManager.showError("Can not find: " + pathToMove);
            }

            @Override
            public void onSuccess(TreeNode<?> result) {
                eventBus.fireEvent(new RefreshProjectTreeEvent(result.getParent()));
            }
        });
    }

    private void reopenMovedFile(final VirtualFile file) {
        eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.CLOSE));

        final String newPathToFile = pathToMove + file.getPath().substring(file.getPath().lastIndexOf('/'));

        appContext.getCurrentProject().getCurrentTree().getNodeByPath(newPathToFile, new AsyncCallback<TreeNode<?>>() {
            @Override
            public void onFailure(Throwable caught) {
                notificationManager.showError("Can not find a file: " + newPathToFile);
            }

            @Override
            public void onSuccess(TreeNode<?> result) {
                if (result instanceof FileNode) {
                    eventBus.fireEvent(new FileEvent((FileNode)result, FileEvent.FileOperation.OPEN));
                }
            }
        });
    }
}
