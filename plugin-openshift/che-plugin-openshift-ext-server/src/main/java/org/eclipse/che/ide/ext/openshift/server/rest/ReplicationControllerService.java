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
import com.openshift.restclient.model.IReplicationController;

import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.ext.openshift.server.ClientFactory;
import org.eclipse.che.ide.ext.openshift.shared.dto.ReplicationController;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
@Path("/openshift/{ws-id}/namespace/{namespace}/replicationcontroller")
public class ReplicationControllerService {
    private final ClientFactory clientFactory;

    @Inject
    public ReplicationControllerService(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ReplicationController> getReplicationControllers(@PathParam("namespace") String namespace,
                                                                 @QueryParam("application") String application)
            throws UnauthorizedException, ServerException {
        Map<String, String> labels = new HashMap<>();
        if (application != null) {
            labels.put("application", application);
        }
        List<IReplicationController> controllers = clientFactory.getOpenshiftClient().list(ResourceKind.REPLICATION_CONTROLLER,
                                                                                           namespace, labels);
        return controllers.stream()
                          .map(controller -> toDto(ReplicationController.class, controller))
                          .collect(Collectors.toList());
    }

    @GET
    @Path("/{replicationController}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReplicationController getReplicationController(@PathParam("namespace") String namespace,
                                                          @PathParam("replicationController") String controller)
            throws UnauthorizedException, ServerException {
        return toDto(ReplicationController.class,
                     clientFactory.getOpenshiftClient().get(ResourceKind.REPLICATION_CONTROLLER, controller, namespace));
    }

    @PUT
    @Path("/{replicationController}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReplicationController updateReplicationController(@PathParam("namespace") String namespace,
                                                             @PathParam("replicationController") String controller,
                                                             ReplicationController replicationController)
            throws ForbiddenException, UnauthorizedException, ServerException {
        if (!controller.equals(replicationController.getMetadata().getName())) {
            throw new ForbiddenException("Name of resources can read only access mode");
        }
        final IClient client = clientFactory.getOpenshiftClient();
        return toDto(ReplicationController.class, client.update(toOpenshiftResource(client, replicationController)));
    }
}
