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
package org.eclipse.che.jdt;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.notification.EventSubscriber;
import org.eclipse.che.api.project.server.ProjectCreatedEvent;
import org.eclipse.che.api.vfs.server.observation.CreateEvent;
import org.eclipse.che.api.vfs.server.observation.VirtualFileEvent;
import org.eclipse.che.jdt.core.resources.ResourceChangedEvent;
import org.eclipse.che.jdt.internal.core.JavaModelManager;
import org.eclipse.che.vfs.impl.fs.LocalFSMountStrategy;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class ProjectListeners {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectListeners.class);

    private LocalFSMountStrategy fsMountStrategy;

    private CopyOnWriteArraySet<String> notFullyCreatedProjects = new CopyOnWriteArraySet<>();

    @Inject
    public ProjectListeners(EventService eventService, LocalFSMountStrategy fsMountStrategy) {
        this.fsMountStrategy = fsMountStrategy;
        eventService.subscribe(new VirtualFileEventSubscriber());
        eventService.subscribe(new ProjectCreated());

    }

    private class VirtualFileEventSubscriber implements EventSubscriber<VirtualFileEvent> {

        @Override
        public void onEvent(VirtualFileEvent event) {
            final VirtualFileEvent.ChangeType eventType = event.getType();
            final String eventWorkspace = event.getWorkspaceId();
            final String eventPath = event.getPath();
            if(event.getType() == VirtualFileEvent.ChangeType.CREATED) {
                IPath path = new Path(eventPath);
                if (path.segmentCount() == 0) {
                    notFullyCreatedProjects.add(eventPath);
                    return;
                }
            }
            //ignore all changes when project creating
            if (checkIfInCreationProject(eventPath)) {
                return;
            }
            try {
                JavaModelManager.getJavaModelManager().deltaState.resourceChanged(
                        new ResourceChangedEvent(fsMountStrategy.getMountPath(eventWorkspace), event));
            } catch (Throwable t) {
                //catch all exceptions that may be happened
                LOG.error("Can't update java model", t);
            }
        }
    }

    private boolean checkIfInCreationProject(String path) {
        for (String project : notFullyCreatedProjects) {
            if (path.startsWith(project)) {
                return true;
            }
        }
        return false;
    }

    private class ProjectCreated implements EventSubscriber<ProjectCreatedEvent>{

        @Override
        public void onEvent(ProjectCreatedEvent event) {
            final String eventWorkspace = event.getWorkspaceId();
            final String eventPath = event.getProjectPath();
            notFullyCreatedProjects.remove(eventPath);
            try {
                CreateEvent createEvent = new CreateEvent(eventWorkspace, eventPath, true);
                JavaModelManager.getJavaModelManager().deltaState.resourceChanged(
                        new ResourceChangedEvent(fsMountStrategy.getMountPath(eventWorkspace), createEvent));
            } catch (Throwable t) {
                //catch all exceptions that may be happened
                LOG.error("Can't update java model", t);
            }
        }
    }
}
