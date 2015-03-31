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
package org.eclipse.che.ide.ext.git.client.branch;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.ext.git.client.GitOutputPartPresenter;
import org.eclipse.che.ide.ext.git.shared.Branch;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.Event;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.eclipse.che.ide.ext.git.client.patcher.WindowPatcher.RETURNED_MESSAGE;
import static org.eclipse.che.ide.ext.git.shared.BranchListRequest.LIST_ALL;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link BranchPresenter} functionality.
 *
 * @author Andrey Plotnikov
 */
public class BranchPresenterTest extends BaseTest {
    public static final String  BRANCH_NAME   = "branchName";
    public static final boolean NEED_DELETING = true;
    public static final boolean IS_REMOTE     = true;
    public static final boolean IS_ACTIVE     = true;
    @Mock
    private BranchView             view;
    @Mock
    private FileNode               file;
    @Mock
    private EditorInput            editorInput;
    @Mock
    private EditorAgent            editorAgent;
    @Mock
    private Branch                 selectedBranch;
    @Mock
    private EditorPartPresenter    partPresenter;
    @Mock
    private GitOutputPartPresenter gitConsole;
    @Mock
    private WorkspaceAgent         workspaceAgent;
    @Mock
    private DialogFactory          dialogFactory;
    @Mock
    private DtoFactory             dtoFactory;
    private BranchPresenter        presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new BranchPresenter(view, eventBus, dtoFactory, editorAgent, service, constant, appContext, notificationManager,
                                        dtoUnmarshallerFactory, gitConsole, workspaceAgent, dialogFactory);

        StringMap<EditorPartPresenter> partPresenterMap = Collections.createStringMap();
        partPresenterMap.put("partPresenter", partPresenter);

