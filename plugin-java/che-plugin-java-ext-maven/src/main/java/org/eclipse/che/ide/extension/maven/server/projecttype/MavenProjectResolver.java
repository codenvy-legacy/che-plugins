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
package org.eclipse.che.ide.extension.maven.server.projecttype;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.model.workspace.ModuleConfig;
import org.eclipse.che.api.project.server.FolderEntry;
import org.eclipse.che.api.project.server.Project;
import org.eclipse.che.api.project.server.ProjectManager;
import org.eclipse.che.api.project.server.VirtualFileEntry;
import org.eclipse.che.api.workspace.server.model.impl.ModuleConfigImpl;
import org.eclipse.che.api.workspace.server.model.impl.ProjectConfigImpl;
import org.eclipse.che.ide.maven.tools.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.extension.maven.shared.MavenAttributes.ARTIFACT_ID;
import static org.eclipse.che.ide.extension.maven.shared.MavenAttributes.GROUP_ID;
import static org.eclipse.che.ide.extension.maven.shared.MavenAttributes.MAVEN_ID;
import static org.eclipse.che.ide.extension.maven.shared.MavenAttributes.PACKAGING;
import static org.eclipse.che.ide.extension.maven.shared.MavenAttributes.VERSION;

/**
 * @author Evgen Vidolob
 * @author Dmitry Shnurenko
 */
public class MavenProjectResolver {

    /**
     * The method allows define project structure as it is in project tree. Project can has got some modules and each module can has got
     * own modules.
     *
     * @param projectFolder
     *         base folder which represents project
     * @param projectManager
     *         special manager which is necessary for updating project after resolve
     * @throws ConflictException
     * @throws ForbiddenException
     * @throws ServerException
     * @throws NotFoundException
     * @throws IOException
     */
    public static void resolve(FolderEntry projectFolder, ProjectManager projectManager) throws ConflictException,
                                                                                                ForbiddenException,
                                                                                                ServerException,
                                                                                                NotFoundException,
                                                                                                IOException {
        VirtualFileEntry pom = projectFolder.getChild("pom.xml");

        if (pom == null) {
            return;
        }

        Model model = Model.readFrom(pom.getVirtualFile());
        MavenClassPathConfigurator.configure(projectFolder);

        String packaging = model.getPackaging();
        if (packaging != null && packaging.equals("pom")) {
            String workspaceId = projectFolder.getWorkspace();
            Project project = projectManager.getProject(workspaceId, projectFolder.getPath());

            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put(ARTIFACT_ID, Arrays.asList(model.getArtifactId()));
            attributes.put(GROUP_ID, Arrays.asList(model.getGroupId()));
            attributes.put(VERSION, Arrays.asList(model.getVersion()));
            attributes.put(PACKAGING, Arrays.asList(model.getPackaging()));

            ProjectConfigImpl projectConfig = new ProjectConfigImpl();
            projectConfig.setName(projectFolder.getName());
            projectConfig.setDescription(model.getDescription());
            projectConfig.setAttributes(attributes);
            projectConfig.setType(MAVEN_ID);

            List<ModuleConfig> modules = new ArrayList<>();

            for (FolderEntry folderEntry : project.getBaseFolder().getChildFolders()) {
                MavenClassPathConfigurator.configure(folderEntry);

                defineModules(folderEntry, modules);
            }

            projectConfig.setModules(modules);

            project.updateConfig(projectConfig);
        }
    }

    private static void defineModules(FolderEntry folderEntry, List<ModuleConfig> modules) throws ServerException,
                                                                                                  ForbiddenException,
                                                                                                  IOException,
                                                                                                  ConflictException {
        VirtualFileEntry pom = folderEntry.getChild("pom.xml");

        if (pom == null) {
            return;
        }

        Model model = Model.readFrom(pom.getVirtualFile());

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(ARTIFACT_ID, Arrays.asList(model.getArtifactId()));
        attributes.put(GROUP_ID, Arrays.asList(model.getGroupId()));
        attributes.put(VERSION, Arrays.asList(model.getVersion()));
        attributes.put(PACKAGING, Arrays.asList(model.getPackaging()));

        ModuleConfigImpl moduleConfig = new ModuleConfigImpl();
        moduleConfig.setType(MAVEN_ID);
        moduleConfig.setName(folderEntry.getName());
        moduleConfig.setPath(folderEntry.getPath());
        moduleConfig.setAttributes(attributes);
        moduleConfig.setDescription(model.getDescription());

        List<ModuleConfig> internalModules = new ArrayList<>();

        for (FolderEntry internalModule : folderEntry.getChildFolders()) {
            MavenClassPathConfigurator.configure(folderEntry);

            defineModules(internalModule, internalModules);
        }

        moduleConfig.setModules(internalModules);

        modules.add(moduleConfig);
    }
}
