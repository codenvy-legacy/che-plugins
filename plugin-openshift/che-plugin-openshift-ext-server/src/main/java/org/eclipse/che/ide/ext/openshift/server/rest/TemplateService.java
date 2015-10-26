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
import com.openshift.restclient.capability.resources.IProjectTemplateProcessing;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.template.ITemplate;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.dto.server.JsonArrayImpl;
import org.eclipse.che.ide.ext.openshift.server.ClientFactory;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;

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
@Path("/openshift/{ws-id}/{namespace}/template")
public class TemplateService {
    private final ClientFactory clientFactory;

    @Inject
    public TemplateService(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Template createTemplate(@PathParam("namespace") String namespace,
                                   Template template) throws BadRequestException, UnauthorizedException, ServerException {
        if (template.getKind() == null) {
            template.setKind(ResourceKind.TEMPLATE);
        }
        if (!ResourceKind.TEMPLATE.equals(template.getKind())) {
            throw new BadRequestException(template.getKind() + " cannot be handled as a " + ResourceKind.TEMPLATE);
        }

        final IClient client = clientFactory.getOpenshiftClient();
        final ITemplate openshiftTemplate = toOpenshiftResource(client, template);
        return toDto(Template.class, client.create(openshiftTemplate, namespace));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Template> getTemplates(@PathParam("namespace") String namespace,
                                       @QueryParam("application") String application) throws UnauthorizedException, ServerException {
        Map<String, String> labels = new HashMap<>();
        if (application != null) {
            labels.put("application", application);
        }
        List<ITemplate> templates = clientFactory.getOpenshiftClient().list(ResourceKind.TEMPLATE, namespace, labels);
        // It is need to wrap list into JsonArray for correct serialization of List<DTO>
        //   into @{link org.eclipse.che.api.core.rest.CodenvyJsonProvider}
        return new JsonArrayImpl<>(templates.stream()
                                            .map(imageStream -> toDto(Template.class, imageStream))
                                            .collect(Collectors.toList()));
    }

    @GET
    @Path("/{template}")
    @Produces(MediaType.APPLICATION_JSON)
    public Template getTemplate(@PathParam("namespace") String namespace,
                                @PathParam("template") String template) throws UnauthorizedException, ServerException {

        return toDto(Template.class, clientFactory.getOpenshiftClient().get(ResourceKind.TEMPLATE, template, namespace));
    }

    @PUT
    @Path("/{template}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Template updateTemplate(@PathParam("namespace") String namespace,
                                   @PathParam("template") String templateName,
                                   Template template) throws ForbiddenException, UnauthorizedException, ServerException {
        if (!templateName.equals(template.getMetadata().getName())) {
            throw new ForbiddenException("Name of resources can read only access mode");
        }
        final IClient client = clientFactory.getOpenshiftClient();
        return toDto(Template.class, client.update(toOpenshiftResource(client, template)));
    }

    @POST
    @Path("/process")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Template processTemplate(@PathParam("namespace") String namespace,
                                    Template template) throws ForbiddenException, UnauthorizedException, ServerException {
        final IClient client = clientFactory.getOpenshiftClient();//TODO investigate why method client.getCapability(ITemplateProcessing.class) returns null
        final IProject project = client.get(ResourceKind.PROJECT, namespace, namespace);
        final IProjectTemplateProcessing capability = project.getCapability(IProjectTemplateProcessing.class);
        final ITemplate processedTemplate = capability.process(toOpenshiftResource(client, template));
        return toDto(Template.class, processedTemplate);
    }
}
