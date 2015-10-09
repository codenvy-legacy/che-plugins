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
package org.eclipse.che.ide.ext.java.client.refactoring.preview;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.event.FileContentUpdateEvent;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.java.client.refactoring.RefactorInfo;
import org.eclipse.che.ide.ext.java.client.refactoring.move.wizard.MovePresenter;
import org.eclipse.che.ide.ext.java.client.refactoring.rename.wizard.RenamePresenter;
import org.eclipse.che.ide.ext.java.client.refactoring.service.RefactoringServiceClient;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.ChangeEnabledState;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.ChangePreview;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringChange;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringPreview;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringSession;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringStatus;
import org.eclipse.che.ide.part.explorer.project.ProjectExplorerPresenter;

import static org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringStatus.OK;

/**
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@Singleton
public class PreviewPresenter implements PreviewView.ActionDelegate {

    private final PreviewView               view;
    private final Provider<RenamePresenter> renamePresenterProvider;
    private final DtoFactory                dtoFactory;
    private final EventBus                  eventBus;
    private final EditorAgent               editorAgent;
    private final ProjectExplorerPresenter  projectExplorer;
    private final RefactoringServiceClient  refactoringService;
    private final Provider<MovePresenter>   movePresenterProvider;

    private RefactorInfo       refactorInfo;
    private RefactoringSession session;

    @Inject
    public PreviewPresenter(PreviewView view,
                            Provider<MovePresenter> movePresenterProvider,
                            Provider<RenamePresenter> renamePresenterProvider,
                            DtoFactory dtoFactory,
                            EventBus eventBus,
                            EditorAgent editorAgent,
                            ProjectExplorerPresenter projectExplorer,
                            RefactoringServiceClient refactoringService) {
        this.view = view;
        this.renamePresenterProvider = renamePresenterProvider;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;
        this.editorAgent = editorAgent;
        this.projectExplorer = projectExplorer;
        this.refactoringService = refactoringService;
        this.view.setDelegate(this);

        this.movePresenterProvider = movePresenterProvider;
    }

    public void show(String refactoringSessionId, RefactorInfo refactorInfo) {
        this.refactorInfo = refactorInfo;

        session = dtoFactory.createDto(RefactoringSession.class);
        session.setSessionId(refactoringSessionId);

        refactoringService.getRefactoringPreview(session).then(new Operation<RefactoringPreview>() {
            @Override
            public void apply(RefactoringPreview changes) throws OperationException {
                view.setTreeOfChanges(changes);
            }
        });

        view.show();
    }

    /**
     * Set a title of the window.
     *
     * @param title
     *         the name of the preview window
     */
    public void setTitle(String title) {
        view.setTitle(title);
    }

    /** {@inheritDoc} */
    @Override
    public void onAcceptButtonClicked() {
        refactoringService.applyRefactoring(session).then(new Operation<RefactoringStatus>() {
            @Override
            public void apply(RefactoringStatus arg) throws OperationException {
                if (arg.getSeverity() == OK) {
                    view.hide();
                    //TODO It is temporary solution. We need to know which files have changes.
                    projectExplorer.reloadChildren();
                    updateAllEditors();
                } else {
                    view.showErrorMessage(arg);
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onBackButtonClicked() {
        if (refactorInfo == null || refactorInfo.getMoveType() == null) {
            RenamePresenter renamePresenter = renamePresenterProvider.get();
            renamePresenter.show(refactorInfo);
        } else {
            MovePresenter movePresenter = movePresenterProvider.get();
            movePresenter.show(refactorInfo);
        }

        view.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void onEnabledStateChanged(final RefactoringPreview change) {
        ChangeEnabledState changeEnableState = dtoFactory.createDto(ChangeEnabledState.class);
        changeEnableState.setChangeId(change.getId());
        changeEnableState.setSessionId(session.getSessionId());
        changeEnableState.setEnabled(change.isEnabled());

        refactoringService.changeChangeEnabledState(changeEnableState).then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                onSelectionChanged(change);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onSelectionChanged(RefactoringPreview change) {
        RefactoringChange refactoringChanges = dtoFactory.createDto(RefactoringChange.class);
        refactoringChanges.setChangeId(change.getId());
        refactoringChanges.setSessionId(session.getSessionId());

        Promise<ChangePreview> changePreviewPromise = refactoringService.getChangePreview(refactoringChanges);
        changePreviewPromise.then(new Operation<ChangePreview>() {
            @Override
            public void apply(ChangePreview arg) throws OperationException {
                view.showDiff(arg);
            }
        });
    }

    private void updateAllEditors() {
        for (EditorPartPresenter editor : editorAgent.getOpenedEditors().values()) {
            String path = editor.getEditorInput().getFile().getPath();
            eventBus.fireEvent(new FileContentUpdateEvent(path));
        }
    }

}
