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

import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.java.client.refactoring.RefactorInfo;
import org.eclipse.che.ide.ext.java.client.refactoring.RefactoringUpdater;
import org.eclipse.che.ide.ext.java.client.refactoring.move.wizard.MovePresenter;
import org.eclipse.che.ide.ext.java.client.refactoring.service.RefactoringServiceClient;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.ChangeEnabledState;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.ChangePreview;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringChange;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringPreview;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringSession;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringStatus;

import static org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringStatus.OK;

/**
 * @author Dmitry Shnurenko
 */
@Singleton
public class PreviewPresenter implements PreviewView.ActionDelegate {

    private final PreviewView              view;
    private final RefactoringUpdater       refactoringUpdater;
    private final DtoFactory               dtoFactory;
    private final RefactoringServiceClient refactoringService;
    private final Provider<MovePresenter>  movePresenterProvider;

    private RefactorInfo       refactorInfo;
    private RefactoringSession session;

    @Inject
    public PreviewPresenter(PreviewView view,
                            RefactoringUpdater refactoringUpdater,
                            Provider<MovePresenter> movePresenterProvider,
                            DtoFactory dtoFactory,
                            RefactoringServiceClient refactoringService) {
        this.view = view;
        this.refactoringUpdater = refactoringUpdater;
        this.dtoFactory = dtoFactory;
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

    /** {@inheritDoc} */
    @Override
    public void onAcceptButtonClicked() {
        refactoringService.applyRefactoring(session).then(new Operation<RefactoringStatus>() {
            @Override
            public void apply(RefactoringStatus arg) throws OperationException {
                if (arg.getSeverity() == OK) {
                    view.hide();
                    refactoringUpdater.refreshProjectTree();
                    refactoringUpdater.refreshOpenEditors();
                } else {
                    view.showErrorMessage(arg);
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onBackButtonClicked() {
        MovePresenter movePresenter = movePresenterProvider.get();

        movePresenter.show(refactorInfo);

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

}
