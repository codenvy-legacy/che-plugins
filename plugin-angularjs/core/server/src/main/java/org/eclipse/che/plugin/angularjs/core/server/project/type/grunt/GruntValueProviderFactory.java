/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.angularjs.core.server.project.type.grunt;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.project.server.FileEntry;
import org.eclipse.che.api.project.server.FolderEntry;
import org.eclipse.che.api.project.server.InvalidValueException;
import org.eclipse.che.api.project.server.ValueProvider;
import org.eclipse.che.api.project.server.ValueProviderFactory;
import org.eclipse.che.api.project.server.ValueStorageException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.eclipse.che.plugin.angularjs.core.shared.ProjectAttributes.HAS_CONFIG_FILE;

/**
 * Provide value for specific property from Grunt project.
 *
 * @author Roman Nikitenko
 */
public class GruntValueProviderFactory implements ValueProviderFactory {

    public static final String GRUNT_JS_CONFIG_FILE     = "gruntfile.js";
    public static final String GRUNT_COFFEE_CONFIG_FILE = "gruntfile.coffee";

    @Override
    public ValueProvider newInstance(FolderEntry projectFolder) {
        return new GruntValueProvider(projectFolder);
    }

    protected class GruntValueProvider implements ValueProvider {

        protected FolderEntry projectFolder;

        protected GruntValueProvider(FolderEntry projectFolder) {
            this.projectFolder = projectFolder;
        }

        @Override
        public List<String> getValues(String attributeName) throws ValueStorageException {
            try {
                if (attributeName.equals(HAS_CONFIG_FILE) && isProjectContainsConfigFile(projectFolder)) {
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

        private boolean isProjectContainsConfigFile(FolderEntry folder) throws ServerException {
            List<FileEntry> children = folder.getChildFiles();
            for (FileEntry child : children) {
                String path = child.getPath().toLowerCase();
                if (path.endsWith(GRUNT_JS_CONFIG_FILE) || path.endsWith(GRUNT_COFFEE_CONFIG_FILE)) {
                    return true;
                }
            }
            return false;
        }
    }
}
