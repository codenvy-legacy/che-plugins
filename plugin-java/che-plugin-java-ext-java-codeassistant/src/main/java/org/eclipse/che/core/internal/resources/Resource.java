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
import org.eclipse.core.internal.utils.WrappedRuntimeException;
import org.eclipse.core.internal.watson.IPathRequestor;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jdt.internal.ui.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public abstract class Resource implements IResource, IPathRequestor {
    /* package */ IPath     path;
    /* package */ Workspace workspace;

    protected Resource(IPath path, Workspace workspace) {
        this.path = path.removeTrailingSeparator();
        this.workspace = workspace;
    }

    @Override
    public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {
        accept(visitor, IResource.DEPTH_INFINITE, memberFlags);
    }

    @Override
    public void accept(final IResourceProxyVisitor visitor, final int depth, final int memberFlags) throws CoreException {
        java.io.File file = workspace.getFile(getFullPath());
        int maxDepth = depth == IResource.DEPTH_INFINITE ? Integer.MAX_VALUE : depth;
        try {
            final ResourceProxy resourceProxy = new ResourceProxy();
            final int  workspacePath = workspace.getAbsoluteWorkspacePath().length();
            Files.walkFileTree(file.toPath(), Collections.<FileVisitOption>emptySet(), maxDepth, new FileVisitor<java.nio.file.Path>() {
                @Override
                public FileVisitResult preVisitDirectory(java.nio.file.Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(java.nio.file.Path file, BasicFileAttributes attrs) throws IOException {
                    FileVisitResult result = FileVisitResult.CONTINUE;
                    try {
                        String string = file.toString();
                        IPath path = new Path(string.substring(workspacePath));
                        resourceProxy.info = workspace.getResourceInfo(path);
                        resourceProxy.fullPath = path;

                        boolean shouldContinue = true;
                        switch (depth) {
                            case DEPTH_ZERO :
                                shouldContinue = false;
                                break;
                            case DEPTH_ONE :
                                shouldContinue = !Resource.this.path.equals(path.removeLastSegments(1));
                                break;
                            case DEPTH_INFINITE :
                                shouldContinue = true;
                                break;
                        }
                        boolean visit = visitor.visit(resourceProxy) && shouldContinue;
                        result = visit? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
                    } catch (CoreException e) {
                        throw new WrappedRuntimeException(e);
                    } finally {
                        resourceProxy.reset();
                    }
                    return result;
                }

                @Override
                public FileVisitResult visitFileFailed(java.nio.file.Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(java.nio.file.Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, JavaPlugin.ID_PLUGIN, e.getMessage(), e));
        }
    }

    @Override
    public void accept(IResourceVisitor visitor) throws CoreException {
        accept(visitor, IResource.DEPTH_INFINITE, 0);
    }

    @Override
    public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {
        accept(visitor, depth, includePhantoms ? IContainer.INCLUDE_PHANTOMS : 0);
    }

    @Override
    public void accept(IResourceVisitor  visitor, int depth, int memberFlags) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(ISchedulingRule rule) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearHistory(IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copy(IPath  destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copy(IProjectDescription destDesc, boolean force, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copy(IProjectDescription destDesc, int updateFlags, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IResourceProxy createProxy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IMarker createMarker(String type) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(boolean force, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exists() {
        return workspace.getFile(path).exists();
    }

    @Override
    public IMarker findMarker(long id) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFileExtension() {
        String name = getName();
        int index = name.lastIndexOf('.');
        if (index == -1)
            return null;
        if (index == (name.length() - 1))
            return ""; //$NON-NLS-1$
        return name.substring(index + 1);
    }

    @Override
    public IPath getFullPath() {
        return path;
    }

    @Override
    public long getLocalTimeStamp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPath getLocation() {
        return new Path(workspace.getAbsoluteWorkspacePath() + path.toOSString());
    }

    @Override
    public URI getLocationURI() {
        IProject project = getProject();
        if (project != null && !project.exists())
            return null;
        return workspace.getFile(getFullPath()).toURI();
    }

    @Override
    public IMarker getMarker(long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getModificationStamp() {
        return 0;
    }

    @Override
    public String getName() {
        return path.lastSegment();
    }

    @Override
    public IContainer getParent() {
        int segments = path.segmentCount();
        //zero and one segments handled by subclasses
        if (segments < 2)
            Assert.isLegal(false, path.toString());
        if (segments == 2)
            return workspace.getRoot().getProject(path.segment(0));
        return (IFolder) workspace.newResource(path.removeLastSegments(1), IResource.FOLDER);
    }

    @Override
    public Map<QualifiedName, String> getPersistentProperties() throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPersistentProperty(QualifiedName qualifiedName) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProject getProject() {
        return workspace.getRoot().getProject(path.segment(0));
    }

    @Override
    public IPath getProjectRelativePath() {
        return getFullPath().removeFirstSegments(ICoreConstants.PROJECT_SEGMENT_LENGTH);
    }

    @Override
    public IPath getRawLocation() {
//        if (isLinked())
//            return FileUtil.toPath(((Project) getProject()).internalGetDescription().getLinkLocationURI(getProjectRelativePath()));
//        return getLocation();
        throw new UnsupportedOperationException();
    }

    @Override
    public URI getRawLocationURI() {
//        if (isLinked())
//            return ((Project) getProject()).internalGetDescription().getLinkLocationURI(getProjectRelativePath());
//        return getLocationURI();
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceAttributes getResourceAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<QualifiedName, Object> getSessionProperties() throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getSessionProperty(QualifiedName qualifiedName) throws CoreException {
        throw new UnsupportedOperationException();
    }

    public abstract int getType();

    public IWorkspace getWorkspace() {
        return workspace;
    }

    @Override
    public int hashCode() {
        // the container may be null if the identified resource
        // does not exist so don't bother with it in the hash
        return getFullPath().hashCode();
    }

    /* (non-Javadoc)
	 * @see IResource#isAccessible()
	 */
    public boolean isAccessible() {
        return exists();
    }

    @Override
    public boolean isDerived() {
        return isDerived(IResource.NONE);
    }

    @Override
    public boolean isDerived(int options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isHidden() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isHidden(int options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLinked() {
        return isLinked(NONE);
    }

    @Override
    public boolean isLocal(int depth) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVirtual() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLinked(int options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPhantom() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSynchronized(int depth) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTeamPrivateMember() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTeamPrivateMember(int options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void move(IProjectDescription  description, boolean force, boolean keepHistory, IProgressMonitor monitor)
            throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void revertModificationStamp(long value) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPath requestPath() {
        return getFullPath();
    }

    @Override
    public String requestName() {
        return getName();
    }

    @Override
    public void setDerived(boolean isDerived) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDerived(boolean isDerived, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHidden(boolean isHidden) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long setLocalTimeStamp(long value) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPersistentProperty(QualifiedName key, String value) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReadOnly(boolean readonly) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setResourceAttributes(ResourceAttributes attributes) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSessionProperty(QualifiedName key, Object value) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {
        throw new UnsupportedOperationException();
    }

    public String getTypeString() {
        switch (getType()) {
            case FILE :
                return "L"; //$NON-NLS-1$
            case FOLDER :
                return "F"; //$NON-NLS-1$
            case PROJECT :
                return "P"; //$NON-NLS-1$
            case ROOT :
                return "R"; //$NON-NLS-1$
        }
        return ""; //$NON-NLS-1$
    }

    @Override
    public Object getAdapter(Class aClass) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see IProject#delete(boolean, boolean, IProgressMonitor)
     * @see IWorkspaceRoot#delete(boolean, boolean, IProgressMonitor)
     * N.B. This is not an IResource method!
     */
    public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
        delete(updateFlags, monitor);
    }

    @Override
    public boolean isConflicting(ISchedulingRule rule) {
        if (this == rule)
            return true;
        //must not schedule at same time as notification
        if (rule.getClass().equals(WorkManager.NotifyRule.class))
            return true;
        if (rule instanceof MultiRule) {
            MultiRule multi = (MultiRule) rule;
            ISchedulingRule[] children = multi.getChildren();
            for (int i = 0; i < children.length; i++)
                if (isConflicting(children[i]))
                    return true;
            return false;
        }
        if (!(rule instanceof IResource))
            return false;
        IResource resource = (IResource) rule;
        if (!workspace.equals(resource.getWorkspace()))
            return false;
        IPath otherPath = resource.getFullPath();
        return path.isPrefixOf(otherPath) || otherPath.isPrefixOf(path);
    }

    @Override
    public void touch(IProgressMonitor iProgressMonitor) throws CoreException {
        // do nothing
        //todo
    }

    /* (non-Javadoc)
     * @see IResource#getPathVariableManager()
     */
    public IPathVariableManager getPathVariableManager() {
//        if (getProject() == null)
//            return workspace.getPathVariableManager();
//        return new ProjectPathVariableManager(this);
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
         * @see Object#toString()
         */
    @Override
    public String toString() {
        return getTypeString() + getFullPath().toString();
    }

    /* (non-Javadoc)
 * @see IResource#equals(Object)
 */
    @Override
    public boolean equals(Object target) {
        if (this == target)
            return true;
        if (!(target instanceof Resource))
            return false;
        Resource resource = (Resource) target;
        return getType() == resource.getType() && path.equals(resource.path) && workspace.equals(resource.workspace);
    }

    /* (non-Javadoc)
 * @see IFile#move(IPath, boolean, boolean, IProgressMonitor)
 * @see IFolder#move(IPath, boolean, boolean, IProgressMonitor)
 */
    public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
        move(destination, updateFlags, monitor);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.core.resources.IFolder#createLink(IPath, int, IProgressMonitor)
	 * @see org.eclipse.core.resources.IFile#createLink(IPath, int, IProgressMonitor)
	 */
    public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {
//        Assert.isNotNull(localLocation);
//        createLink(URIUtil.toURI(localLocation), updateFlags, monitor);
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFolder#createLink(URI, int, IProgressMonitor)
     * @see org.eclipse.core.resources.IFile#createLink(URI, int, IProgressMonitor)
     */
    public void createLink(URI localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }
}
