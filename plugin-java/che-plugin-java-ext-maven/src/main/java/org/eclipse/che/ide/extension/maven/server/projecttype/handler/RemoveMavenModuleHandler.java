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
package org.eclipse.che.ide.extension.maven.server.projecttype.handler;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.project.server.FolderEntry;
import org.eclipse.che.api.project.server.ProjectConfig;
import org.eclipse.che.api.project.server.VirtualFileEntry;
import org.eclipse.che.api.project.server.handlers.RemoveModuleHandler;
import org.eclipse.che.ide.extension.maven.shared.MavenAttributes;
import org.eclipse.che.ide.maven.tools.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Roman Nikitenko
 */
public class RemoveMavenModuleHandler implements RemoveModuleHandler {

    private final static Logger logger = LoggerFactory.getLogger(RemoveMavenModuleHandler.class);

    @Override
    public String getProjectType() {
        return MavenAttributes.MAVEN_ID;
    }

    @Override
    public void onRemoveModule(FolderEntry parentFolder, String modulePath, ProjectConfig moduleConfig)
            throws ForbiddenException, ConflictException, ServerException {
        if (!moduleConfig.getTypeId().equals(MavenAttributes.MAVEN_ID)) {
            logger.warn("Module isn't Maven module");
            throw new IllegalArgumentException("Module isn't Maven module");
        }
        VirtualFileEntry pom = parentFolder.getChild("pom.xml");
        if (pom == null) {
            throw new IllegalArgumentException("Can't find pom.xml file in path: " + parentFolder.getPath());
        }
        try {
            Model model = Model.readFrom(pom.getVirtualFile());
            if (model.getModules().contains(modulePath)) {
                model.removeModule(modulePath);
                model.writeTo(pom.getVirtualFile());
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }
}
