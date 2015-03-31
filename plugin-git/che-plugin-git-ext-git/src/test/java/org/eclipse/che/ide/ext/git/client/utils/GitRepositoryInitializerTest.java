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
package org.eclipse.che.ide.ext.git.client.utils;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.ext.git.client.GitRepositoryInitializer;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.websocket.rest.RequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Sergii Leschenko
 */
public class GitRepositoryInitializerTest extends BaseTest {
    public static final boolean BARE = false;

    private GitRepositoryInitializer gitRepositoryInitializer;

    @Mock
    private AsyncCallback         callback;
    @Mock
    private AsyncCallback<String> stringCallback;

    @Override
    public void disarm() {
        super.disarm();

        gitRepositoryInitializer = new GitRepositoryInitializer(service, constant, appContext, notificationManager, dtoUnmarshallerFactory,
                                                                projectServiceClient);
    }

    @Test
    public void testInitRepositoryAndUpdateRootProject() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<Void> callback = (RequestCallback<Void>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).init((ProjectDescriptor)anyObject(), anyBoolean(), (RequestCallback<Void>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<ProjectDescriptor> callback = (AsyncRequestCallback<ProjectDescriptor>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, rootProjectDescriptor);
                return callback;
            }
        }).when(projectServiceClient).getProject(anyString(), (AsyncRequestCallback<ProjectDescriptor>)anyObject());

        gitRepositoryInitializer.initGitRepository(rootProjectDescriptor, callback);

        verify(service).init(eq(rootProjectDescriptor), eq(BARE), (RequestCallback<Void>)anyObject());
        verify(projectServiceClient).getProject(anyString(), (AsyncRequestCallback<ProjectDescriptor>)anyObject());
        verify(currentProject).setRootProject(eq(rootProjectDescriptor));
    }

    @Test
    public void testGetLinkUrlWithAutoInitRepository() throws Exception {
        final HashMap<String, List<String>> attributes = new HashMap<>();
        doReturn(attributes).when(rootProjectDescriptor).getAttributes();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<Void> callback = (RequestCallback<Void>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).init((ProjectDescriptor)anyObject(), anyBoolean(), (RequestCallback<Void>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<ProjectDescriptor> callback = (AsyncRequestCallback<ProjectDescriptor>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                attributes.put("vcs.provider.name", new ArrayList<>(Arrays.asList("git")));
                onSuccess.invoke(callback, rootProjectDescriptor);
                return callback;
            }
        }).when(projectServiceClient).getProject(anyString(), (AsyncRequestCallback<ProjectDescriptor>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, REMOTE_URI);
                return callback;
            }
        }).when(service).getGitReadOnlyUrl((ProjectDescriptor)anyObject(), (AsyncRequestCallback<String>)anyObject());

        gitRepositoryInitializer.getGitUrlWithAutoInit(rootProjectDescriptor, stringCallback);

        verify(service).init(eq(rootProjectDescriptor), eq(BARE), (RequestCallback<Void>)anyObject());
        verify(projectServiceClient).getProject(anyString(), (AsyncRequestCallback<ProjectDescriptor>)anyObject());
        verify(currentProject).setRootProject(eq(rootProjectDescriptor));
        verify(stringCallback).onSuccess(eq(REMOTE_URI));
    }

    @Test
    public void testGetLinkUrlButNotInitRepository() throws Exception {
        final HashMap<String, List<String>> attributes = new HashMap<>();
        attributes.put("vcs.provider.name", new ArrayList<>(Arrays.asList("git")));
        doReturn(attributes).when(rootProjectDescriptor).getAttributes();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, REMOTE_URI);
                return callback;
            }
        }).when(service).getGitReadOnlyUrl((ProjectDescriptor)anyObject(), (AsyncRequestCallback<String>)anyObject());

        gitRepositoryInitializer.getGitUrlWithAutoInit(rootProjectDescriptor, stringCallback);

        verify(service, never()).init((ProjectDescriptor)anyObject(), anyBoolean(), (RequestCallback<Void>)anyObject());
        verify(stringCallback).onSuccess(eq(REMOTE_URI));
    }
}
