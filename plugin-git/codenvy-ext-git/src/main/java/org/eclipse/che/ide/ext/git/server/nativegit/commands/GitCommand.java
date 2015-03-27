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
package org.eclipse.che.ide.ext.git.server.nativegit.commands;

import org.eclipse.che.api.core.util.ListLineConsumer;
import org.eclipse.che.api.core.util.CommandLine;
import org.eclipse.che.api.core.util.LineConsumerFactory;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.server.nativegit.CommandProcess;

import java.io.File;
import java.util.Collections;
import java.util.Map;

/**
 * Base class for all git commands
 *
 * @author Eugene Voevodin
 */
public abstract class GitCommand<T> extends ListLineConsumer {

    private final File repository;

    private int                 timeout;
    private String              SSHScriptPath;
    private String              askPassScriptPath;
    private LineConsumerFactory lineConsumerFactory;
    private Map<String, String> commandEnvironment;

    protected CommandLine commandLine;

    /**
     * @param repository
     *         directory where command will be executed
     */
    public GitCommand(File repository) {
        this.repository = repository;
        this.commandEnvironment = Collections.emptyMap();
        commandLine = new CommandLine("git");
        timeout = -1;
    }

    /**
     * @return git command result
     * @throws org.eclipse.che.ide.ext.git.server.GitException
     *         when command execution failed or command execution exit value is not 0
     */
    public abstract T execute() throws GitException;

    public File getRepository() {
        return repository;
    }

    /**
     * If command needs ssh, then it needs path to ssh script,
     * that use stored key.
     *
     * @param SSHScriptPath
     *         path to ssh script
     */
    public void setSSHScriptPath(String SSHScriptPath) {
        this.SSHScriptPath = SSHScriptPath;
    }

    /** @return current command line */
    public CommandLine getCommandLine() {
        return new CommandLine(commandLine);
    }

    /** @return path to ssh script */
    public String getSSHScriptPath() {
        return SSHScriptPath;
    }

    public void setAskPassScriptPath(String askPassScriptPath) {
        this.askPassScriptPath = askPassScriptPath;
    }

    public String getAskPassScriptPath() {
        return askPassScriptPath;
    }

    /**
     * @param timeout
     *         command execution timeout in seconds
     * @return GitCommand with timeout
     */
    public GitCommand setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /** @return command execution timeout in seconds */
    public int getTimeout() {
        return timeout;
    }

    /** Command line initialization. */
    protected GitCommand reset() {
        commandLine.clear().add("git");
        clear();
        return this;
    }

    /**
     * @return command additional environment variables;
     */
    public Map<String, String> getCommandEnvironment() {
        return commandEnvironment != null ? commandEnvironment : Collections.<String, String>emptyMap();
    }

    /** command additional environment variables */

    public void setCommandEnvironment(Map<String, String> commandEnvironment) {
        this.commandEnvironment = commandEnvironment;
    }

    /**
     * Executes git command.
     *
     * @throws GitException
     *         when command execution failed or command execution exit value is not 0
     */
    protected void start() throws GitException {
        CommandProcess.executeGitCommand(this, lineConsumerFactory);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String command : commandLine.asArray()) {
            builder.append(command).append(" ");
        }
        return builder.toString();
    }

    /**
     * Set a process line consumer to be used to capture process output
     *
     * @param lineConsumerFactory
     *         factory that provides consumer for command output
     */
    public GitCommand setLineConsumerFactory(LineConsumerFactory lineConsumerFactory) {
        this.lineConsumerFactory = lineConsumerFactory;
        return this;
    }
}
