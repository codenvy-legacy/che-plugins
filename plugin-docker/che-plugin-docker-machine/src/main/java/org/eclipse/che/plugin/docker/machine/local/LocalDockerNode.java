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
package org.eclipse.che.plugin.docker.machine.local;

import org.eclipse.che.api.machine.server.exception.MachineException;
import org.eclipse.che.plugin.docker.client.DockerConnectorConfiguration;
import org.eclipse.che.plugin.docker.machine.DockerNode;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * Local directory as a workspace (projects tree) to be bound to Machines
 *
 * @author gazarenkov
 */
public class LocalDockerNode implements DockerNode {

    private final File folder;
    private String host;

    @Inject
    public LocalDockerNode(@Named("host.projects.root") String projectsFolder) throws IOException {

        folder = new File(projectsFolder);
        if (!folder.exists()) {
            Files.createDirectory(folder.toPath());
        }
        if (folder.isFile()) {
            throw new IOException(
                    "Folder " + folder.getAbsolutePath() + " does not exist. Check vfs.local.fs_root_dir configuration property.");
        }

        host = DockerConnectorConfiguration.getExpectedLocalHost();

    }

    @Override
    public void bindWorkspace(String workspaceId, String hostProjectsFolder) throws MachineException {

    }

    @Override
    public void unbindWorkspace(String workspaceId, String hostProjectsFolder) throws MachineException {

    }

    @Override
    public String getProjectsFolder() {
        return folder.getAbsolutePath();
    }


    @Override
    public String getHost() {
        return host;
    }
}
