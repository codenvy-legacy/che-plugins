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
package org.eclipse.che.ide.ext.git.server.nativegit;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.ext.git.server.GitException;
import com.google.common.collect.ImmutableList;

import org.eclipse.che.ide.ext.git.server.nativegit.commands.BranchDeleteCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.BranchListCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.CloneCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.CommitCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.EmptyGitCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.FetchCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.LogCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.PullCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.PushCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.RemoteListCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.RemoteUpdateCommand;
import org.eclipse.che.ide.ext.git.shared.BranchDeleteRequest;
import org.eclipse.che.ide.ext.git.shared.CloneRequest;
import org.eclipse.che.ide.ext.git.shared.CommitRequest;
import org.eclipse.che.ide.ext.git.shared.FetchRequest;
import org.eclipse.che.ide.ext.git.shared.GitUser;
import org.eclipse.che.ide.ext.git.shared.PullRequest;
import org.eclipse.che.ide.ext.git.shared.PushRequest;
import org.eclipse.che.ide.ext.git.shared.Remote;
import org.eclipse.che.ide.ext.git.shared.Revision;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

@Listeners(MockitoTestNGListener.class)
public class NativeGitConnectionTest {

    @Mock
    GitUser             user;
    @Mock
    GitUser             wso2User;
    @Mock
    SshKeysManager      keysManager;
    @Mock
    CredentialsLoader   credentialsLoader;
    @Mock
    NativeGit           nativeGit;
    @Mock
    EmptyGitCommand     emptyGitCommand;
    @Mock
    CommitCommand       commitCommand;
    @Mock
    BranchDeleteCommand branchDeleteCommand;
    @Mock
    ConfigImpl          config;
    @Mock
    LogCommand          logCommand;
    @Mock
    Revision            revision;
    @Mock
    BranchListCommand   branchListCommand;
    @Mock
    RemoteListCommand   remoteListCommand;
    @Mock
    Remote              remote;

    NativeGitConnection connection;

    @BeforeMethod
    public void setUp() throws Exception {

        connection = new NativeGitConnection(nativeGit, user, keysManager, credentialsLoader, new GitAskPassScript());

        when(nativeGit.createEmptyGitCommand()).thenReturn(emptyGitCommand);
        when(nativeGit.createCommitCommand()).thenReturn(commitCommand);
        when(nativeGit.createBranchDeleteCommand()).thenReturn(branchDeleteCommand);
        when(nativeGit.createLogCommand()).thenReturn(logCommand);
        when(logCommand.execute()).thenReturn(ImmutableList.of(revision));
        when(nativeGit.createConfig()).thenReturn(config);
        when(nativeGit.createBranchListCommand()).thenReturn(branchListCommand);
        when(branchListCommand.getLines()).thenReturn(ImmutableList.of("* master"));
        when(nativeGit.createRemoteListCommand()).thenReturn(remoteListCommand);
        when(remoteListCommand.execute()).thenReturn(ImmutableList.of(remote));

    }

    @Test
    public void shouldCommit() throws Exception {
        //given
        CommitRequest commitRequest =
                DtoFactory.getInstance().createDto(CommitRequest.class).withMessage("Message 1").withAmend(false).withAll(true);
        when(config.get(eq("codenvy.credentialsProvider"))).thenThrow(GitException.class); //emulate no property configured
        //when
        Revision actualRevision = connection.commit(commitRequest);
        //then
        ArgumentCaptor<GitUser> captor = ArgumentCaptor.forClass(GitUser.class);
        verify(commitCommand).setCommitter(captor.capture());
        verify(commitCommand).execute();
        verify(commitCommand).setAll(eq(true));
        verify(commitCommand).setCommitter(user);
        verify(commitCommand).setAmend(eq(false));
        verify(commitCommand).setMessage(eq("Message 1"));
        assertEquals(captor.getValue(), user);
        assertEquals(actualRevision, revision);
        verify(actualRevision).setBranch(eq("master"));
    }

    @Test
    public void shouldOverrideAuthorIfUserSetInConfig() throws GitException {
        //given
        CommitRequest commitRequest =
                DtoFactory.getInstance().createDto(CommitRequest.class).withMessage("Message 1").withAmend(false).withAll(true);
        when(config.get(eq("codenvy.credentialsProvider"))).thenThrow(GitException.class); //emulate no property configured
        when(config.get(eq("user.name"))).thenReturn("Inders Gogh Rasmusen");
        //when
        connection.commit(commitRequest);
        //then
        verify(commitCommand).setAuthor(user);
    }

