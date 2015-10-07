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
import com.openshift.restclient.model.IImageStream;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.ext.openshift.server.ClientFactory;
import org.eclipse.che.ide.ext.openshift.shared.dto.ImageStream;

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
@Path("/openshift/{ws-id}/{namespace}/imagestream")
public class ImageStreamService {
    private final ClientFactory clientFactory;

    @Inject
    public ImageStreamService(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ImageStream createImageStream(@PathParam("namespace") String namespace,
                                         ImageStream imageStream) throws BadRequestException, UnauthorizedException, ServerException {
        if (imageStream.getKind() == null) {
            imageStream.setKind(ResourceKind.IMAGE_STREAM);
        }
        if (!ResourceKind.IMAGE_STREAM.equals(imageStream.getKind())) {
            throw new BadRequestException(imageStream.getKind() + " cannot be handled as a " + ResourceKind.IMAGE_STREAM);
        }

        final IClient client = clientFactory.createClient();
        final IImageStream openshiftImageStream = toOpenshiftResource(client, imageStream);
        return toDto(ImageStream.class, client.create(openshiftImageStream, namespace));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ImageStream> getImageStreams(@PathParam("namespace") String namespace) throws UnauthorizedException, ServerException {
        List<IImageStream> imageStreams = clientFactory.createClient().list(ResourceKind.IMAGE_STREAM, namespace);
        return imageStreams.stream()
                           .map(imageStream -> toDto(ImageStream.class, imageStream))
                           .collect(Collectors.toList());
    }

    @GET
    @Path("/{imageStream}")
    @Produces(MediaType.APPLICATION_JSON)
    public ImageStream getImageStream(@PathParam("namespace") String namespace,
                                      @PathParam("imageStream") String imageStream) throws UnauthorizedException, ServerException {

        return toDto(ImageStream.class, clientFactory.createClient().get(ResourceKind.IMAGE_STREAM, imageStream, namespace));
    }

    @PUT
    @Path("/{imageStream}")
    @Produces(MediaType.APPLICATION_JSON)
    public ImageStream updateImageStream(@PathParam("namespace") String namespace,
                                         @PathParam("imageStream") String imageStreamName,
                                         ImageStream imageStream) throws ForbiddenException, UnauthorizedException, ServerException {
        if (!imageStreamName.equals(imageStream.getMetadata().getName())) {
            throw new ForbiddenException("Name of resources can read only access mode");
        }
        final IClient client = clientFactory.createClient();
        return toDto(ImageStream.class, client.update(toOpenshiftResource(client, imageStream)));
    }
}
