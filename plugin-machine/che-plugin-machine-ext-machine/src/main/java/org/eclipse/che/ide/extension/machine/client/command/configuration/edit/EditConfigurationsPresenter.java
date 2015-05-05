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
import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandConfigurationManager;
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

    private final CommandConfigurationManager commandConfigurationManager;
    private final EditConfigurationsView      view;
    private       CommandConfiguration        selectedConfiguration;

    @Inject
    protected EditConfigurationsPresenter(EditConfigurationsView view, CommandConfigurationManager commandConfigurationManager) {
        this.view = view;
        this.commandConfigurationManager = commandConfigurationManager;
        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onNameChanged(String name) {
        selectedConfiguration.setName(name);
    }

    /** {@inheritDoc} */
    @Override
    public void onConfigurationSelected(CommandConfiguration configuration) {
        selectedConfiguration = configuration;

        view.setConfigurationName(configuration.getName());

        final ConfigurationPage configurationPage = configuration.getType().getConfigurationPage();
        configurationPage.reset(configuration);
        configurationPage.go(view.getContentPanel());
    }

    /** {@inheritDoc} */
    @Override
    public void onAddClicked() {
        commandConfigurationManager.createConfiguration(selectedConfiguration.getType());
        refreshView();
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        commandConfigurationManager.removeConfiguration(selectedConfiguration);
        refreshView();
    }

    /** {@inheritDoc} */
    @Override
    public void onExecuteClicked() {
        commandConfigurationManager.execute(selectedConfiguration);
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** Show dialog. */
    public void show() {
        refreshView();
        view.show();
    }

    private void refreshView() {
        final Set<CommandType> commandTypes = commandConfigurationManager.getCommandTypes();
        final Set<CommandConfiguration> commandConfigurations = commandConfigurationManager.getCommandConfigurations();

        final Map<CommandType, Set<CommandConfiguration>> commands = new HashMap<>();

        for (CommandType commandType : commandTypes) {
            Set<CommandConfiguration> configurations = new HashSet<>();
            commands.put(commandType, configurations);
            for (CommandConfiguration configuration : commandConfigurations) {
                if (commandType.getId().equals(configuration.getType().getId())) {
                    configurations.add(configuration);
                }
            }
        }

        view.setCommandConfigurations(commands);
    }
}