    @Test
    public void shouldNotOverrideAuthorIfUserNotSetInConfig() throws GitException {
        //given
        CommitRequest commitRequest =
                DtoFactory.getInstance().createDto(CommitRequest.class).withMessage("Message 1").withAmend(false).withAll(true);
        when(config.get(eq("codenvy.credentialsProvider"))).thenThrow(GitException.class); //emulate no property configured
        when(config.get(eq("user.name"))).thenThrow(GitException.class);
        //when
        connection.commit(commitRequest);
        //then
        verify(commitCommand, never()).setAuthor(any(GitUser.class));
    }

    @Test
    public void shouldSetCommitterFromCredentialLoader() throws GitException {
        //given
        CommitRequest commitRequest =
                DtoFactory.getInstance().createDto(CommitRequest.class).withMessage("Message 1").withAmend(false).withAll(true);
        when(config.get(eq("codenvy.credentialsProvider"))).thenReturn("wso2");
        when(credentialsLoader.getUser(eq("wso2"))).thenReturn(wso2User);

        //when
        connection.commit(commitRequest);
        //then
        verify(commitCommand).setCommitter(eq(wso2User));
        verify(commitCommand).setAuthor(eq(wso2User));

    }

    @Test
    public void shouldDeleteLocalBranch()
            throws GitException, UnauthorizedException, InterruptedException, IOException {
        BranchDeleteRequest branchDeleteRequest = DtoFactory.getInstance().createDto(BranchDeleteRequest.class)
                                                            .withName("refs/heads/localBranch").withForce(true);
        when(emptyGitCommand.setNextParameter(anyString())).thenReturn(emptyGitCommand);
        when(emptyGitCommand.getText()).thenReturn("f5d9ef24292f7e432b2b13762e112c380323f869 refs/heads/localBranch");
        when(nativeGit.createRemoteListCommand()).thenReturn(remoteListCommand);

        //delete branch
        connection.branchDelete(branchDeleteRequest);

        verify(branchDeleteCommand).setBranchName(eq("localBranch"));
        verify(branchDeleteCommand).setDeleteFullyMerged(eq(true));
        verify(branchDeleteCommand).execute();
    }

    @Test
    public void shouldDeleteRemoteBranch()
            throws GitException, UnauthorizedException, InterruptedException, IOException {
        final String REMOTE_URI = "git@github.com:gitaccount/repository.git";
        File keyfile = mock(File.class);
        BranchDeleteRequest branchDeleteRequest = DtoFactory.getInstance().createDto(BranchDeleteRequest.class)
                                                            .withName("refs/remotes/origin/remoteBranch").withForce(true);

        when(emptyGitCommand.setNextParameter(anyString())).thenReturn(emptyGitCommand);
        when(emptyGitCommand.getText())
                .thenReturn("f5d9ef24292f7e432b2b13762e112c380323f869 refs/remotes/origin/remoteBranch");
        when(remoteListCommand.setRemoteName("origin")).thenReturn(remoteListCommand);
        when(remoteListCommand.execute().get(0).getUrl())
                .thenReturn(REMOTE_URI);
        when(keysManager.writeKeyFile(REMOTE_URI)).thenReturn(keyfile);
        when(keyfile.getAbsolutePath()).thenReturn("keyfile");
        when(nativeGit.createBranchDeleteCommand(eq("keyfile"))).thenReturn(branchDeleteCommand);

        //delete branch
        connection.branchDelete(branchDeleteRequest);

        verify(branchDeleteCommand).setBranchName(eq("remoteBranch"));
        verify(branchDeleteCommand).setRemote(eq("origin"));
        verify(branchDeleteCommand).setDeleteFullyMerged(eq(true));
        verify(branchDeleteCommand).execute();
    }

    @Test
    public void shouldInvokeSshManagerRemoveKeyMethodAfterClone() throws Exception {
        when(keysManager.writeKeyFile("git@host.com:codenvy")).thenReturn(mock(File.class));
        //prepare clone command
        final CloneCommand cloneCommand = mock(CloneCommand.class);
        when(nativeGit.createCloneCommand(anyString())).thenReturn(cloneCommand);
        //prepare remote update command for clone
        when(nativeGit.createRemoteUpdateCommand()).thenReturn(mock(RemoteUpdateCommand.class, RETURNS_DEEP_STUBS));

        connection.clone(DtoFactory.getInstance()
                                   .createDto(CloneRequest.class)
                                   .withRemoteUri("git@host.com:codenvy"));

        verify(keysManager).removeKey("git@host.com:codenvy");
    }

