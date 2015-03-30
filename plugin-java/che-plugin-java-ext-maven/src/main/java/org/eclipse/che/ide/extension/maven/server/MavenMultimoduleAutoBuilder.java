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
package org.eclipse.che.ide.extension.maven.server;

import org.eclipse.che.api.builder.dto.BuildOptions;
import org.eclipse.che.api.builder.dto.BuildTaskDescriptor;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.notification.EventSubscriber;
import org.eclipse.che.api.core.rest.HttpJsonHelper;
import org.eclipse.che.api.project.server.*;

import org.eclipse.che.api.project.server.Project;
import org.eclipse.che.api.project.server.ProjectCreatedEvent;
import org.eclipse.che.api.project.server.ProjectManager;
import org.eclipse.che.api.project.server.ProjectTypeConstraintException;
import org.eclipse.che.api.project.server.ValueStorageException;
import org.eclipse.che.api.project.server.type.AttributeValue;
import org.eclipse.che.commons.lang.Pair;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.extension.maven.shared.MavenAttributes;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class MavenMultimoduleAutoBuilder implements EventSubscriber<ProjectCreatedEvent> {

    private ProjectManager manager;
    private static final Logger LOG = LoggerFactory.getLogger(MavenMultimoduleAutoBuilder.class);
    @Inject
    @Named("api.endpoint")
    private String apiUrl;

    @Inject
    public MavenMultimoduleAutoBuilder(EventService eventService, ProjectManager manager) {
        this.manager = manager;
        eventService.subscribe(this);
    }

    @Override
    public void onEvent(ProjectCreatedEvent event) {
        try {
            Project project = manager.getProject(event.getWorkspaceId(), event.getProjectPath());

            if (project != null) {
                AttributeValue packaging = project.getConfig().getAttributes().get(MavenAttributes.PACKAGING);
                if (packaging != null && packaging.getString().equals("pom"))
                    buildMavenProject(project);

//                    if (project.getBaseFolder().getChild("pom.xml") != null) {
//                        Model model = Model.readFrom(project.getBaseFolder().getChild("pom.xml").getVirtualFile());
//                        if ("pom".equals(model.getPackaging())) {
//                            buildMavenProject(project);
//                        }
//                    }
            }
        } catch (ForbiddenException | ServerException | ProjectTypeConstraintException
                | ValueStorageException e) {
            LOG.error(e.getMessage(), e);
        }

    }

    private void buildMavenProject(Project project) {
        String url = apiUrl + "/builder/" + project.getWorkspace() + "/build";
        BuildOptions buildOptions = DtoFactory.getInstance().createDto(BuildOptions.class);
        buildOptions.setSkipTest(true);
        buildOptions.setTargets(Arrays.asList("install"));
        Map<String, String> options = new HashMap<>();
        options.put("-fn", null);
        options.put("-Dmaven.test.skip", "true");
        buildOptions.setOptions(options);
        Pair<String, String> projectParam = Pair.of("project", project.getPath());
        try {
            HttpJsonHelper.request(BuildTaskDescriptor.class, url, "POST", buildOptions, projectParam);
        } catch (IOException | ServerException | UnauthorizedException | ForbiddenException | NotFoundException | ConflictException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
