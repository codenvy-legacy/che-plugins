/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.grunt.runner;

import org.eclipse.che.api.builder.BuildStatus;
import org.eclipse.che.api.builder.dto.BuildTaskDescriptor;
import org.eclipse.che.api.builder.internal.Constants;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.api.core.rest.HttpJsonHelper;
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.api.runner.RunnerException;
import org.eclipse.che.dto.server.DtoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Florent Benoit
 */
public class RemoteBuilderRunnable implements Callable<String> {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteBuilderRunnable.class);

    private static final long CHECK_BUILD_RESULT_DELAY = 2000;


    private BuildTaskDescriptor buildTaskDescriptor;

    public RemoteBuilderRunnable(BuildTaskDescriptor buildTaskDescriptor) {
        this.buildTaskDescriptor = buildTaskDescriptor;
    }


    private static Link getLink(String rel, List<Link> links) {
        for (Link link : links) {
            if (rel.equals(link.getRel())) {
                return link;
            }
        }
        return null;
    }


    @Override
    public String call() throws Exception {
        boolean done = false;
        final Link buildStatusLink = getLink(Constants.LINK_REL_GET_STATUS,
                                             buildTaskDescriptor.getLinks());

        while (!done) {

            if (Thread.currentThread().isInterrupted()) {
                // Expected to get here if task is canceled. Try to cancel related build process.
                tryCancelBuild(buildTaskDescriptor);
                return null;
            }
                try {
                    Thread.sleep(CHECK_BUILD_RESULT_DELAY);
                } catch (InterruptedException e) {
                    // Expected to get here if task is canceled. Try to cancel related build process.
                    tryCancelBuild(buildTaskDescriptor);
                    return null;
                }
            buildTaskDescriptor = HttpJsonHelper.request(BuildTaskDescriptor.class,// create copy of link when pass it outside!!
                                                                         DtoFactory.getInstance().clone(buildStatusLink));

            switch (buildTaskDescriptor.getStatus()) {
                case SUCCESSFUL:
                    final Link downloadLink = getLink(Constants.LINK_REL_DOWNLOAD_RESULT,
                                                      buildTaskDescriptor.getLinks());
                    if (downloadLink == null) {
                        throw new RunnerException("Unable start application. Application build is successful but there " +
                                                  "is no URL for download result of build.");
                    }
                    return downloadLink.getHref();
                case CANCELLED:
                case FAILED:
                    String msg = "Unable start application. Build of application is failed or cancelled.";
                    final Link logLink = getLink(Constants.LINK_REL_VIEW_LOG,
                                                 buildTaskDescriptor.getLinks());
                    if (logLink != null) {
                        msg += (" Build logs: " + logLink.getHref());
                    }
                    throw new RunnerException(msg);
                case IN_PROGRESS:
                case IN_QUEUE:
                    // wait
                    break;
            }
        }
        return null;
    }


    private boolean tryCancelBuild(BuildTaskDescriptor buildDescriptor) {
        final Link cancelLink = getLink(Constants.LINK_REL_CANCEL, buildDescriptor.getLinks());
        if (cancelLink == null) {
            LOG.error("Can't cancel build process since cancel link is not available.");
            return false;
        } else {
            final BuildTaskDescriptor result;
            try {
                result = HttpJsonHelper.request(BuildTaskDescriptor.class,
                                                // create copy of link when pass it outside!!
                                                DtoFactory.getInstance().clone(cancelLink));
            } catch (IOException| ForbiddenException | ConflictException | NotFoundException | ServerException | UnauthorizedException e) {
                return false;
            }
            LOG.debug("Build cancellation result {}", result);
                return result != null && result.getStatus() == BuildStatus.CANCELLED;
        }
    }
}



