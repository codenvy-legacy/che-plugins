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
package org.eclipse.che.ide.extension.machine.client.command.configuration.maven;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.ConfigurationFactory;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.ConfigurationPage;

import javax.annotation.Nonnull;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MavenCommandType implements CommandType {

    private final MavenPagePresenter        page;
    private final MavenConfigurationFactory configurationFactory;

    @Inject
    public MavenCommandType(MavenPagePresenter page) {
        this.page = page;
        this.configurationFactory = new MavenConfigurationFactory(this);
    }

    @Nonnull
    @Override
    public String getId() {
        return "maven";
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return "Maven";
    }

    @Nonnull
    @Override
    public ConfigurationPage getConfigurationPage() {
        return page;
    }

    @Nonnull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return configurationFactory;
    }
}
