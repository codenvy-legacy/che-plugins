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
package org.eclipse.che.ide.extension.machine.client.actions;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.CustomComponentAction;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandConfiguration;
import org.eclipse.che.ide.ui.dropdown.DropDownHeaderWidget;
import org.eclipse.che.ide.ui.dropdown.DropDownListFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.eclipse.che.ide.extension.machine.client.MachineExtension.GROUP_COMMANDS_LIST;

/**
 * Action that allows user to select command from list of all commands.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ChooseCommandAction extends Action implements CustomComponentAction {

    public static final String GROUP_COMMANDS = "CommandsGroup";

    private final DropDownHeaderWidget dropDownHeaderWidget;
    private final DropDownListFactory  configRunnerFactory;
    private final ActionManager        actionManager;

    private List<CommandConfiguration> commands;
    private DefaultActionGroup         projectActions;

    @Inject
    public ChooseCommandAction(MachineLocalizationConstant locale,
                               ActionManager actionManager,
                               DropDownListFactory dropDownListFactory) {
        super(locale.chooseCommandControlTitle(), locale.chooseCommandControlDescription(), null);

        this.dropDownHeaderWidget = dropDownListFactory.createList(GROUP_COMMANDS_LIST);
        this.actionManager = actionManager;
        this.configRunnerFactory = dropDownListFactory;

        commands = new LinkedList<>();

        projectActions = new DefaultActionGroup(GROUP_COMMANDS, false, actionManager);
        actionManager.registerAction(GROUP_COMMANDS, projectActions);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Widget createCustomComponent(Presentation presentation) {
        return (Widget)dropDownHeaderWidget;
    }

    /**
     * Sets command configurations to the list.
     *
     * @param commandConfigurations
     *         collection of command configurations to set
     */
    public void setProjectRunners(@Nonnull Collection<CommandConfiguration> commandConfigurations) {
        final DefaultActionGroup commandsList = (DefaultActionGroup)actionManager.getAction(GROUP_COMMANDS_LIST);

        commands.clear();

        clearRunnerActions(commandsList);
        projectActions.removeAll();

        for (CommandConfiguration configuration : commandConfigurations) {
            projectActions.add(configRunnerFactory.createElement(configuration.getName(),
                                                                 configuration.getType().getIcon(),
                                                                 dropDownHeaderWidget));
        }

        commandsList.addAll(projectActions);

        commands.addAll(commandConfigurations);

        selectDefaultCommand();
    }

    private void clearRunnerActions(@Nonnull DefaultActionGroup runnersList) {
        for (Action action : projectActions.getChildActionsOrStubs()) {
            runnersList.remove(action);
        }
    }

    /** @return selected environment. */
    @Nullable
    public CommandConfiguration selectEnvironment() {
        // Method returns null if list of environments is empty. And app will be run with default runner.
        if (commands.isEmpty()) {
            return null;
        }

        final String selectedCommandName = dropDownHeaderWidget.getSelectedElementName();

        for (CommandConfiguration configuration : commands) {
            if (configuration.getName().equals(selectedCommandName)) {
                return configuration;
            }
        }

        return null;
    }

    /** Selects default environment from system environments if it is defined for current project. */
    public void selectDefaultCommand() {
        if (commands.isEmpty()) {
            setEmptyDefaultRunner();
        } else {
            final CommandConfiguration command = commands.get(0);
            dropDownHeaderWidget.selectElement(command.getType().getIcon(), command.getName());
        }
    }

    /** Clears the selected element in the 'Choose command' menu. */
    public void setEmptyDefaultRunner() {
        dropDownHeaderWidget.selectElement(null, "");
    }
}
