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
package org.eclipse.che.ide.ext.openshift.server;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IResource;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.dto.server.DtoFactory;

import javax.inject.Inject;

/**
 * @author Sergii Leschenko
 */
public class DtoConverter {
    public static <T> T toDto(Class<T> convertTo, IResource resource) {
        return DtoFactory.getInstance().createDtoFromJson(resource.toJson(true), convertTo);
    }

    public static <DTO, T extends IResource> T toOpenshiftResource(IClient client, DTO dto) throws UnauthorizedException, ServerException {
        return client.getResourceFactory().create(dto.toString());
    }
}
