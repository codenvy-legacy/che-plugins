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

import org.eclipse.core.internal.resources.ICoreConstants;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFilterMatcherDescriptor;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ISynchronizer;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class Workspace implements IWorkspace{
    protected final IWorkspaceRoot defaultRoot = new WorkspaceRoot(Path.ROOT, this);
    private String wsPath;
    private java.io.File workspaceFile;

    public Workspace(String path) {
        this.wsPath = path;
    }

    public String getAbsoluteWorkspacePath() {
        return wsPath;
    }

    public Resource newResource(IPath path, int type) {
        String message;
        switch (type) {
            case IResource.FOLDER:
                if (path.segmentCount() < ICoreConstants.MINIMUM_FOLDER_SEGMENT_LENGTH) {
                    message = "Path must include project and resource name: " + path.toString(); //$NON-NLS-1$
                    Assert.isLegal(false, message);
                }
                return new Folder(path.makeAbsolute(), this);
            case IResource.FILE:
                if (path.segmentCount() < ICoreConstants.MINIMUM_FILE_SEGMENT_LENGTH) {
                    message = "Path must include project and resource name: " + path.toString(); //$NON-NLS-1$
                    Assert.isLegal(false, message);
                }
                return new File(path.makeAbsolute(), this);
            case IResource.PROJECT:
                return (Resource)getRoot().getProject(path.toOSString());
            case IResource.ROOT:
                return (Resource)getRoot();
        }
        Assert.isLegal(false);
        // will never get here because of assertion.
        return null;
    }

    @Override
    public void addResourceChangeListener(IResourceChangeListener iResourceChangeListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addResourceChangeListener(IResourceChangeListener iResourceChangeListener, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ISavedState addSaveParticipant(Plugin plugin, ISaveParticipant iSaveParticipant) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ISavedState addSaveParticipant(String s, ISaveParticipant iSaveParticipant) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void build(int i, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void build(IBuildConfiguration[] iBuildConfigurations, int i, boolean b, IProgressMonitor iProgressMonitor)
            throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkpoint(boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProject[][] computePrerequisiteOrder(IProject[] iProjects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProjectOrder computeProjectOrder(IProject[] iProjects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus copy(IResource[] iResources, IPath iPath, boolean b, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus copy(IResource[] iResources, IPath iPath, int i, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus delete(IResource[] iResources, boolean b, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus delete(IResource[] iResources, int i, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteMarkers(IMarker[] iMarkers) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forgetSavedTree(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFilterMatcherDescriptor[] getFilterMatcherDescriptors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFilterMatcherDescriptor getFilterMatcherDescriptor(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProjectNatureDescriptor[] getNatureDescriptors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProjectNatureDescriptor getNatureDescriptor(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<IProject, IProject[]> getDanglingReferences() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IWorkspaceDescription getDescription() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IWorkspaceRoot getRoot() {
        return defaultRoot;
    }

    @Override
    public IResourceRuleFactory getRuleFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ISynchronizer getSynchronizer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAutoBuilding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTreeLocked() {
        //todo
        return false;
    }

    @Override
    public IProjectDescription loadProjectDescription(IPath iPath) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProjectDescription loadProjectDescription(InputStream inputStream) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus move(IResource[] iResources, IPath iPath, boolean b, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus move(IResource[] iResources, IPath iPath, int i, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBuildConfiguration newBuildConfig(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProjectDescription newProjectDescription(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeResourceChangeListener(IResourceChangeListener iResourceChangeListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSaveParticipant(Plugin plugin) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSaveParticipant(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void run(IWorkspaceRunnable action, ISchedulingRule rule, int options, IProgressMonitor monitor)
            throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void run(IWorkspaceRunnable action, IProgressMonitor monitor) throws CoreException {
        run(action, defaultRoot, IWorkspace.AVOID_UPDATE, monitor);
    }

    @Override
    public IStatus save(boolean b, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(IWorkspaceDescription iWorkspaceDescription) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] sortNatureSet(String[] strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus validateEdit(IFile[] iFiles, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus validateFiltered(IResource iResource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus validateLinkLocation(IResource iResource, IPath iPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus validateLinkLocationURI(IResource iResource, URI uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus validateName(String s, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus validateNatureSet(String[] strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus validatePath(String s, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus validateProjectLocation(IProject iProject, IPath iPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus validateProjectLocationURI(IProject iProject, URI uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPathVariableManager getPathVariableManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAdapter(Class aClass) {
        throw new UnsupportedOperationException();
    }

    public java.io.File getFile(IPath path) {
        return new java.io.File(wsPath, path.toOSString());
    }

    public ResourceInfo getResourceInfo(IPath path) {
        java.io.File file = getFile(path);
        if(file.exists()) {
            return newElement(getType(file));
        }
        return null;
    }

    private int getType(java.io.File file){
        if(file.isFile()){
            return IResource.FILE;
        } else {
            java.io.File codenvy = new java.io.File(file, ".codenvy");
            if(codenvy.exists()){
                return IResource.PROJECT;
            }else {
                return IResource.FOLDER;
            }
        }
    }

    /**
     * Create and return a new tree element of the given type.
     */
    protected ResourceInfo newElement(int type) {
        ResourceInfo result = null;
        switch (type) {
            case IResource.FILE :
            case IResource.FOLDER :
                result = new ResourceInfo(type);
                break;
            case IResource.PROJECT :
                result = new ResourceInfo(type);
                break;
            case IResource.ROOT :
                result = new ResourceInfo(type);
                break;
        }

        return result;
    }

    public IResource[] getChildren(IPath path) {
        java.io.File file = getFile(path);
        if(file.exists() && file.isDirectory()){
            java.io.File[] list = file.listFiles();
            if(list!=null) {
                IResource[] resources = new IResource[list.length];
                for (int i = 0; i < list.length; i++) {

                    java.io.File child = list[i];
                    IPath iPath = new Path(child.getPath().substring(wsPath.length()));
                    resources[i] = newResource(iPath, getType(child));
                }
                return resources;
            }
        }

        return ICoreConstants.EMPTY_RESOURCE_ARRAY;
    }

    public java.io.File getWorkspaceFile() {
        return workspaceFile;
    }
}
