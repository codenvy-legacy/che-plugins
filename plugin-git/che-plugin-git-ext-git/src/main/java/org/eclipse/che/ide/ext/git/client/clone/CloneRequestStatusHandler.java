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
package org.eclipse.che.ide.ext.git.client.clone;

import org.eclipse.che.ide.ext.git.client.GitLocalizationConstant;
import org.eclipse.che.ide.job.Job;
import org.eclipse.che.ide.job.JobChangeEvent;
import org.eclipse.che.ide.job.RequestStatusHandlerBase;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 28, 2011 evgen $
 */
public class CloneRequestStatusHandler extends RequestStatusHandlerBase {
    private String                  remoteUri;
    private GitLocalizationConstant constant;

    /**
     * Create handler.
     *
     * @param projectName
     * @param remoteUri
     * @param eventBus
     * @param constant
     */
    public CloneRequestStatusHandler(String projectName, String remoteUri, EventBus eventBus, GitLocalizationConstant constant) {
        super(projectName, eventBus);
        this.remoteUri = remoteUri;
        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, Job.JobStatus.STARTED);
        job.setStartMessage(constant.cloneStarted(projectName, remoteUri));
        eventBus.fireEvent(new JobChangeEvent(job));
    }

    /** {@inheritDoc} */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, Job.JobStatus.FINISHED);
        job.setFinishMessage(constant.cloneFinished(projectName, remoteUri));
        eventBus.fireEvent(new JobChangeEvent(job));
    }
}