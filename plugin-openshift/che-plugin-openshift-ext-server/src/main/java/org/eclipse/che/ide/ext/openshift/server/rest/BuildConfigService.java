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
import com.openshift.restclient.model.IBuildConfig;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.ext.openshift.server.ClientFactory;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.eclipse.che.ide.ext.openshift.server.DtoConverter.toDto;
import static org.eclipse.che.ide.ext.openshift.server.DtoConverter.toOpenshiftResource;

/**
 * @author Sergii Leschenko
 */
@Path("/openshift/{ws-id}/namespace/{namespace}/buildconfig")
public class BuildConfigService {
    private final ClientFactory clientFactory;

    @Inject
    public BuildConfigService(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BuildConfig createBuildConfig(@PathParam("namespace") String namespace,
                                         BuildConfig buildConfig) throws BadRequestException, UnauthorizedException, ServerException {
        if (buildConfig.getKind() == null) {
            buildConfig.setKind(ResourceKind.BUILD_CONFIG);
        }
        if (!ResourceKind.BUILD_CONFIG.equals(buildConfig.getKind())) {
            throw new BadRequestException(buildConfig.getKind() + " cannot be handled as a " + ResourceKind.BUILD_CONFIG);
        }

        final IClient client = clientFactory.getOpenshiftClient();
        final IBuildConfig openshiftBuildConfig = toOpenshiftResource(client, buildConfig);
        return toDto(BuildConfig.class, client.create(openshiftBuildConfig, namespace));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<BuildConfig> getBuildConfigs(@PathParam("namespace") String namespace,
                                             @QueryParam("application") String application) throws UnauthorizedException, ServerException {
        Map<String, String> labels = new HashMap<>();
        if (application != null) {
            labels.put("application", application);
        }
        List<IBuildConfig> buildConfigs = clientFactory.getOpenshiftClient().list(ResourceKind.BUILD_CONFIG, namespace, labels);
        return buildConfigs.stream()
                           .map(imageStream -> toDto(BuildConfig.class, imageStream))
                           .collect(Collectors.toList());
    }

    @GET
    @Path("/{buildConfig}")
    @Produces(MediaType.APPLICATION_JSON)
    public BuildConfig getBuildConfig(@PathParam("namespace") String namespace,
                                      @PathParam("buildConfig") String buildConfig) throws UnauthorizedException, ServerException {

        return toDto(BuildConfig.class, clientFactory.getOpenshiftClient().get(ResourceKind.BUILD_CONFIG, buildConfig, namespace));
    }

    @PUT
    @Path("/{buildConfig}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BuildConfig updateBuildConfig(@PathParam("namespace") String namespace,
                                         @PathParam("buildConfig") String buildConfigName,
                                         BuildConfig buildConfig) throws ForbiddenException, UnauthorizedException, ServerException {
        if (!buildConfigName.equals(buildConfig.getMetadata().getName())) {
            throw new ForbiddenException("Name of resources can read only access mode");
        }
        final IClient client = clientFactory.getOpenshiftClient();
        return toDto(BuildConfig.class, client.update(toOpenshiftResource(client, buildConfig)));
    }
}
