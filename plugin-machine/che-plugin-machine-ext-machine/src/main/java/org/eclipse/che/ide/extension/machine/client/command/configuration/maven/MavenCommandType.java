/**
 * ****************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors:
 * Codenvy, S.A. - initial API and implementation
 * *****************************************************************************
 */
package org.eclipse.che.ide.extension.machine.client.command.configuration.maven;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.configuration.ConfigurationPage;

import javax.annotation.Nonnull;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MavenCommandType implements CommandType {

    private final MavenPagePresenter page;

    @Inject
    public MavenCommandType(MavenPagePresenter page) {
        this.page = page;
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
}
