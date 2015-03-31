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
package org.eclipse.che.ide.ext.github.server.rest;

import org.eclipse.che.ide.ext.github.server.GitHubException;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Oksana Vereshchaka
 */
@Singleton
@Provider
public class GitHubExceptionMapper implements ExceptionMapper<GitHubException> {

    /** @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable) */
    @Override
    public Response toResponse(GitHubException e) {
        return Response.status(e.getResponseStatus()).header("JAXRS-Body-Provided", "Error-Message")
                       .entity(e.getMessage()).type(e.getContentType()).build();
    }

}
