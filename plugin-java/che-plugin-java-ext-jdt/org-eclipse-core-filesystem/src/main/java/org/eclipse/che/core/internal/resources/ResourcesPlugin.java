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

package org.eclipse.che.core.internal.resources;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eclipse.core.internal.utils.Messages;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class ResourcesPlugin {
    private static final Logger    LOG       = LoggerFactory.getLogger(ResourcesPlugin.class);
    /**
     * The workspace managed by the single instance of this
     * plug-in runtime class, or <code>null</code> is there is none.
     */
    private static       Workspace workspace = null;
    private static String indexPath;
    private static String workspacePath;
    private static String pluginId;

    @Inject
    public ResourcesPlugin(@Named("che.jdt.workspace.index.dir") String indexPath, @Named("vfs.local.fs_root_dir") String workspacePath) {
        ResourcesPlugin.indexPath = indexPath;
        ResourcesPlugin.workspacePath = workspacePath;
    }

    public static String getPathToWorkspace() {
        return workspacePath;
    }

    public static String getIndexPath() {
        return indexPath;
    }

    public static String getPluginId() {
        return pluginId;
    }

    @PostConstruct
    public void start() {
        workspace = new Workspace(workspacePath);
    }

    /**
     * Returns the workspace. The workspace is not accessible after the resources
     * plug-in has shutdown.
     *
     * @return the workspace that was created by the single instance of this
     *   plug-in class.
     */
    public static IWorkspace getWorkspace() {
        if (workspace == null)
            throw new IllegalStateException(Messages.resources_workspaceClosed);
        return workspace;
    }

    public static void log(Exception e) {
        LOG.error(e.getMessage(), e);
    }

    public static void log(IStatus status) {
        LOG.error(status.getMessage(), status.getException());
    }
}
