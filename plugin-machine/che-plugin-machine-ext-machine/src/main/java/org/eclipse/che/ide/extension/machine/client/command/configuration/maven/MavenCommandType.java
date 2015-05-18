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

import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.configuration.ConfigurationFactory;
import org.eclipse.che.ide.extension.machine.client.command.configuration.ConfigurationPage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Maven command type.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MavenCommandType implements CommandType {

    private static final String ID           = "mvn";
    private static final String DISPLAY_NAME = "Maven";

    private final MachineResources          resources;
    private final MavenConfigurationFactory configurationFactory;

    private final Collection<ConfigurationPage<? extends CommandConfiguration>> pages;

    @Inject
    public MavenCommandType(MachineResources resources, MavenPagePresenter page) {
        this.resources = resources;
        configurationFactory = new MavenConfigurationFactory(this);
        pages = new LinkedList<>();
        pages.add(page);
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
        return resources.mavenCommandType();
    }

    @Nonnull
    @Override
    public Collection<ConfigurationPage<? extends CommandConfiguration>> getConfigurationPages() {
        return pages;
    }

    @Nonnull
    @Override
    public ConfigurationFactory<MavenCommandConfiguration> getConfigurationFactory() {
        return configurationFactory;
    }
}