        when(selectedBranch.getDisplayName()).thenReturn(BRANCH_NAME);
        when(selectedBranch.getName()).thenReturn(BRANCH_NAME);
        when(selectedBranch.isRemote()).thenReturn(IS_REMOTE);
        when(selectedBranch.isActive()).thenReturn(IS_ACTIVE);
        when(editorAgent.getOpenedEditors()).thenReturn(partPresenterMap);
        when(partPresenter.getEditorInput()).thenReturn(editorInput);
        when(editorInput.getFile()).thenReturn(file);
    }

    @Ignore
    public void testShowDialogWhenGetBranchesRequestIsSuccessful() throws Exception {
        final Array<Branch> branches = Collections.createArray();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Branch>> callback = (AsyncRequestCallback<Array<Branch>>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, branches);
                return callback;
            }
        }).when(service).branchList((ProjectDescriptor)anyObject(), anyString(), (AsyncRequestCallback<Array<Branch>>)anyObject());

        presenter.showDialog();

        verify(appContext).getCurrentProject();
        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(DISABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
        verify(view).setBranches(eq(branches));
        verify(service).branchList(eq(projectDescriptor), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(notificationManager, never()).showError(anyString());
        verify(constant, never()).branchesListFailed();
    }

    @Test
    public void testShowDialogWhenGetBranchesRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Branch>> callback = (AsyncRequestCallback<Array<Branch>>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchList((ProjectDescriptor)anyObject(), anyString(), (AsyncRequestCallback<Array<Branch>>)anyObject());

        presenter.showDialog();

        verify(appContext).getCurrentProject();
        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(DISABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
        verify(service).branchList(eq(rootProjectDescriptor), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(notificationManager).showError(anyString());
        verify(constant).branchesListFailed();
    }

    @Test
    public void testOnCloseClicked() throws Exception {
        presenter.onCloseClicked();

        verify(view).close();
    }

    @Test
    @Ignore
    public void testOnRenameClickedWhenBranchRenameRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_PATH);
                return callback;
            }
        }).when(service).branchRename((ProjectDescriptor)anyObject(), anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onRenameClicked();

        verify(selectedBranch).getDisplayName();
        verify(service).branchRename(eq(rootProjectDescriptor), eq(BRANCH_NAME), eq(RETURNED_MESSAGE),
                                     (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2))
                .branchList(eq(rootProjectDescriptor), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(notificationManager, never()).showError(anyString());
        verify(constant, never()).branchRenameFailed();
    }

    /** Select mock branch for testing. */
    private void selectBranch() {
        presenter.showDialog();
        presenter.onBranchSelected(selectedBranch);
    }

    @Test
    @Ignore
    public void testOnRenameClickedWhenBranchRenameRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchRename((ProjectDescriptor)anyObject(), anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onRenameClicked();

        verify(selectedBranch).getDisplayName();
        verify(service).branchRename(eq(rootProjectDescriptor), eq(BRANCH_NAME), eq(RETURNED_MESSAGE),
                                     (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showError(anyString());
        verify(constant).branchRenameFailed();
    }

    @Test
    public void testOnDeleteClickedWhenBranchDeleteRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_PATH);
                return callback;
            }
        }).when(service).branchDelete((ProjectDescriptor)anyObject(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onDeleteClicked();

        verify(selectedBranch).getName();
        verify(service)
                .branchDelete(eq(rootProjectDescriptor), eq(BRANCH_NAME), eq(NEED_DELETING), (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2))
                .branchList(eq(rootProjectDescriptor), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(constant, never()).branchDeleteFailed();
        verify(notificationManager, never()).showError(anyString());
    }

    @Test
    public void testOnDeleteClickedWhenBranchDeleteRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchDelete((ProjectDescriptor)anyObject(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onDeleteClicked();

        verify(selectedBranch).getName();
        verify(service).branchDelete(eq(rootProjectDescriptor), anyString(), eq(NEED_DELETING), (AsyncRequestCallback<String>)anyObject());
        verify(constant).branchDeleteFailed();
        verify(notificationManager).showError(anyString());
    }

    @Test
    public void testOnCheckoutClickedWhenBranchCheckoutRequestAndRefreshProjectIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_PATH);
                return callback;
            }
        }).when(service).branchCheckout((ProjectDescriptor)anyObject(), anyString(), anyString(), anyBoolean(),
                                        (AsyncRequestCallback<String>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, "content");
                return callback;
            }
        }).when(projectServiceClient).getFileContent(anyString(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onCheckoutClicked();

        verify(editorAgent).getOpenedEditors();
        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).isRemote();
        verify(service).branchCheckout(eq(rootProjectDescriptor), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2)).branchList(eq(rootProjectDescriptor), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(appContext).getCurrentProject();
        verify(partPresenter).getEditorInput();
        verify(notificationManager, never()).showError(anyString());
        verify(constant, never()).branchCheckoutFailed();
    }

    @Test
    public void testOnCheckoutClickedWhenBranchCheckoutRequestAndRefreshProjectIsSuccessfulButOpenFileIsNotExistInBranch()
            throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_PATH);
                return callback;
            }
        }).when(service).branchCheckout((ProjectDescriptor)anyObject(), anyString(), anyString(), anyBoolean(),
                                        (AsyncRequestCallback<String>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(projectServiceClient).getFileContent(anyString(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onCheckoutClicked();

        verify(editorAgent).getOpenedEditors();
        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).isRemote();
        verify(service).branchCheckout(eq(rootProjectDescriptor), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2)).branchList(eq(rootProjectDescriptor), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(appContext).getCurrentProject();
        verify(partPresenter).getEditorInput();
        verify(eventBus, times(2)).fireEvent(Matchers.<Event<GwtEvent>>anyObject());
    }

    @Test
    public void testOnCheckoutClickedWhenBranchCheckoutRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchCheckout((ProjectDescriptor)anyObject(), anyString(), anyString(), anyBoolean(),
                                        (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onCheckoutClicked();

        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).isRemote();
        verify(service).branchCheckout(eq(rootProjectDescriptor), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
    }

    @Test
    @Ignore
    public void testOnCreateClickedWhenBranchCreateRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Branch> callback = (AsyncRequestCallback<Branch>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, selectedBranch);
                return callback;
            }
        }).when(service).branchCreate((ProjectDescriptor)anyObject(), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());

        presenter.showDialog();
        presenter.onCreateClicked();

        verify(constant).branchTypeNew();
        verify(service).branchCreate(eq(rootProjectDescriptor), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());
        verify(service, times(2)).branchList(eq(rootProjectDescriptor), eq(LIST_ALL),
                                             (AsyncRequestCallback<Array<Branch>>)anyObject());
    }

    @Test
    @Ignore
    public void testOnCreateClickedWhenBranchCreateRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Branch> callback = (AsyncRequestCallback<Branch>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchCreate((ProjectDescriptor)anyObject(), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());

        presenter.showDialog();
        presenter.onCreateClicked();

        verify(service).branchCreate(eq(rootProjectDescriptor), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());
        verify(constant).branchCreateFailed();
        verify(notificationManager).showError(anyString());
    }

    @Test
    public void testOnBranchSelected() throws Exception {
        presenter.onBranchSelected(selectedBranch);

        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(ENABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(ENABLE_BUTTON));
    }
}