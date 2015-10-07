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
import com.openshift.restclient.model.IDeploymentConfig;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.ext.openshift.server.ClientFactory;
import org.eclipse.che.ide.ext.openshift.shared.dto.DeploymentConfig;

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
@Path("/openshift/{ws-id}/{namespace}/deploymentconfig")
public class DeploymentConfigService {
    private final ClientFactory clientFactory;

    @Inject
    public DeploymentConfigService(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DeploymentConfig createDeploymentConfig(@PathParam("namespace") String namespace,
                                                   DeploymentConfig deploymentConfig)
            throws BadRequestException, UnauthorizedException, ServerException {
        if (deploymentConfig.getKind() == null) {
            deploymentConfig.setKind(ResourceKind.DEPLOYMENT_CONFIG);
        }
        if (!ResourceKind.DEPLOYMENT_CONFIG.equals(deploymentConfig.getKind())) {
            throw new BadRequestException(deploymentConfig.getKind() + " cannot be handled as a " + ResourceKind.DEPLOYMENT_CONFIG);
        }

        final IClient client = clientFactory.createClient();
        final IDeploymentConfig openshiftDeploymentConfig = toOpenshiftResource(client, deploymentConfig);
        return toDto(DeploymentConfig.class, client.create(openshiftDeploymentConfig, namespace));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<DeploymentConfig> getDeploymentConfigs(@PathParam("namespace") String namespace)
            throws UnauthorizedException, ServerException {
        List<IDeploymentConfig> deploymentConfigs = clientFactory.createClient().list(ResourceKind.DEPLOYMENT_CONFIG, namespace);
        return deploymentConfigs.stream()
                                .map(imageStream -> toDto(DeploymentConfig.class, imageStream))
                                .collect(Collectors.toList());
    }

    @GET
    @Path("/{deploymentConfig}")
    @Produces(MediaType.APPLICATION_JSON)
    public DeploymentConfig getDeploymentConfig(@PathParam("namespace") String namespace,
                                                @PathParam("deploymentConfig") String deploymentConfig)
            throws UnauthorizedException, ServerException {

        return toDto(DeploymentConfig.class, clientFactory.createClient().get(ResourceKind.DEPLOYMENT_CONFIG, deploymentConfig, namespace));
    }

    @PUT
    @Path("/{deploymentConfig}")
    @Produces(MediaType.APPLICATION_JSON)
    public DeploymentConfig updateDeploymentConfig(@PathParam("namespace") String namespace,
                                                   @PathParam("deploymentConfig") String deploymentConfigName,
                                                   DeploymentConfig deploymentConfig)
            throws ForbiddenException, UnauthorizedException, ServerException {
        if (!deploymentConfigName.equals(deploymentConfig.getMetadata().getName())) {
            throw new ForbiddenException("Name of resources can read only access mode");
        }
        final IClient client = clientFactory.createClient();
        return toDto(DeploymentConfig.class, client.update(toOpenshiftResource(client, deploymentConfig)));
    }
}
