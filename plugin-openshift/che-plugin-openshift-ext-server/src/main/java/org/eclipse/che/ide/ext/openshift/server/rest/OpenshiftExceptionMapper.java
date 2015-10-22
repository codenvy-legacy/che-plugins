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

import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.model.IStatus;

import org.eclipse.che.api.core.rest.shared.dto.ServiceError;
import org.eclipse.che.dto.server.DtoFactory;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Sergii Leschenko
 */
@Provider
@Singleton
public class OpenshiftExceptionMapper implements ExceptionMapper<OpenShiftException> {
    @Override
    public Response toResponse(OpenShiftException exception) {
        final IStatus status = exception.getStatus();
        String message;
        if (status == null) {
            message = exception.getLocalizedMessage();
        } else {
            message = status.getMessage();
        }
        //TODO Add bindings to another status code in accordance to kind of OpenShiftException
        return Response.status(Response.Status.CONFLICT)
                       .entity(DtoFactory.newDto(ServiceError.class).withMessage(message))
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
