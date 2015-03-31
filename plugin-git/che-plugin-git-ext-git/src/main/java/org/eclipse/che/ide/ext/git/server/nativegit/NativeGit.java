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

import org.eclipse.che.api.core.util.LineConsumerFactory;
import org.eclipse.che.ide.ext.git.server.Config;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.AddCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.BranchCheckoutCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.BranchCreateCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.BranchDeleteCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.BranchListCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.BranchRenameCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.CloneCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.CommitCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.DiffCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.EmptyGitCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.FetchCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.InitCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.ListFilesCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.LogCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.LsRemoteCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.MergeCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.MoveCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.PullCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.PushCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.RemoteAddCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.RemoteDeleteCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.RemoteListCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.RemoteUpdateCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.RemoveCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.ResetCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.StatusCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.TagCreateCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.TagDeleteCommand;
import org.eclipse.che.ide.ext.git.server.nativegit.commands.TagListCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Git commands factory.
 *
 * @author Eugene Voevodin
 */
public class NativeGit {

    private static final Logger LOG                 = LoggerFactory.getLogger(NativeGit.class);
    private static final String SSH_SCRIPT_TEMPLATE = "META-INF/SshTemplate";
    private static final String SSH_SCRIPT          = "ssh_script";
    private static String sshScriptTemplate;
    private        File   repository;
    protected LineConsumerFactory gitOutputPublisherFactory;

