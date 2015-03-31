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
package org.eclipse.che.plugin.gulp.runner;

import org.eclipse.che.api.runner.dto.RunRequest;
import org.eclipse.che.api.runner.internal.RunnerConfiguration;

/**
 * Configuration for using Gulp as AngularJS runner.
 *
 * @author Florent Benoit
 */
public class GulpRunnerConfiguration extends RunnerConfiguration {

    private final int httpPort;

    public GulpRunnerConfiguration(int memory, int httpPort, RunRequest runRequest) {
        super(memory, runRequest);
        this.httpPort = httpPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    @Override
    public String toString() {
        return "GulpRunnerConfiguration{" +
               "memory=" + getMemory() +
               ", links=" + getLinks() +
               ", request=" + getRequest() +
               ", httpPort='" + getHttpPort() +
               '}';
    }
}
