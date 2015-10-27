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

package org.eclipse.che.ide.ext.java;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.project.server.FolderEntry;
import org.eclipse.che.api.project.server.InvalidValueException;
import org.eclipse.che.api.project.server.Project;
import org.eclipse.che.api.project.server.ProjectConfig;
import org.eclipse.che.api.project.server.ProjectManager;
import org.eclipse.che.api.project.server.ProjectMisc;
import org.eclipse.che.api.project.server.ProjectTypeConstraintException;
import org.eclipse.che.api.project.server.ValueStorageException;
import org.eclipse.che.api.project.server.VirtualFileEntry;
import org.eclipse.che.api.project.server.handlers.ProjectHandlerRegistry;
import org.eclipse.che.api.project.server.type.AttributeValue;
import org.eclipse.che.api.project.server.type.ProjectTypeRegistry;
import org.eclipse.che.api.project.shared.dto.SourceEstimation;
import org.eclipse.che.api.vfs.server.VirtualFileSystemRegistry;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.user.UserImpl;
import org.eclipse.che.vfs.impl.fs.LocalFSMountStrategy;
import org.eclipse.che.vfs.impl.fs.LocalFileSystemProvider;
import org.eclipse.core.runtime.Path;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dummy implementation of ProjectManager used for tests
 * @author Evgen Vidolob
 */
public class DummyProjectManager implements ProjectManager {


    final String      vfsUser       = "dev";
    final Set<String> vfsUserGroups = new LinkedHashSet<>(Arrays.asList("workspace/developer"));
    private final LocalFileSystemProvider localFileSystemProvider;

    public DummyProjectManager(String workspacePath) {

        EnvironmentContext context = new EnvironmentContext();
        context.setUser(new UserImpl(vfsUser, "", "", vfsUserGroups, false));
        EnvironmentContext.setCurrent(context);
        localFileSystemProvider = new LocalFileSystemProvider("", new LocalFSMountStrategy() {
            @Override
            public File getMountPath(String workspaceId) throws ServerException {
                return new File(workspacePath);
            }

            @Override
            public File getMountPath() throws ServerException {
                return new File(workspacePath);
            }
        }, new EventService(), null, null);
    }

    @Override
    public List<Project> getProjects(String workspace) throws ServerException, NotFoundException, ForbiddenException {
        final FolderEntry myRoot = getProjectsRoot(workspace);
        final List<Project> projects = new ArrayList<>();
        for (FolderEntry folder : myRoot.getChildFolders()) {
            final Project project = getProject(workspace, folder.getPath());
            if (project != null) {
                projects.add(project);
            }
        }
        return projects;
    }

    @Override
    public Project getProject(String workspace, String projectPath) throws ForbiddenException, ServerException, NotFoundException {
        final FolderEntry myRoot = getProjectsRoot(workspace);
        final VirtualFileEntry child = myRoot.getChild(projectPath.startsWith("/") ? projectPath.substring(1) : projectPath);
        if (child != null && child.isFolder() && isProjectFolder((FolderEntry)child)) {
            return new Project((FolderEntry)child, this);
        }
        return null;
    }

    @Override
    public Project createProject(String workspace, String name, ProjectConfig projectConfig, Map<String, String> options)
            throws ConflictException, ForbiddenException, ServerException, ProjectTypeConstraintException, NotFoundException {
        final FolderEntry myRoot = getProjectsRoot(workspace);
        final FolderEntry projectFolder = myRoot.createFolder(name);
        final Project project = new Project(projectFolder, this);
        return project;
    }

    @Override
    public Project updateProject(String workspace, String path, ProjectConfig newConfig)
            throws ForbiddenException, ServerException, NotFoundException, ConflictException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FolderEntry getProjectsRoot(String workspace) throws ServerException, NotFoundException {
        return new FolderEntry(workspace, localFileSystemProvider.getMountPoint(true).getRoot());
    }

    @Override
    public ProjectConfig getProjectConfig(Project project) throws ServerException, ProjectTypeConstraintException, ValueStorageException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateProjectConfig(Project project, ProjectConfig config)
            throws ServerException, ValueStorageException, ProjectTypeConstraintException, InvalidValueException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProjectMisc getProjectMisc(Project project) throws ServerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveProjectMisc(Project project, ProjectMisc misc) throws ServerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Project> getProjectModules(Project project)
            throws ServerException, ForbiddenException, ConflictException, IOException, NotFoundException {
        return Collections.emptySet();
    }

    @Override
    public VirtualFileSystemRegistry getVirtualFileSystemRegistry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProjectTypeRegistry getProjectTypeRegistry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProjectHandlerRegistry getHandlers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, AttributeValue> estimateProject(String workspace, String path, String projectTypeId)
            throws ValueStorageException, ServerException, ForbiddenException, NotFoundException, ProjectTypeConstraintException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Project addModule(String workspace, String projectPath, String modulePath, ProjectConfig moduleConfig,
                             Map<String, String> options)
            throws ConflictException, ForbiddenException, ServerException, NotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SourceEstimation> resolveSources(String workspace, String path, boolean transientOnly)
            throws ServerException, ForbiddenException, NotFoundException, ValueStorageException, ProjectTypeConstraintException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Project convertFolderToProject(String workspace, String path, ProjectConfig projectConfig)
            throws ConflictException, ForbiddenException, ServerException, NotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public VirtualFileEntry rename(String workspace, String path, String newName, String newMediaType)
            throws ForbiddenException, ServerException, ConflictException, NotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(String workspace, String path, String modulePath)
            throws ServerException, ForbiddenException, NotFoundException, ConflictException {
        final FolderEntry root = getProjectsRoot(workspace);
        final VirtualFileEntry entry = root.getChild(path);
        if (entry == null) {
            return false;
        }
        entry.remove();
        return true;
    }

    @Override
    public boolean isProjectFolder(FolderEntry folder) throws ServerException {
        return new Path(folder.getPath()).segmentCount() == 1;
    }
}
