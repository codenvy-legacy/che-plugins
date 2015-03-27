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

import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.shared.Tag;

import java.io.File;

/**
 * Create tag
 *
 * @author Eugene Voevodin
 */
public class TagCreateCommand extends GitCommand<Tag> {

    private String  name;
    private String  commit;
    private String  message;
    private boolean force;

    public TagCreateCommand(File repository) {
        super(repository);
    }

    /** @see GitCommand#execute() */
    @Override
    public Tag execute() throws GitException {
        if (name == null) {
            throw new GitException("Name wasn't set.");
        }
        reset();
        commandLine.add("tag", name);
        if (commit != null) {
            commandLine.add(commit);
        }
        if (message != null) {
            commandLine.add("-m", message);
        }
        if (force) {
            commandLine.add("--force");
        }
        start();
        return DtoFactory.getInstance().createDto(Tag.class).withName(name);
    }

    /**
     * @param name
     *         tag name
     * @return TagCreateCommand with established name
     */
    public TagCreateCommand setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param commit
     *         commit start point for tag creating
     * @return TagCreateCommand with established commit
     */
    public TagCreateCommand setCommit(String commit) {
        this.commit = commit;
        return this;
    }

    /**
     * @param message
     *         tag message
     * @return TagCreateCommand with established message
     */
    public TagCreateCommand setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * @param force
     *         force tag creating
     * @return TagCreateCommand with established force parameter
     */
    public TagCreateCommand setForce(boolean force) {
        this.force = force;
        return this;
    }
}
