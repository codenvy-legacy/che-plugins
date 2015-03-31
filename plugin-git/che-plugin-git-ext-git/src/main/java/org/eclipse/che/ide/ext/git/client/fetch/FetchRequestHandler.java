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
package org.eclipse.che.ide.ext.git.client.fetch;

import org.eclipse.che.ide.ext.git.client.GitLocalizationConstant;
import org.eclipse.che.ide.job.Job;
import org.eclipse.che.ide.job.JobChangeEvent;
import org.eclipse.che.ide.job.RequestStatusHandlerBase;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 31, 2011 evgen $
 */
public class FetchRequestHandler extends RequestStatusHandlerBase {
    private String localBranch;
    private String remoteBranch = "";
    private GitLocalizationConstant constant;

    /**
     * Create handler.
     *
     * @param projectName
     * @param refSpec
     * @param eventBus
     * @param constant
     */
    public FetchRequestHandler(@Nonnull String projectName, @Nonnull List<String> refSpec, @Nonnull EventBus eventBus,
                               @Nonnull GitLocalizationConstant constant) {
        super(projectName, eventBus);
        if (refSpec.size() > 0){
            String[] split = refSpec.get(0).split(":");
            this.localBranch = split[0];
            if (split.length < 2) {
                this.remoteBranch = split[1];
            }
        }
        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, Job.JobStatus.STARTED);
        job.setStartMessage(constant.fetchStarted(projectName, localBranch, remoteBranch));
        eventBus.fireEvent(new JobChangeEvent(job));
    }

    /** {@inheritDoc} */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, Job.JobStatus.FINISHED);
        job.setFinishMessage(constant.fetchFinished(projectName, localBranch, remoteBranch));
        eventBus.fireEvent(new JobChangeEvent(job));
    }
}