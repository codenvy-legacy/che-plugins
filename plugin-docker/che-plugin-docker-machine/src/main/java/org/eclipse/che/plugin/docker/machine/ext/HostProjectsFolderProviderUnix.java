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
package org.eclipse.che.plugin.docker.machine.ext;


import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

/**
 * Provide path to the project folder on hosted machine under *nix
 * managed by vfs.local.fs_root_dir property
 *
 *  @author Vitalii Parfonov
 */
public class HostProjectsFolderProviderUnix implements Provider<String> {

    private final String projectsFolder;

    @Inject
    public HostProjectsFolderProviderUnix(@Named("vfs.local.fs_root_dir") String projectsFolder) {
        this.projectsFolder = projectsFolder;
    }

    @Override
    public String get() {
        return projectsFolder;
    }
}