    @Test
    public void shouldInvokeSshManagerRemoveKeyMethodAfterPush() throws Exception {
        when(keysManager.writeKeyFile("git@host.com:codenvy")).thenReturn(mock(File.class));

        //force connection to use remote from push request
        final RemoteListCommand rlc = mock(RemoteListCommand.class, RETURNS_DEEP_STUBS);
        when(rlc.setRemoteName(anyString())).thenReturn(rlc);
        when(rlc.execute()).thenThrow(new GitException("test"));
        when(nativeGit.createRemoteListCommand()).thenReturn(rlc);

        //prepare push command
        final PushCommand pushCommand = mock(PushCommand.class, RETURNS_DEEP_STUBS);
        when(pushCommand.getText()).thenReturn("");
        when(nativeGit.createPushCommand(anyString())).thenReturn(pushCommand);

        connection.push(DtoFactory.getInstance()
                                  .createDto(PushRequest.class)
                                  .withRemote("git@host.com:codenvy"));

        verify(keysManager).removeKey("git@host.com:codenvy");
    }

    @Test
    public void shouldInvokeSshManagerRemoveKeyMethodAfterPull() throws Exception {
        when(keysManager.writeKeyFile("git@host.com:codenvy")).thenReturn(mock(File.class));
        when(keysManager.writeKeyFile("git@host.com:codenvy")).thenReturn(mock(File.class));

        //force connection to use remote from pull request
        final RemoteListCommand rlc = mock(RemoteListCommand.class);
        when(rlc.setRemoteName(anyString())).thenReturn(rlc);
        when(rlc.execute()).thenThrow(new GitException("test"));
        when(nativeGit.createRemoteListCommand()).thenReturn(rlc);

        //prepare pull command
        final PullCommand pullCommand = mock(PullCommand.class, RETURNS_DEEP_STUBS);
        when(pullCommand.getText()).thenReturn("");
        when(nativeGit.createPullCommand(anyString())).thenReturn(pullCommand);

        connection.pull(DtoFactory.getInstance()
                                  .createDto(PullRequest.class)
                                  .withRemote("git@host.com:codenvy"));

        verify(keysManager).removeKey("git@host.com:codenvy");
    }

    @Test
    public void shouldInvokeSshManagerRemoveKeyMethodAfterFetch() throws Exception {
        when(keysManager.writeKeyFile("git@host.com:codenvy")).thenReturn(mock(File.class));

        //force connection to use remote from fetch request
        final RemoteListCommand rlc = mock(RemoteListCommand.class);
        when(rlc.setRemoteName(anyString())).thenReturn(rlc);
        when(rlc.execute()).thenThrow(new GitException("test"));
        when(nativeGit.createRemoteListCommand()).thenReturn(rlc);

        //prepare fetch command
        final FetchCommand fetchCommand = mock(FetchCommand.class, RETURNS_DEEP_STUBS);
        when(fetchCommand.getText()).thenReturn("");
        when(nativeGit.createFetchCommand(anyString())).thenReturn(fetchCommand);

        connection.fetch(DtoFactory.getInstance()
                                   .createDto(FetchRequest.class)
                                   .withRemote("git@host.com:codenvy"));

        verify(keysManager).removeKey("git@host.com:codenvy");
    }

    @Test
    public void shouldInvokeSshManagerRemoveKeyMethodAfterDeleteBranch() throws Exception {
        when(keysManager.writeKeyFile("git@host.com:codenvy")).thenReturn(mock(File.class));

        //preparing branch
        when(emptyGitCommand.setNextParameter(anyString())).thenReturn(emptyGitCommand);
        when(emptyGitCommand.getText()).thenReturn("f5d9ef24292f7e432b2b13762e112c380323f869 refs/remotes/branch/");

        //prepare remote uri
        final RemoteListCommand rlc = mock(RemoteListCommand.class);
        when(rlc.setRemoteName(anyString())).thenReturn(rlc);
        when(nativeGit.createRemoteListCommand()).thenReturn(rlc);

        final Remote remote = mock(Remote.class);
        when(rlc.execute()).thenReturn(asList(remote));
        when(remote.getUrl()).thenReturn("git@host.com:codenvy");

        //preparing branch delete command
        final BranchDeleteCommand bdc = mock(BranchDeleteCommand.class);
        when(nativeGit.createBranchDeleteCommand(anyString())).thenReturn(bdc);

        connection.branchDelete(DtoFactory.getInstance().createDto(BranchDeleteRequest.class));

        verify(keysManager).removeKey("git@host.com:codenvy");
    }
}