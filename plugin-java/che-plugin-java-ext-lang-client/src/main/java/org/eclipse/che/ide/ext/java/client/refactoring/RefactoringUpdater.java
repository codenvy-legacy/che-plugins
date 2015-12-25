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

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.event.FileContentUpdateEvent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.node.HasStorablePath.StorablePath;
import org.eclipse.che.ide.api.project.node.Node;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.ChangeInfo;
import org.eclipse.che.ide.jseditor.client.texteditor.TextEditor;
import org.eclipse.che.ide.part.explorer.project.ProjectExplorerPresenter;
import org.eclipse.che.ide.project.node.FileReferenceNode;
import org.eclipse.che.ide.rest.AsyncRequestCallback;

import java.util.Iterator;
import java.util.List;

import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;

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
    private final JavaLocalizationConstant locale;
    private final NotificationManager      notificationManager;
    private final ProjectServiceClient     projectServiceClient;
    private final AppContext               appContext;

    @Inject
    public RefactoringUpdater(EditorAgent editorAgent,
                              EventBus eventBus,
                              NotificationManager notificationManager,
                              AppContext appContext,
                              ProjectServiceClient projectServiceClient,
                              ProjectExplorerPresenter projectExplorer,
                              JavaLocalizationConstant locale) {
        this.editorAgent = editorAgent;
        this.notificationManager = notificationManager;
        this.projectServiceClient = projectServiceClient;
        this.eventBus = eventBus;
        this.projectExplorer = projectExplorer;
        this.locale = locale;
        this.appContext = appContext;
    }

    /**
     * Updates all open editors which have changes and refreshes the project tree.
     *
     * @param changes
     *         applied changes
     */
    public void updateAfterRefactoring(RefactorInfo refactoringInfo, List<ChangeInfo> changes) {
        removeSelectedNodes(refactoringInfo);
        Promise<Void> promise = Promises.resolve(null);
        processChanges(promise, editorAgent.getOpenedEditors().values().iterator(), changes).then(setActiveEditorFocus());
    }

    private void removeSelectedNodes(RefactorInfo refactoringInfo) {
        if (refactoringInfo == null || refactoringInfo.getSelectedItems() == null) {
            return;
        }
        for (Object operatedItem : refactoringInfo.getSelectedItems()) {
            if (operatedItem instanceof Node) {
                projectExplorer.removeNode((Node)operatedItem, false);
            }
        }
    }

    private Promise<Void> processChanges(Promise<Void> promise, Iterator<EditorPartPresenter> iterator, List<ChangeInfo> changes) {
        if (!iterator.hasNext()) {
            return promise;
        }

        final EditorPartPresenter editor = iterator.next();
        final ChangeInfo change = getActiveChange(changes, editor);

        if (change == null) {
            return processChanges(promise, iterator, changes);
        }

        changes.remove(change);

        Promise<Void> derivedPromise;

        switch (change.getName()) {
            case RENAME_COMPILATION_UNIT:
            case MOVE:
                derivedPromise = prepareMovePromise(promise, editor, change);
                break;
            case UPDATE:
                derivedPromise = prepareUpdatePromise(promise, editor, change);
                break;
            default:
                return processChanges(promise, iterator, changes);
        }

        return processChanges(derivedPromise, iterator, changes);
    }

    private Promise<Void> prepareUpdatePromise(Promise<Void> promise, final EditorPartPresenter editor, final ChangeInfo change) {
        return promise.thenPromise(new Function<Void, Promise<Void>>() {
            @Override
            public Promise<Void> apply(Void arg) throws FunctionException {

                if (!Strings.isNullOrEmpty(change.getOldPath())) {
                    return doUpdate(editor, change);
                }

                projectServiceClient.getItem(appContext.getWorkspace().getId(), change.getPath(), new AsyncRequestCallback<ItemReference>() {
                    @Override
                    protected void onSuccess(ItemReference result) {
                        eventBus.fireEvent(new FileContentUpdateEvent(change.getPath()));
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        //do nothing
                    }
                });

                return Promises.resolve(null);
            }
        });
    }

    private Promise<Void> prepareMovePromise(Promise<Void> promise, final EditorPartPresenter editor, final ChangeInfo change) {
        return promise.thenPromise(new Function<Void, Promise<Void>>() {
            @Override
            public Promise<Void> apply(Void ignored) throws FunctionException {
                return doUpdate(editor, change);
            }
        });
    }

    private ChangeInfo getActiveChange(List<ChangeInfo> changes, EditorPartPresenter openEditor) {
        for (ChangeInfo change : changes) {
            String fPath = openEditor.getEditorInput().getFile().getPath();
            if (change.getOldPath().equals(fPath) || change.getPath().equals(fPath)) {
                return change;
            }
        }
        return null;
    }

    private Promise<Void> doUpdate(EditorPartPresenter editor, final ChangeInfo change) {
        return projectExplorer.getNodeByPath(new StorablePath(change.getPath()), true, false)
                              .thenPromise(updateEditorInput(editor, change))
                              .catchError(onNodeNotFound());
    }

    private Operation<Void> setActiveEditorFocus() {
        return new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                projectExplorer.reloadChildren();
                EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
                if (activeEditor instanceof TextEditor) {
                    ((TextEditor)activeEditor).setFocus();
                }
            }
        };
    }

    private Function<Node, Promise<Void>> updateEditorInput(EditorPartPresenter editor, final ChangeInfo change) {
        final FileReferenceNode file = (FileReferenceNode)editor.getEditorInput().getFile();
        return new Function<Node, Promise<Void>>() {
            @Override
            public Promise<Void> apply(Node node) throws FunctionException {
                if (!(node instanceof FileReferenceNode)) {
                    return Promises.resolve(null);
                }

                file.setData(((FileReferenceNode)node).getData());
                file.setParent(node.getParent());
                editorAgent.updateEditorNode(change.getOldPath(), file);
                eventBus.fireEvent(new FileContentUpdateEvent(change.getPath()));

                return Promises.resolve(null);
            }
        };
    }

    private Operation<PromiseError> onNodeNotFound() {
        return new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                notificationManager.notify(locale.failedToProcessRefactoringOperation(), arg.getMessage(), FAIL, true);
            }
        };
    }
}
