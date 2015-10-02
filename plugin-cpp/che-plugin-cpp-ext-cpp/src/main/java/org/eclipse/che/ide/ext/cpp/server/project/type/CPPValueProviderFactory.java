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

package org.eclipse.che.ide.ext.cpp.server.project.type;

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
import java.util.regex.Pattern;

import static org.eclipse.che.ide.ext.cpp.shared.ProjectAttributes.HAS_CPP_FILES;

/**
 * Provide value for specific property from CPP project.
 *
 * @author Roman Nikitenko
 */
public class CPPValueProviderFactory implements ValueProviderFactory {

    private static final Pattern CPP_FILE_EXTENSIONS_PATTERN = Pattern.compile("([^\\s]+(\\.(?i)(cpp|c\\+\\+|cc|h|hpp|hh|h\\+\\+|cxx|hxx))$)");

    @Override
    public ValueProvider newInstance(FolderEntry projectFolder) {
        return new CPPValueProvider(projectFolder);
    }

    protected class CPPValueProvider implements ValueProvider {

        protected FolderEntry projectFolder;

        protected CPPValueProvider(FolderEntry projectFolder) {
            this.projectFolder = projectFolder;
        }

        @Override
        public List<String> getValues(String attributeName) throws ValueStorageException {
            try {
                if (attributeName.equals(HAS_CPP_FILES) && isFolderContainsCPPFiles(projectFolder)) {
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

        private boolean isFolderContainsCPPFiles(FolderEntry folder) throws ServerException {
            List<VirtualFileEntry> children = folder.getChildren();
            for (VirtualFileEntry child : children) {
                String path = child.getPath();
                if (child.isFile() && CPP_FILE_EXTENSIONS_PATTERN.matcher(path).matches()) {
                    return true;
                }
                if (child.isFolder() && isFolderContainsCPPFiles((FolderEntry)child)) {
                    return true;
                }
            }
            return false;
        }
    }
}
