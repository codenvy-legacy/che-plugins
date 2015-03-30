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
package org.eclipse.che.ide.extension.ant.server.project.type;

import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.project.server.FolderEntry;
import org.eclipse.che.api.project.server.InvalidValueException;
import org.eclipse.che.api.project.server.Project;
import org.eclipse.che.api.project.server.ProjectConfig;
import org.eclipse.che.api.project.server.ProjectManager;
import org.eclipse.che.api.project.server.ProjectTypeConstraintException;
import org.eclipse.che.api.project.server.ProjectTypeResolver;
import org.eclipse.che.api.project.server.ValueStorageException;
import org.eclipse.che.api.project.server.type.ProjectType;
import org.eclipse.che.api.project.shared.Builders;
import org.eclipse.che.ide.extension.ant.shared.AntAttributes;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author Vladyslav Zhukovskii */
@Singleton
public class AntProjectTypeResolver implements ProjectTypeResolver {

    @Inject
    private ProjectManager projectManager;

    /** {@inheritDoc} */
    @Override
    public boolean resolve(FolderEntry folderEntry)
            throws ServerException, ValueStorageException, InvalidValueException {
        try {
            if (!folderEntry.isProjectFolder()) {
                ProjectType projectType = projectManager.getProjectTypeRegistry().getProjectType(AntAttributes.ANT_ID);
                if (projectType == null) {
                    return false;
                }
                if (folderEntry.getChild(AntAttributes.BUILD_FILE) == null) {
                    return false;
                }
                Project project = new Project(folderEntry, projectManager);
                project.updateConfig(createProjectConfig(projectType));
                return true;
            }
            return false;//project configure in initial source
        } catch (ForbiddenException | ProjectTypeConstraintException e) {
            throw new ServerException("An error occurred when trying to resolve ant project.", e);
        }
    }

    /** Create new {@link org.eclipse.che.api.project.shared.Builders} description for resolved project. */
    private ProjectConfig createProjectConfig(ProjectType projectType) {
        Builders builders = new Builders();
        builders.setDefault("ant");
        return new ProjectConfig("Ant project type", projectType.getId(), null, null, new Builders(projectType.getDefaultBuilder()), null);
    }
}
