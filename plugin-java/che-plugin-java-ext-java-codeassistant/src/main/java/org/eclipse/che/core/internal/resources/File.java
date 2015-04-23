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

import org.eclipse.che.jdt.internal.core.JavaModelStatus;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.content.IContentDescription;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author Evgen Vidolob
 */
public class File  extends Resource implements IFile{

    protected File(IPath path, Workspace workspace) {
        super(path, workspace);
    }

    @Override
    public void appendContents(InputStream content, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        // funnel all operations to central method
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
        appendContents(content, updateFlags, monitor);
    }

    @Override
    public void appendContents(InputStream content, int updateFlags, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void create(InputStream content, boolean force, IProgressMonitor monitor) throws CoreException {
        // funnel all operations to central method
        create(content, (force ? IResource.FORCE : IResource.NONE), monitor);
    }

    @Override
    public void create(InputStream content, int updateFlags, IProgressMonitor monitor) throws CoreException {
        workspace.createResource(this, updateFlags);
        internalSetContents(content);
    }
    protected void internalSetContents(InputStream content) {
        workspace.setFileContent(this, content);
    }
                                       @Override
    public String getCharset() throws CoreException {
        return getCharset(true);
    }

    @Override
    public String getCharset(boolean checkImplicit) throws CoreException {
        return "UTF-8";
    }

    @Override
    public String getCharsetFor(Reader contents) throws CoreException {
        return "UTF-8";
    }

    @Override
    public IContentDescription getContentDescription() throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getContents() throws CoreException {
        return getContents(true);
    }

    @Override
    public InputStream getContents(boolean force) throws CoreException {
        try {
            return new FileInputStream(workspace.getFile(path));
        } catch (FileNotFoundException e) {
            throw new CoreException(new JavaModelStatus(IStatus.ERROR, e));
        }
    }

    @Override
    public int getEncoding() throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFileState[] getHistory(IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharset(String s) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharset(String s, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContents(InputStream inputStream, boolean b, boolean b1, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContents(IFileState iFileState, boolean b, boolean b1, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContents(InputStream inputStream, int i, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContents(IFileState iFileState, int i, IProgressMonitor iProgressMonitor) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getType() {
        return FILE;
    }
}
