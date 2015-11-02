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

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.model.workspace.ModuleConfig;
import org.eclipse.che.api.project.server.ProjectManager;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Evgen Vidolob
 */
public class WorkspaceRoot extends Container implements IWorkspaceRoot {
    public static final  String PROJECT_INNER_SETTING_DIR = ".codenvy";
    private static final Logger LOG                       = LoggerFactory.getLogger(WorkspaceRoot.class);
    /**
     * As an optimization, we store a table of project handles
     * that have been requested from this root.  This maps project
     * name strings to project handles.
     */
    private final        ConcurrentMap<String, Project>
                                projectTable              = new ConcurrentHashMap<>(16);


    protected WorkspaceRoot(IPath path, Workspace workspace) {
        super(path, workspace);
    }

    @Override
    public IContainer[] findContainersForLocation(IPath iPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContainer[] findContainersForLocationURI(URI uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContainer[] findContainersForLocationURI(URI uri, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFile[] findFilesForLocation(IPath iPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFile[] findFilesForLocationURI(URI uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFile[] findFilesForLocationURI(URI uri, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContainer getContainerForLocation(IPath iPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFile getFileForLocation(IPath iPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProject getProject(String name) {
        //first check our project cache
        Project result = projectTable.get(name);
        if (result == null) {
            IPath projectPath = new Path(null, name).makeAbsolute();
//            String message = "Path for project must have only one segment."; //$NON-NLS-1$
//            Assert.isLegal(projectPath.segmentCount() == ICoreConstants.PROJECT_SEGMENT_LENGTH, message);
            //try to get the project using a canonical name
            String canonicalName = projectPath.toOSString(); //.lastSegment();
            result = projectTable.get(canonicalName);
            if (result != null)
                return result;
            result = new Project(projectPath, workspace);
            projectTable.putIfAbsent(canonicalName, result);
        }
        return result;
    }

    @Override
    public IProject[] getProjects() {
        ProjectManager manager = workspace.getProjectManager();
        List<IProject> projects = new ArrayList<>();
        try {
            List<org.eclipse.che.api.project.server.Project> rootProjects = manager.getProjects(workspace.getWsId());
            for (org.eclipse.che.api.project.server.Project rootProject : rootProjects) {
                projects.add(new Project(new Path(rootProject.getPath()), workspace));
                addAllModules(projects, rootProject, manager);
            }
        } catch (ServerException | NotFoundException | ForbiddenException | IOException | ConflictException e) {
            LOG.error(e.getMessage(), e);
        }

        return projects.toArray(new IProject[projects.size()]);
    }

    private void addAllModules(List<IProject> projects, org.eclipse.che.api.project.server.Project project, ProjectManager manager)
            throws IOException, ForbiddenException, ConflictException, NotFoundException, ServerException {

        List<ModuleConfig> modules = manager.getProjectModules(project);

        for (ModuleConfig module : modules) {
            addModules(projects, module);
        }
    }

    private void addModules(List<IProject> projects, ModuleConfig moduleConfig) {
        List<? extends ModuleConfig> modules = moduleConfig.getModules();

        if (modules.isEmpty()) {
            return;
        }

        for (ModuleConfig module : modules) {
            Project project = new Project(new Path(module.getPath()), workspace);

            projects.add(project);

            addModules(projects, moduleConfig);
        }
    }

    @Override
    public IProject[] getProjects(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDefaultCharset(boolean b) throws CoreException {
        return "UTF-8";
    }

    @Override
    public int getType() {
        return ROOT;
    }
}
