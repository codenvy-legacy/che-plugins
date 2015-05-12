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

import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandManager;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandType;
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

    private final CommandManager         commandManager;
    private final EditConfigurationsView view;

    @Inject
    protected EditConfigurationsPresenter(EditConfigurationsView view, CommandManager commandManager) {
        this.view = view;
        this.commandManager = commandManager;
        this.view.setDelegate(this);
    }

    @Override
    public void onCloseClicked() {
        view.close();
    }

    @Override
    public void onNameChanged(String name) {
        final CommandConfiguration selectedConfiguration = view.getSelectedConfiguration();
        if (selectedConfiguration != null) {
            selectedConfiguration.setName(name);
        }
    }

    @Override
    public void onCommandTypeSelected(CommandType type) {
        view.setAddButtonState(true);
        view.setRemoveButtonState(false);
        view.setConfigurationName("");
        view.clearCommandConfigurationsDisplayContainer();
    }

    @Override
    public void onConfigurationSelected(CommandConfiguration configuration) {
        view.setAddButtonState(true);
        view.setRemoveButtonState(true);
        view.setConfigurationName(configuration.getName());

        final ConfigurationPage configurationPage = configuration.getType().getConfigurationPage();
        configurationPage.reset(configuration);
        configurationPage.go(view.getCommandConfigurationsDisplayContainer());
    }

    @Override
    public void onAddClicked() {
        final CommandType selectedType = view.getSelectedCommandType();
        if (selectedType != null) {
            commandManager.createConfiguration(selectedType);
            refreshView();
        }
    }

    @Override
    public void onDeleteClicked() {
        final CommandConfiguration selectedConfiguration = view.getSelectedConfiguration();
        if (selectedConfiguration != null) {
            commandManager.removeConfiguration(selectedConfiguration);
            refreshView();
        }
    }

    /** Show dialog. */
    public void show() {
        refreshView();
        view.show();
    }

    private void refreshView() {
        final Set<CommandType> commandTypes = commandManager.getCommandTypes();
        final Set<CommandConfiguration> commandConfigurations = commandManager.getCommandConfigurations();

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
