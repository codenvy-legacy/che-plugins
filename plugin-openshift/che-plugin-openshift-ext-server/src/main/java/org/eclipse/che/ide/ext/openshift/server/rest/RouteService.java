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
import com.openshift.restclient.model.route.IRoute;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.ext.openshift.server.ClientFactory;
import org.eclipse.che.ide.ext.openshift.shared.dto.Route;

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
@Path("/openshift/{ws-id}/namespace/{namespace}/route")
public class RouteService {
    private final ClientFactory clientFactory;

    @Inject
    public RouteService(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Route createRoute(@PathParam("namespace") String namespace,
                             Route route) throws BadRequestException, UnauthorizedException, ServerException {
        if (route.getKind() == null) {
            route.setKind(ResourceKind.ROUTE);
        }
        if (!ResourceKind.ROUTE.equals(route.getKind())) {
            throw new BadRequestException(route.getKind() + " cannot be handled as a " + ResourceKind.ROUTE);
        }

        final IClient client = clientFactory.getOpenshiftClient();
        final IRoute openshiftRoute = toOpenshiftResource(client, route);
        return toDto(Route.class, client.create(openshiftRoute, namespace));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Route> getRoutes(@PathParam("namespace") String namespace,
                                 @QueryParam("application") String application) throws UnauthorizedException, ServerException {
        Map<String, String> labels = new HashMap<>();
        if (application != null) {
            labels.put("application", application);
        }
        List<IRoute> routes = clientFactory.getOpenshiftClient().list(ResourceKind.ROUTE, namespace, labels);
        return routes.stream()
                     .map(imageStream -> toDto(Route.class, imageStream))
                     .collect(Collectors.toList());
    }

    @GET
    @Path("/{route}")
    @Produces(MediaType.APPLICATION_JSON)
    public Route getRoute(@PathParam("namespace") String namespace,
                          @PathParam("route") String route) throws UnauthorizedException, ServerException {

        return toDto(Route.class, clientFactory.getOpenshiftClient().get(ResourceKind.ROUTE, route, namespace));
    }

    @PUT
    @Path("/{route}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Route updateRoute(@PathParam("namespace") String namespace,
                             @PathParam("route") String routeName,
                             Route route) throws ForbiddenException, UnauthorizedException, ServerException {
        if (!routeName.equals(route.getMetadata().getName())) {
            throw new ForbiddenException("Name of resources can read only access mode");
        }
        final IClient client = clientFactory.getOpenshiftClient();
        return toDto(Route.class, client.update(toOpenshiftResource(client, route)));
    }
}
