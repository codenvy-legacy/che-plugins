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
package org.eclipse.che.ide.extension.maven.client.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
public class CommandLine {
    private final List<String> arguments;

    public CommandLine() {
        arguments = new ArrayList<>();
    }

    public CommandLine(String... args) {
        arguments = new ArrayList<>();
        if (args != null && args.length > 0) {
            Collections.addAll(arguments, args);
        }
    }

    public CommandLine(String commandLine) {
        final String[] args = commandLine.split("\\s+");
        arguments = new ArrayList<>();
        if (args.length > 0) {
            Collections.addAll(arguments, args);
        }
    }

    public List<String> getArguments() {
        return new ArrayList<>(arguments);
    }

    public String getArgument(int index) {
        return arguments.get(index);
    }

    public int indexOf(String arg) {
        return arguments.indexOf(arg);
    }

    /**
     * Adds list of arguments in command line.
     *
     * @param args
     *         arguments
     * @return this {@code CommandLine}
     */
    public CommandLine add(String... args) {
        if (args != null && args.length > 0) {
            Collections.addAll(arguments, args);
        }
        return this;
    }

    /**
     * Adds list of arguments in command line.
     *
     * @param args
     *         arguments
     * @return this {@code CommandLine}
     */
    public CommandLine add(List<String> args) {
        if (args != null && !args.isEmpty()) {
            arguments.addAll(args);
        }
        return this;
    }

    public boolean hasArgument(String arg) {
        return arguments.contains(arg);
    }

    public boolean removeArgument(String arg) {
        return arguments.remove(arg);
    }

    public String[] asArray() {
        return arguments.toArray(new String[arguments.size()]);
    }

    @Override
    public String toString() {
        final String[] str = asArray();
        final StringBuilder sb = new StringBuilder();
        for (String s : str) {
            if (sb.length() > 1) {
                sb.append(' ');
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
