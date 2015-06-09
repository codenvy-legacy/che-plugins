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
package org.eclipse.che.ide.extension.maven.client.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.ConfigurationFactory;
import org.eclipse.che.ide.extension.machine.client.command.ConfigurationPage;
import org.eclipse.che.ide.extension.maven.client.MavenResources;
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

    private static final String ID               = "mvn";
    private static final String DISPLAY_NAME     = "Maven";
    private static final String COMMAND_TEMPLATE = "mvn clean install";

    private final MavenResources            resources;
    private final AppContext                appContext;
    private final MavenConfigurationFactory configurationFactory;

    private final Collection<ConfigurationPage<? extends CommandConfiguration>> pages;

    @Inject
    public MavenCommandType(MavenResources resources, MavenCommandPagePresenter page, AppContext appContext) {
        this.resources = resources;
        this.appContext = appContext;
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

    @Nonnull
    @Override
    public String getCommandTemplate() {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            return COMMAND_TEMPLATE + " -f " + currentProject.getRootProject().getName();
        }
        return COMMAND_TEMPLATE;
    }
}
