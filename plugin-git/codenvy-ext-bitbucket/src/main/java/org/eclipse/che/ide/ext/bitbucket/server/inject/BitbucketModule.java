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
package org.eclipse.che.ide.ext.bitbucket.server.inject;

import org.eclipse.che.api.project.server.ProjectImporter;
import org.eclipse.che.ide.ext.bitbucket.server.Bitbucket;
import org.eclipse.che.ide.ext.bitbucket.server.BitbucketKeyUploader;
import org.eclipse.che.ide.ext.bitbucket.server.BitbucketProjectImporter;
import org.eclipse.che.ide.ext.bitbucket.server.rest.BitbucketExceptionMapper;
import org.eclipse.che.ide.ext.bitbucket.server.rest.BitbucketService;
import org.eclipse.che.ide.ext.git.server.nativegit.SshKeyUploader;
import org.eclipse.che.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * The module that contains configuration of the server side part of the Bitbucket extension.
 *
 * @author Kevin Pollet
 */
@DynaModule
public class BitbucketModule extends AbstractModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(Bitbucket.class);
        bind(BitbucketService.class);
        bind(BitbucketExceptionMapper.class);

        Multibinder.newSetBinder(binder(), ProjectImporter.class).addBinding().to(BitbucketProjectImporter.class);
        Multibinder.newSetBinder(binder(), SshKeyUploader.class).addBinding().to(BitbucketKeyUploader.class);
    }
}
