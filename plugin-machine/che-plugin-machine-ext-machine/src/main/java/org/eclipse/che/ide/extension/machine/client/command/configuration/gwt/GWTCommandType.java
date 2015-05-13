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
package org.eclipse.che.ide.extension.machine.client.command.configuration.gwt;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.ConfigurationFactory;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.ConfigurationPage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;

/**
 * GWT command type.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class GWTCommandType implements CommandType {

    private static final String ID           = "mvn";
    private static final String DISPLAY_NAME = "Maven";

    private final GWTPagePresenter        page;
    private final MachineResources        resources;
    private final GWTConfigurationFactory configurationFactory;

    @Inject
    public GWTCommandType(GWTPagePresenter page, MachineResources resources) {
        this.page = page;
        this.resources = resources;
        this.configurationFactory = new GWTConfigurationFactory(this);
    }

    @Nonnull
    @Override
    public String getId() {
        return ID;
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Nonnull
    @Override
    public SVGResource getIcon() {
        return resources.gwtCommandType();
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
