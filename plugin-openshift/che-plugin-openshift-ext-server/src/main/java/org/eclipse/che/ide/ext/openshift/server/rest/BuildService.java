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
import com.openshift.restclient.capability.resources.IBuildTriggerable;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.IBuildConfig;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.ide.ext.openshift.server.ClientFactory;
import org.eclipse.che.ide.ext.openshift.shared.dto.Build;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

/**
 * @author Sergii Leschenko
 */
@Path("/openshift/{ws-id}/namespace/{namespace}/build")
public class BuildService {
    private final ClientFactory clientFactory;

    @Inject
    public BuildService(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{buildConfig}")
    public Build createBuild(@PathParam("namespace") String namespace,
                             @PathParam("buildConfig") String buildConfig)
            throws BadRequestException, UnauthorizedException, ServerException {
        final IClient client = clientFactory.getOpenshiftClient();
        final IBuildConfig iBuildConfig = client.get(ResourceKind.BUILD_CONFIG, buildConfig, namespace);
        final IBuildTriggerable buildConfigTrigger = iBuildConfig.getCapability(IBuildTriggerable.class);
        return toDto(Build.class, buildConfigTrigger.trigger());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Build> getBuilds(@PathParam("namespace") String namespace,
                                 @QueryParam("application") String application) throws UnauthorizedException, ServerException {
        Map<String, String> labels = new HashMap<>();
        if (application != null) {
            labels.put("application", application);
        }
        List<IBuild> imageStreams = clientFactory.getOpenshiftClient().list(ResourceKind.BUILD, namespace, labels);
        return imageStreams.stream()
                           .map(imageStream -> toDto(Build.class, imageStream))
                           .collect(Collectors.toList());
    }

    @GET
    @Path("/{build}")
    @Produces(MediaType.APPLICATION_JSON)
    public Build getBuild(@PathParam("namespace") String namespace,
                          @PathParam("build") String build) throws UnauthorizedException, ServerException {

        return toDto(Build.class, clientFactory.getOpenshiftClient().get(ResourceKind.BUILD, build, namespace));
    }
}
