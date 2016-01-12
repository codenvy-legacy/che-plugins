/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.docker.machine.local.node.provider;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.plugin.docker.machine.node.WorkspaceFolderPathProvider;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides path to workspace folder in CHE.
 *
 * @author Alexander Garagatyi
 */
@Singleton
public class LocalWorkspaceFolderPathProvider implements WorkspaceFolderPathProvider {

    /**
     * this value provide path to projects on local host
     * if this value will be set all workspace will manage
     * same projects from your host
     */
    @Inject(optional = true)
    @Named("host.projects.root")
    private String hostProjectsFolder;

    /**
     * Value provide path to directory on host machine where will by all created and mount to the
     * created workspaces folder that become root of workspace inside machine.
     * Inside machine it will point to the directory described by @see che.projects.root.
     *
     * For example:
     * if you set "host.workspaces.root" to the /home/user/che/workspaces after creating new workspace will be created new folder
     * /home/user/che/workspaces/{workspaceId} and it will be mount to the  dev-machine to "che.projects.root"
     */
    final String workspacesMountPoint;

    @Inject
    public LocalWorkspaceFolderPathProvider(@Named("host.workspaces.root") String workspacesMountPoint) throws IOException {
        this.workspacesMountPoint = workspacesMountPoint;
        checkProps(workspacesMountPoint, hostProjectsFolder);
    }

    //used for testing
    protected LocalWorkspaceFolderPathProvider(String workspacesMountPoint, String projectsFolder) throws IOException {
        checkProps(workspacesMountPoint, projectsFolder);
        this.workspacesMountPoint = workspacesMountPoint;
        this.hostProjectsFolder = projectsFolder;
    }

    private void checkProps(String workspacesFolder, String projectsFolder) throws IOException {
        if (workspacesFolder == null && projectsFolder == null) {
            throw new IOException("Can't mount host file system. Check host.workspaces.root or host.projects.root configuration property.");
        }
        if (workspacesFolder != null) {
            ensureExist(workspacesFolder, "host.workspaces.root");
        }
        if (projectsFolder != null) {
            ensureExist(projectsFolder, "host.projects.root");
        }
    }


    private void ensureExist(String path, String prop) throws IOException {
        Path folder = Paths.get(path);
        if (Files.notExists(folder)) {
            Files.createDirectory(folder);
        }
        if (!Files.isDirectory(folder)) {
            throw new IOException(String.format("Projects %s is not directory. Check %s configuration property.", path, prop));
        }
    }

    @Override
    public String getPath(@Assisted("workspace") String workspaceId) throws IOException {
        if (hostProjectsFolder != null) {
            return hostProjectsFolder;
        } else {
            Path folder = Paths.get(workspacesMountPoint).resolve(workspaceId);
            if (Files.notExists(folder)) {
                Files.createDirectory(folder);
            }
            return folder.toString();
        }
    }
}
