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
package org.eclipse.che.ide.ext.svn.server.upstream;

import org.eclipse.che.api.core.util.CancellableProcessWrapper;
import org.eclipse.che.api.core.util.CommandLine;
import org.eclipse.che.api.core.util.ProcessUtil;
import org.eclipse.che.api.core.util.Watchdog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Utilities class containing logic copied/pasted from Git extension that could/should be put into a core VCS API.
 */
public class UpstreamUtils {

    private static final Logger LOG = LoggerFactory.getLogger(UpstreamUtils.class);

    /**
     * Private constructor.
     */
    private UpstreamUtils() { }

    /**
     * Executes a command line executable based on the arguments specified.
     *
     * @param env the optional environment variables
     * @param cmd the command to run
     * @param args the optional command arguments
     * @param timeout the optional timeout in milliseconds
     * @param workingDirectory the optional working directory
     *
     * @return the command line result
     *
     * @throws IOException if something goes wrong
     */
    public static CommandLineResult executeCommandLine(@Nullable final Map<String, String> env,
                                                       final String cmd,
                                                       @Nullable final String[] args,
                                                       final long timeout,
                                                       @Nullable final File workingDirectory) throws IOException {
        return executeCommandLine(env, cmd, args, null, timeout, workingDirectory);
    }

    /**
     * Executes a command line executable based on the arguments specified.
     *
     * @param env the optional environment variables
     * @param cmd the command to run
     * @param args the optional command arguments
     * @param redactedArgs additional command arguments that will not be shown in result
     * @param timeout the optional timeout in milliseconds
     * @param workingDirectory the optional working directory
     * 
     * @return the command line result
     * 
     * @throws IOException if something goes wrong
     */
    public static CommandLineResult executeCommandLine(@Nullable final Map<String, String> env,
                                                       final String cmd,
                                                       @Nullable final String[] args,
                                                       @Nullable final String[] redactedArgs,
                                                       final long timeout,
                                                       @Nullable final File workingDirectory)
            throws IOException {
        CommandLine command = new CommandLine(cmd);

        if (args != null) {
            for (String arg: args) {
                command.add(arg);
            }
        }

        CommandLine redactedCommand = new CommandLine(command);
        if (redactedArgs != null) {
            for (String arg: redactedArgs) {
                redactedCommand.add(arg);
            }
        }

        LOG.debug("Running command: " + command.toString());
        final ProcessBuilder processBuilder = new ProcessBuilder(redactedCommand.toShellCommand());

        Map<String, String> environment = processBuilder.environment();
        if (env != null) {
            environment.putAll(env);
        }
        environment.put("LANG", "en_US.UTF-8");
        environment.put("GDM_LANG", "en_US.UTF-8");
        environment.put("LANGUAGE", "us");

        processBuilder.directory(workingDirectory);

        final Process process = processBuilder.start();

        final Watchdog watcher;

        if (timeout > 0) {
            watcher = new Watchdog(timeout, TimeUnit.MILLISECONDS);

            watcher.start(new CancellableProcessWrapper(process));
        }

        final CommandLineOutputProcessor stdoutConsumer = new CommandLineOutputProcessor(new ArrayList<String>());
        final CommandLineOutputProcessor stderrConsumer = new CommandLineOutputProcessor(new ArrayList<String>());

        ProcessUtil.process(process, stdoutConsumer, stderrConsumer);

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        return new CommandLineResult(command, process.exitValue(), stdoutConsumer.getOutput(),
                                     stderrConsumer.getOutput());
    }
}
