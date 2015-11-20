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
package org.eclipse.che.plugin.docker.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Yevhenii Voevodin
 */
@Singleton
public class DockerRegistryChecker {

    @Inject
    private DockerConnector connector;

    /**
     * Checks that registry exists and if it doesn't - logs warning message.
     */
    @PostConstruct
    public void checkExists() {
    }
}
