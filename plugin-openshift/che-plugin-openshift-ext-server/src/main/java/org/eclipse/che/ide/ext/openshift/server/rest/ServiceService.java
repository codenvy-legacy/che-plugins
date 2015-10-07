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
import com.openshift.restclient.model.IService;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.ext.openshift.server.ClientFactory;
import org.eclipse.che.ide.ext.openshift.shared.dto.Service;

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
@Path("/openshift/{ws-id}/{namespace}/service")
public class ServiceService {
    private final ClientFactory clientFactory;

    @Inject
    public ServiceService(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Service createService(@PathParam("namespace") String namespace,
                                 Service service) throws BadRequestException, UnauthorizedException, ServerException {
        if (service.getKind() == null) {
            service.setKind(ResourceKind.SERVICE);
        }
        if (!ResourceKind.SERVICE.equals(service.getKind())) {
            throw new BadRequestException(service.getKind() + " cannot be handled as a " + ResourceKind.SERVICE);
        }

        final IClient client = clientFactory.createClient();
        final IService openshiftService = toOpenshiftResource(client, service);
        return toDto(Service.class, client.create(openshiftService, namespace));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Service> getServices(@PathParam("namespace") String namespace) throws UnauthorizedException, ServerException {
        List<IService> services = clientFactory.createClient().list(ResourceKind.SERVICE, namespace);
        return services.stream()
                       .map(imageStream -> toDto(Service.class, imageStream))
                       .collect(Collectors.toList());
    }

    @GET
    @Path("/{service}")
    @Produces(MediaType.APPLICATION_JSON)
    public Service getService(@PathParam("namespace") String namespace,
                              @PathParam("service") String service) throws UnauthorizedException, ServerException {

        return toDto(Service.class, clientFactory.createClient().get(ResourceKind.SERVICE, service, namespace));
    }

    @PUT
    @Path("/{service}")
    @Produces(MediaType.APPLICATION_JSON)
    public Service updateService(@PathParam("namespace") String namespace,
                                 @PathParam("service") String serviceName,
                                 Service service) throws ForbiddenException, UnauthorizedException, ServerException {
        if (!serviceName.equals(service.getMetadata().getName())) {
            throw new ForbiddenException("Name of resources can read only access mode");
        }
        final IClient client = clientFactory.createClient();
        return toDto(Service.class, client.update(toOpenshiftResource(client, service)));
    }
}
