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
package org.eclipse.che.ide.ext.ruby.server.project.type;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.project.server.FolderEntry;
import org.eclipse.che.api.project.server.InvalidValueException;
import org.eclipse.che.api.project.server.ValueProvider;
import org.eclipse.che.api.project.server.ValueProviderFactory;
import org.eclipse.che.api.project.server.ValueStorageException;
import org.eclipse.che.api.project.server.VirtualFileEntry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.eclipse.che.ide.ext.ruby.shared.ProjectAttributes.HAS_RUBY_FILES;
/**
 * Provide value for specific property from ruby project.
 *
 * @author Roman Nikitenko
 */
public class RubyValueProviderFactory implements ValueProviderFactory {

    public static final String RUBY_EXTESION = ".rb";

    @Override
    public ValueProvider newInstance(FolderEntry projectFolder) {
        return new RubyValueProvider(projectFolder);
    }

    protected class RubyValueProvider implements ValueProvider {

        protected FolderEntry projectFolder;

        protected RubyValueProvider(FolderEntry projectFolder) {
            this.projectFolder = projectFolder;
        }

        @Override
        public List<String> getValues(String attributeName) throws ValueStorageException {
            try {
                if (attributeName.equals(HAS_RUBY_FILES) && isFolderContainsRubyFiles(projectFolder)) {
                    return Arrays.asList("true");
                }
                return Collections.<String>emptyList();
            } catch (ServerException e) {
                throw new ValueStorageException(String.format("Can't get children for the project %s: ", projectFolder.getPath()) + e.getMessage());
            }
        }

        @Override
        public void setValues(String attributeName, List<String> value) throws ValueStorageException, InvalidValueException {
            //ignore for now
        }

        private boolean isFolderContainsRubyFiles(FolderEntry folder) throws ServerException {
            List<VirtualFileEntry> children = folder.getChildren();
            for (VirtualFileEntry child : children) {
                if (child.isFile() && child.getPath().endsWith(RUBY_EXTESION)) {
                    return true;
                }
                if (child.isFolder() && isFolderContainsRubyFiles((FolderEntry)child)) {
                    return true;
                }
            }
            return false;
        }
    }
}
