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
package org.eclipse.che.ide.ext.git.server;

import org.eclipse.che.api.project.server.type.TransientMixin;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Vitaly Parfonov
 */
@Singleton
public class GitProjectType extends TransientMixin {

    public static String VCS_PROVIDER_NAME = "vcs.provider.name";

    @Inject
    public GitProjectType(GitValueProviderFactory gitRepositoryValueProviderFactory) {
        super("git", "git");
        addVariableDefinition(VCS_PROVIDER_NAME, "Is this git repo or not?", false,
                              gitRepositoryValueProviderFactory);
    }
}
