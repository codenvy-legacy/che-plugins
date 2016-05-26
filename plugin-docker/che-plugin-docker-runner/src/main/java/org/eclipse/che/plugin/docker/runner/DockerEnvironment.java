/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.docker.runner;

import org.eclipse.che.plugin.docker.client.DockerFileException;
import org.eclipse.che.plugin.docker.client.Dockerfile;

import java.io.IOException;

/**
 * @author andrew00x
 */
abstract class DockerEnvironment {
    final String id;

    DockerEnvironment(String id) {
        this.id = id;
    }

    String getId() {
        return id;
    }

    abstract Mapper getMapper() throws IOException;

    abstract Dockerfile getDockerfile() throws DockerFileException;
}
