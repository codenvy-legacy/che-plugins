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
package org.eclipse.che.ide.ext.openshift.server.rest;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.project.IProjectRequest;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.ext.openshift.server.ClientFactory;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.ext.openshift.shared.dto.ProjectRequest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

import static org.eclipse.che.ide.ext.openshift.server.DtoConverter.toDto;
import static org.eclipse.che.ide.ext.openshift.server.DtoConverter.toOpenshiftResource;

/**
 * @author Sergii Leschenko
 */
@Path("/openshift/{ws_id}/project")
public class ProjectService {
    private final ClientFactory clientFactory;

    @Inject
    public ProjectService(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Project createProject(ProjectRequest project) throws BadRequestException, UnauthorizedException, ServerException {
        if (project.getKind() == null) {
            project.setKind(ResourceKind.PROJECT_REQUEST);
        }
        if (!ResourceKind.PROJECT_REQUEST.equals(project.getKind())) {
            throw new BadRequestException(project.getKind() + " cannot be handled as a " + ResourceKind.PROJECT_REQUEST);
        }

        final IClient client = clientFactory.getClient();
        final IProjectRequest openshiftProject = toOpenshiftResource(client, project);
        return toDto(Project.class, client.create(openshiftProject));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Project> getProjects() throws UnauthorizedException, ServerException {
        List<IProject> projects = clientFactory.getClient().list(ResourceKind.PROJECT);
        return projects.stream()
                       .map(imageStream -> toDto(Project.class, imageStream))
                       .collect(Collectors.toList());
    }

    @GET
    @Path("/{project}")
    @Produces(MediaType.APPLICATION_JSON)
    public Project getProject(@PathParam("project") String project) throws UnauthorizedException, ServerException {

        return toDto(Project.class, clientFactory.getClient().get(ResourceKind.PROJECT, project, project));
    }

    @PUT
    @Path("/{project}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Project updateProject(@PathParam("project") String projectName,
                                 Project project) throws ForbiddenException, UnauthorizedException, ServerException {
        if (!projectName.equals(project.getMetadata().getName())) {
            throw new ForbiddenException("Name of resources can read only access mode");
        }
        final IClient client = clientFactory.getClient();
        return toDto(Project.class, client.update(toOpenshiftResource(client, project)));
    }
}
