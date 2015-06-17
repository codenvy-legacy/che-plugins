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

package org.eclipse.ide.ext.machine;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.project.server.notification.ProjectItemModifiedEvent;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
@Path("machine/project/item")
@Singleton
public class MachineProjectEventService {


    private Set<ProjectEventListener> listeners;

    @Inject
    public MachineProjectEventService(Set<ProjectEventListener> listeners) {
        this.listeners = listeners;
    }

    @POST
    @Path("event")
    @Consumes("application/json")
    public void onEvent(ProjectItemModifiedEvent event) {
        for (ProjectEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
