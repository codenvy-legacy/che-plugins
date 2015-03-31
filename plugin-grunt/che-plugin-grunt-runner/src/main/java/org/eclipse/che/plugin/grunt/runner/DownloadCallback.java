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

import org.eclipse.che.api.core.util.DownloadPlugin;
import org.eclipse.che.api.runner.internal.DeploymentSources;

import java.io.File;
import java.io.IOException;

/**
 * @author Florent Benoit
 */
public class DownloadCallback implements DownloadPlugin.Callback {
    private DeploymentSources resultHolder;

    private IOException errorHolder;


    @Override
    public void done(File downloaded) {
        resultHolder = new DeploymentSources(downloaded);
    }

    @Override
    public void error(IOException e) {
        errorHolder = e;
    }

    public DeploymentSources getResultHolder() {
        return resultHolder;
    }

    public IOException getErrorHolder() {
        return errorHolder;
    }

}
