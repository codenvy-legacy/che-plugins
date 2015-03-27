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
package org.eclipse.che.ide.ext.git.server;

import org.eclipse.che.api.core.util.LineConsumerFactory;
import org.eclipse.che.ide.ext.git.shared.GitUser;

import java.io.File;

/** @author andrew00x */
public abstract class GitConnectionFactory {
    /**
     * Get connection to Git repository located in <code>workDir</code>.
     *
     * @param workDir
     *         repository directory
     * @param user
     *         user
     * @return connection to Git repository
     * @throws GitException
     *         if can't initialize connection
     */
    public final GitConnection getConnection(String workDir, GitUser user) throws GitException {
        return getConnection(new File(workDir), user, LineConsumerFactory.NULL);
    }

    /**
     * Get connection to Git repository located in <code>workDir</code>
     *
     * @param workDir
     *         repository directory
     * @return connection to Git repository
     * @throws GitException
     *         if can't initialize connection
     */
    public final GitConnection getConnection(String workDir) throws GitException {
        return getConnection(new File(workDir));
    }

    /**
     * Get connection to Git repository located in <code>workDir</code>.
     *
     * @param workDir
     *         repository directory
     * @param user
     *         user
     * @param outputPublisherFactory
     *         a consumer factory for git output
     * @return connection to Git repository
     * @throws GitException
     *         if can't initialize connection
     */
    public final GitConnection getConnection(String workDir, GitUser user, LineConsumerFactory outputPublisherFactory) throws GitException {
        return getConnection(new File(workDir), user, outputPublisherFactory);
    }

    /**
     * Get connection to Git repository located in <code>workDir</code>
     *
     * @param workDir
     *         repository directory
     * @param outputPublisherFactory
     *         a factory consumer for git output
     * @return connection to Git repository
     * @throws GitException
     *         if can't initialize connection
     */
    public final GitConnection getConnection(String workDir, LineConsumerFactory outputPublisherFactory) throws GitException {
        return getConnection(new File(workDir), outputPublisherFactory);
    }

    /**
     * Get connection to Git repository located in <code>workDir</code>
     *
     * @param workDir
     *         repository directory
     * @return connection to Git repository
     * @throws GitException
     *         if can't initialize connection
     */
    public final GitConnection getConnection(File workDir) throws GitException {
        return getConnection(workDir, LineConsumerFactory.NULL);
    }

    /**
     * Get connection to Git repository located in <code>workDir</code>.
     *
     * @param workDir
     *         repository directory
     * @param user
     *         user
     * @param outputPublisherFactory
     *         to create a consumer for git output
     * @return connection to Git repository
     * @throws GitException
     *         if can't initialize connection
     */
    public abstract GitConnection getConnection(File workDir, GitUser user, LineConsumerFactory outputPublisherFactory) throws GitException;

    /**
     * Get connection to Git repository locate in <code>workDir</code>
     *
     * @param workDir
     *         repository directory
     * @param outputPublisherFactory
     *         to create a consumer for git output
     * @return connection to Git repository
     * @throws GitException
     *         if can't initialize connection
     */
    public abstract GitConnection getConnection(File workDir, LineConsumerFactory outputPublisherFactory) throws GitException;
}