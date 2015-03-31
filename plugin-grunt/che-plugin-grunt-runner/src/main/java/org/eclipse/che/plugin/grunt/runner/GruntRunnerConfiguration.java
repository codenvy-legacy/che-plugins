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

import org.eclipse.che.api.runner.dto.RunRequest;
import org.eclipse.che.api.runner.internal.RunnerConfiguration;

/**
 * Configuration for using Grunt as AngularJS runner.
 *
 * @author Florent Benoit
 */
public class GruntRunnerConfiguration extends RunnerConfiguration {

    private final int httpPort;
    private final int liveReloadPort;

    public GruntRunnerConfiguration(int memory, int httpPort, int liveReloadPort, RunRequest runRequest) {
        super(memory, runRequest);
        this.httpPort = httpPort;
        this.liveReloadPort = liveReloadPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public int getLiveReloadPort() {
        return liveReloadPort;
    }

    @Override
    public String toString() {
        return "GruntRunnerConfiguration{" +
               "memory=" + getMemory() +
               ", links=" + getLinks() +
               ", request=" + getRequest() +
               ", httpPort='" + getHttpPort() +
               ", liveReloadPort='" + getHttpPort() +
               '}';
    }
}
