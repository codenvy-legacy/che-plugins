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

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;

import org.eclipse.che.api.promises.client.Operation;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringStatus.OK;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class PreviewPresenterTest {
    public static final String SESSION_ID = "sessionId";

    @Mock
    private PreviewView              view;
    @Mock
    private RefactoringUpdater       refactoringUpdater;
    @Mock
    private Provider<MovePresenter>  movePresenterProvider;
    @Mock
    private DtoFactory               dtoFactory;
    @Mock
    private RefactoringServiceClient refactoringService;

    @Mock
    private ChangeEnabledState          changeEnableState;
    @Mock
    private RefactoringChange           refactoringChanges;
    @Mock
    private RefactorInfo                refactorInfo;
    @Mock
    private RefactoringSession          refactoringSession;
    @Mock
    private RefactoringPreview          refactoringPreview;
    @Mock
    private Promise<RefactoringPreview> refactoringPreviewPromise;
    @Mock
    private RefactoringStatus           refactoringStatus;
    @Mock
    private ChangePreview               changePreview;
    @Mock
    private Promise<RefactoringStatus>  refactoringStatusPromise;
    @Mock
    private Promise<ChangePreview>      changePreviewPromise;
    @Mock
    private Promise<Void>               changeEnableStatePromise;

    @Captor
    private ArgumentCaptor<Operation<RefactoringPreview>> refactoringPreviewOperation;
    @Captor
    private ArgumentCaptor<Operation<RefactoringStatus>>  refactoringStatusOperation;
    @Captor
    private ArgumentCaptor<Operation<ChangePreview>>      changePreviewOperation;
    @Captor
    private ArgumentCaptor<Operation<Void>>               changeEnableStateOperation;


    private PreviewPresenter presenter;

    @Before
    public void setUp() throws Exception {
        when(dtoFactory.createDto(RefactoringSession.class)).thenReturn(refactoringSession);
        when(dtoFactory.createDto(ChangeEnabledState.class)).thenReturn(changeEnableState);
        when(dtoFactory.createDto(RefactoringChange.class)).thenReturn(refactoringChanges);
        when(refactoringService.getRefactoringPreview(refactoringSession)).thenReturn(refactoringPreviewPromise);
        when(refactoringService.applyRefactoring(anyObject())).thenReturn(refactoringStatusPromise);
        when(refactoringService.getChangePreview(refactoringChanges)).thenReturn(changePreviewPromise);
        when(refactoringService.changeChangeEnabledState(changeEnableState)).thenReturn(changeEnableStatePromise);

        presenter = new PreviewPresenter(view, refactoringUpdater, movePresenterProvider, dtoFactory, refactoringService);
    }

    @Test
    public void constructorShouldBePerformed() throws Exception {
        verify(view).setDelegate(presenter);
    }

    @Test
    public void viewShouldBeShowed() throws Exception {
        presenter.show(SESSION_ID, refactorInfo);

        verify(refactoringSession).setSessionId(SESSION_ID);
        verify(refactoringPreviewPromise).then(refactoringPreviewOperation.capture());
        refactoringPreviewOperation.getValue().apply(refactoringPreview);
        verify(view).setTreeOfChanges(refactoringPreview);
        verify(view).show();
    }

    @Test
    public void acceptButtonActionShouldBePerformed() throws Exception {
        when(refactoringStatus.getSeverity()).thenReturn(OK);

        presenter.onAcceptButtonClicked();

        verify(refactoringStatusPromise).then(refactoringStatusOperation.capture());
        refactoringStatusOperation.getValue().apply(refactoringStatus);
        verify(view).hide();
        verify(refactoringUpdater).refreshProjectTree();
        verify(refactoringUpdater).refreshOpenEditors();
    }

    @Test
    public void acceptButtonActionShouldBeNotPerformedIfStatusIsNotOK() throws Exception {
        when(refactoringStatus.getSeverity()).thenReturn(2);

        presenter.onAcceptButtonClicked();

        verify(refactoringStatusPromise).then(refactoringStatusOperation.capture());
        refactoringStatusOperation.getValue().apply(refactoringStatus);
        verify(view, never()).hide();
        verify(refactoringUpdater, never()).refreshProjectTree();
        verify(refactoringUpdater, never()).refreshOpenEditors();
        verify(view).showErrorMessage(refactoringStatus);
    }

    @Test
    public void showMoveWizardIfOnBackButtonClicked() throws Exception {
        MovePresenter movePresenter = Mockito.mock(MovePresenter.class);
        when(movePresenterProvider.get()).thenReturn(movePresenter);

        presenter.show(SESSION_ID, refactorInfo);
        presenter.onBackButtonClicked();

        verify(view).hide();
        verify(movePresenter).show(refactorInfo);
    }

    @Test
    public void sendRequestToServerIfEnablingStateOfChangeIsChanging() throws Exception {
        when(refactoringPreview.getId()).thenReturn("id");
        when(refactoringPreview.isEnabled()).thenReturn(true);
        when(refactoringSession.getSessionId()).thenReturn(SESSION_ID);

        presenter.show(SESSION_ID, refactorInfo);
        presenter.onEnabledStateChanged(refactoringPreview);

        verify(changeEnableState).setChangeId("id");
        changeEnableState.setSessionId(SESSION_ID);
        changeEnableState.setEnabled(true);

        verify(changeEnableStatePromise).then(changeEnableStateOperation.capture());
        changeEnableStateOperation.getValue().apply(any());
        verify(refactoringChanges).setChangeId("id");
        verify(refactoringChanges).setSessionId(SESSION_ID);
        verify(changePreviewPromise).then(changePreviewOperation.capture());
        changePreviewOperation.getValue().apply(changePreview);
        verify(view).showDiff(changePreview);
    }

    @Test
    public void performIfSelectionChanged() throws Exception {
        when(refactoringPreview.getId()).thenReturn("id");
        when(refactoringPreview.isEnabled()).thenReturn(true);
        when(refactoringSession.getSessionId()).thenReturn(SESSION_ID);

        presenter.show(SESSION_ID, refactorInfo);
        presenter.onSelectionChanged(refactoringPreview);

        verify(changePreviewPromise).then(changePreviewOperation.capture());
        changePreviewOperation.getValue().apply(changePreview);
        verify(view).showDiff(changePreview);
    }
}