    /**
     * Loading template, that will be used to store ssh
     */
    static {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Thread.currentThread().getContextClassLoader()
                                            .getResourceAsStream(SSH_SCRIPT_TEMPLATE)))) {
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (buf.length() > 0) {
                    buf.append('\n');
                }
                buf.append(line);
            }
            sshScriptTemplate = buf.toString();
        } catch (Exception e) {
            LOG.error("Cant load template " + SSH_SCRIPT_TEMPLATE);
            throw new RuntimeException("Cant load credentials template.", e);
        }
    }

    /**
     * @param repository
     *         directory where will be executed all commands created with
     *         this NativeGit object
     */
    public NativeGit(File repository) {
        this.repository = repository;
    }

    /** @return empty git command */
    public EmptyGitCommand createEmptyGitCommand() {
        return new EmptyGitCommand(repository);
    }

    /**
     * Creates clone command that will be used without ssh key
     *
     * @return clone command
     */
    public CloneCommand createCloneCommand() {
        CloneCommand cloneCommand = new CloneCommand(repository);
        cloneCommand.setLineConsumerFactory(gitOutputPublisherFactory);
        return cloneCommand;
    }

    /**
     * Creates CloneCommand that will be used with ssh key
     *
     * @param sshKeyPath
     *         path to ssh key that will be used with clone command
     * @return git command with ssh key parameter
     * @throws GitException
     *         when some error with script storing occurs
     */
    public CloneCommand createCloneCommand(String sshKeyPath) throws GitException {
        storeSshScript(sshKeyPath);
        CloneCommand command = new CloneCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        command.setSSHScriptPath(SshKeysManager.getKeyDirectoryPath() + '/' + SSH_SCRIPT);
        return command;
    }

    /** @return commit command */
    public CommitCommand createCommitCommand() {
        CommitCommand command = new CommitCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return branch create command */
    public BranchRenameCommand createBranchRenameCommand() {
        BranchRenameCommand command = new BranchRenameCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return remote add command */
    public RemoteAddCommand createRemoteAddCommand() {
        RemoteAddCommand command = new RemoteAddCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return remote list command */
    public RemoteListCommand createRemoteListCommand() {
        RemoteListCommand command = new RemoteListCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return remote delete command */
    public RemoteDeleteCommand createRemoteDeleteCommand() {
        RemoteDeleteCommand command = new RemoteDeleteCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return log command */
    public LogCommand createLogCommand() {
        LogCommand command = new LogCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return ls command */
    public LsRemoteCommand createLsRemoteCommand() {
        LsRemoteCommand command = new LsRemoteCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return add command */
    public AddCommand createAddCommand() {
        AddCommand command = new AddCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return init command */
    public InitCommand createInitCommand() {
        InitCommand command = new InitCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return diff command */
    public DiffCommand createDiffCommand() {
        DiffCommand command = new DiffCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return reset command */
    public ResetCommand createResetCommand() {
        ResetCommand command = new ResetCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return tag create command */
    public TagCreateCommand createTagCreateCommand() {
        TagCreateCommand command = new TagCreateCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return tag delete command */
    public TagDeleteCommand createTagDeleteCommand() {
        TagDeleteCommand command = new TagDeleteCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return tah list command */
    public TagListCommand createTagListCommand() {
        TagListCommand command = new TagListCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return branch create command */
    public BranchCreateCommand createBranchCreateCommand() {
        BranchCreateCommand command = new BranchCreateCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return config */
    public Config createConfig() throws GitException {
        return new ConfigImpl(repository);
    }

    /** @return branch checkout command */
    public BranchCheckoutCommand createBranchCheckoutCommand() {
        BranchCheckoutCommand command = new BranchCheckoutCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return list files command */
    public ListFilesCommand createListFilesCommand() {
        ListFilesCommand command = new ListFilesCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return branch list command */
    public BranchListCommand createBranchListCommand() {
        BranchListCommand command = new BranchListCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return branch delete command */
    public BranchDeleteCommand createBranchDeleteCommand() {
        BranchDeleteCommand command = new BranchDeleteCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /**
     * Creates branch delete command that will be used with ssh key for delete origin branch
     *
     * @param sshKeyPath
     *         path to ssh key that will be used with branch delete command
     * @return branch delete command with ssh key parameter
     * @throws GitException
     *         when some error with script storing occurs
     */
    public BranchDeleteCommand createBranchDeleteCommand(String sshKeyPath) throws GitException {
        storeSshScript(sshKeyPath);
        BranchDeleteCommand command = new BranchDeleteCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        command.setSSHScriptPath(SshKeysManager.getKeyDirectoryPath() + '/' + SSH_SCRIPT);
        return command;
    }

    /** @return remote command */
    public RemoveCommand createRemoveCommand() {
        RemoveCommand command = new RemoveCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return move command */
    public MoveCommand createMoveCommand() {
        MoveCommand command = new MoveCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return status command */
    public StatusCommand createStatusCommand() {
        StatusCommand command = new StatusCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /** @return merge command */
    public MergeCommand createMergeCommand() {
        MergeCommand command = new MergeCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /**
     * Creates fetch command that will be used without ssh key
     *
     * @return fetch command
     */
    public FetchCommand createFetchCommand() {
        FetchCommand command = new FetchCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /**
     * Creates fetch command that will be used with ssh key
     *
     * @param sshKeyPath
     *         path to ssh key that will be used with fetch command
     * @return fetch command with ssh key parameter
     * @throws GitException
     *         when some error with script storing occurs
     */
    public FetchCommand createFetchCommand(String sshKeyPath) throws GitException {
        storeSshScript(sshKeyPath);
        FetchCommand command = new FetchCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        command.setSSHScriptPath(SshKeysManager.getKeyDirectoryPath() + '/' + SSH_SCRIPT);
        return command;
    }

    /**
     * Creates pull command that will be used without ssh key
     *
     * @return pull command
     */
    public PullCommand createPullCommand() {
        PullCommand command = new PullCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /**
     * Creates pull command that will be used with ssh key
     *
     * @param sshKeyPath
     *         path to ssh key that will be used with pull command
     * @return pull command with ssh key
     * @throws GitException
     *         when some error with script storing occurs
     */
    public PullCommand createPullCommand(String sshKeyPath) throws GitException {
        storeSshScript(sshKeyPath);
        PullCommand command = new PullCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        command.setSSHScriptPath(SshKeysManager.getKeyDirectoryPath() + '/' + SSH_SCRIPT);
        return command;
    }

    /** @return remote update command */
    public RemoteUpdateCommand createRemoteUpdateCommand() {
        RemoteUpdateCommand command = new RemoteUpdateCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /**
     * Creates push command that will be used without ssh key
     *
     * @return push command
     */
    public PushCommand createPushCommand() {
        PushCommand command = new PushCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        return command;
    }

    /**
     * Creates push command that will be used with ssh key
     *
     * @param sshKeyPath
     *         path to ssh key that will be used with push command
     * @return pull command with ssh key parameter
     * @throws GitException
     *         when some error with script storing occurs
     */
    public PushCommand createPushCommand(String sshKeyPath) throws GitException {
        storeSshScript(sshKeyPath);
        PushCommand command = new PushCommand(repository);
        command.setLineConsumerFactory(gitOutputPublisherFactory);
        command.setSSHScriptPath(SshKeysManager.getKeyDirectoryPath() + '/' + SSH_SCRIPT);
        return command;
    }

    /** @return repository */
    public File getRepository() {
        return repository;
    }

    /**
     * @param repository
     *         repository
     */
    public void setRepository(File repository) {
        this.repository = repository;
    }


    /**
     * Stores ssh script that will be executed with all commands that need ssh.
     *
     * @param pathToSSHKey
     *         path to ssh key
     * @throws GitException
     *         when any error with ssh script storing occurs
     */
    private void storeSshScript(String pathToSSHKey) throws GitException {
        File sshScript = new File(SshKeysManager.getKeyDirectoryPath(), SSH_SCRIPT);
        //creating script
        try (FileOutputStream fos = new FileOutputStream(sshScript)) {
            fos.write(sshScriptTemplate.replace("$ssh_key", pathToSSHKey).getBytes());
        } catch (IOException e) {
            LOG.error("It is not possible to store " + pathToSSHKey + " ssh key");
            throw new GitException("Can't store SSH key");
        }
        if (!sshScript.setExecutable(true)) {
            LOG.error("Can't make " + sshScript + " executable");
            throw new GitException("Can't set permissions to SSH key");
        }
    }

    public void setOutputLineConsumerFactory(LineConsumerFactory gitOutputPublisherFactory) {
        this.gitOutputPublisherFactory = gitOutputPublisherFactory;
    }
}