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
package org.eclipse.che.ide.ext.bitbucket.client.inject;

import com.google.gwt.inject.client.AbstractGinModule;

import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.ext.bitbucket.client.BitbucketClientService;

/**
 * Bindings for the Bitbucket plugin.
 *
 * @author Kevin Pollet
 */
@ExtensionGinModule
public class BitbucketGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(BitbucketClientService.class);
    }
}