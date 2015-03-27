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
package org.eclipse.che.ide.ext.bitbucket.server;

import org.eclipse.che.ide.ext.git.server.GitConnectionFactory;
import org.eclipse.che.ide.ext.git.server.GitProjectImporter;
import org.eclipse.che.vfs.impl.fs.LocalPathResolver;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * {@link BitbucketProjectImporter} implementation for Bitbucket.
 *
 * @author Kevin Pollet
 */
@Singleton
public class BitbucketProjectImporter extends GitProjectImporter {
    @Inject
    public BitbucketProjectImporter(@Nonnull final GitConnectionFactory gitConnectionFactory,
                                    @Nonnull final LocalPathResolver localPathResolver) {

        super(gitConnectionFactory, localPathResolver);
    }

    @Override
    public String getId() {
        return "bitbucket";
    }

    @Override
    public String getDescription() {
        return "Import project from bitbucket.";
    }
}
