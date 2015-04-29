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
package org.eclipse.che.ide.extension.machine.client.command.configuration.edit;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.configuration.ConfigurationManager;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.ConfigurationPage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Presenter for managing command configurations.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class EditConfigurationsPresenter implements EditConfigurationsView.ActionDelegate {

    private final ConfigurationManager   configurationManager;
    private final EditConfigurationsView view;

    @Inject
    protected EditConfigurationsPresenter(EditConfigurationsView view, ConfigurationManager configurationManager) {
        this.view = view;
        this.configurationManager = configurationManager;
        this.view.setDelegate(this);
//        for (ConfigurationPage preference : preferences) {
//            preference.setUpdateDelegate(this);
//        }
    }

    /** {@inheritDoc} */
    @Override
    public void onConfigurationSelected(CommandConfiguration configuration) {
        final ConfigurationPage configurationPage = configuration.getType().getConfigurationPage();
        configurationPage.reset(configuration);
        configurationPage.go(view.getContentPanel());
    }

    /** Shows dialog. */
    public void show() {
        final Set<CommandType> commandTypes = configurationManager.getCommandTypes();
        final Set<CommandConfiguration> commandConfigurations = configurationManager.getCommandConfigurations();

        final Map<CommandType, Set<CommandConfiguration>> commandsMap = new HashMap<>();

        for (CommandType commandType : commandTypes) {
            Set<CommandConfiguration> configurations = new HashSet<>();
            commandsMap.put(commandType, configurations);
            for (CommandConfiguration configuration : commandConfigurations) {
                if (commandType.getId().equals(configuration.getType().getId())) {
                    configurations.add(configuration);
                }
            }
        }
        view.setCommandTypes(commandsMap);

        view.show();
        view.enableSaveButton(false);
//        view.selectPreference(commandsMap.entrySet().iterator().next().getValue().iterator().next());
    }

    /** {@inheritDoc} */
    @Override
    public void onSaveClicked() {
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }
}
