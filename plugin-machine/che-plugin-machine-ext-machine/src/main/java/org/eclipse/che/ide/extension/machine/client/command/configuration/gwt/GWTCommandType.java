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
package org.eclipse.che.ide.extension.machine.client.command.configuration.gwt;

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
public class GWTCommandType implements CommandType {

    private final GWTPagePresenter page;

    @Inject
    public GWTCommandType(GWTPagePresenter page) {
        this.page = page;
    }

    @Nonnull
    @Override
    public String getId() {
        return "gwt";
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return "GWT";
    }

    @Nonnull
    @Override
    public ConfigurationPage getConfigurationPage() {
        return page;
    }
}
