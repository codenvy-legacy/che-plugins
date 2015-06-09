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
package org.eclipse.che.ide.extension.machine.client;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.event.ProjectActionEvent;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.extension.machine.client.actions.EditCommandsAction;
import org.eclipse.che.ide.extension.machine.client.actions.ExecuteArbitraryCommandAction;
import org.eclipse.che.ide.extension.machine.client.actions.ExecuteSelectedCommandAction;
import org.eclipse.che.ide.extension.machine.client.actions.MachinePerspectiveAction;
import org.eclipse.che.ide.extension.machine.client.actions.SelectCommandAction;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
import org.eclipse.che.ide.extension.machine.client.machine.console.ClearConsoleAction;
import org.eclipse.che.ide.extension.machine.client.machine.console.MachineConsoleToolbar;
import org.eclipse.che.ide.extension.machine.client.outputspanel.OutputsContainerPresenter;
import org.eclipse.che.ide.ui.toolbar.ToolbarPresenter;

import static org.eclipse.che.ide.api.action.IdeActions.GROUP_CODE;
import static org.eclipse.che.ide.api.action.IdeActions.GROUP_MAIN_MENU;
import static org.eclipse.che.ide.api.action.IdeActions.GROUP_RIGHT_TOOLBAR;
import static org.eclipse.che.ide.api.constraints.Anchor.AFTER;
import static org.eclipse.che.ide.api.constraints.Constraints.FIRST;

/**
 * Machine extension entry point.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
@Singleton
@Extension(title = "Machine", version = "1.0.0")
public class MachineExtension {

    public static final String GROUP_MACHINE_CONSOLE_TOOLBAR = "MachineConsoleToolbar";
    public static final String GROUP_MACHINE_TOOLBAR         = "MachineGroupToolbar";
    public static final String GROUP_COMMANDS_LIST           = "CommandsListGroup";

    @Inject
    public MachineExtension(MachineResources machineResources, EventBus eventBus, MachineManager machineManager) {
        machineResources.getCss().ensureInjected();
        eventBus.addHandler(ProjectActionEvent.TYPE, machineManager);
    }

    @Inject
    private void prepareActions(MachineLocalizationConstant localizationConstant,
                                ActionManager actionManager,
                                ExecuteArbitraryCommandAction executeArbitraryCommandAction,
                                ExecuteSelectedCommandAction executeSelectedCommandAction,
                                SelectCommandAction selectCommandAction,
                                EditCommandsAction editCommandsAction,
                                MachinePerspectiveAction machinePerspectiveAction) {
        final DefaultActionGroup mainMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_MENU);
        final DefaultActionGroup runMenu = new DefaultActionGroup(localizationConstant.mainMenuRunName(), true, actionManager);

        // register actions
        actionManager.registerAction("run", runMenu);
        actionManager.registerAction("executeArbitraryCommand", executeArbitraryCommandAction);
        actionManager.registerAction("editCommands", editCommandsAction);
        actionManager.registerAction("selectCommandAction", selectCommandAction);
        actionManager.registerAction("executeSelectedCommand", executeSelectedCommandAction);

        // add actions in main menu
        mainMenu.add(runMenu, new Constraints(AFTER, GROUP_CODE));
        runMenu.add(executeArbitraryCommandAction);
        runMenu.add(editCommandsAction);

        // add actions on right toolbar
        final DefaultActionGroup rightToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RIGHT_TOOLBAR);
        final DefaultActionGroup machineToolbarGroup = new DefaultActionGroup(GROUP_MACHINE_TOOLBAR, false, actionManager);
        actionManager.registerAction(GROUP_MACHINE_TOOLBAR, machineToolbarGroup);
        rightToolbarGroup.add(machinePerspectiveAction);
        rightToolbarGroup.add(machineToolbarGroup);
        machineToolbarGroup.add(selectCommandAction);
        machineToolbarGroup.add(executeSelectedCommandAction);

        // add group for command list
        final DefaultActionGroup commandList = new DefaultActionGroup(GROUP_COMMANDS_LIST, false, actionManager);
        actionManager.registerAction(GROUP_COMMANDS_LIST, commandList);
        commandList.add(editCommandsAction, FIRST);
        commandList.addSeparator();
    }

    @Inject
    private void setUpMachineConsole(ActionManager actionManager,
                                     ClearConsoleAction clearConsoleAction,
                                     @MachineConsoleToolbar ToolbarPresenter machineConsoleToolbar) {

        // add toolbar to Machine console
        final DefaultActionGroup consoleToolbarActionGroup = new DefaultActionGroup(GROUP_MACHINE_CONSOLE_TOOLBAR, false, actionManager);
        consoleToolbarActionGroup.add(clearConsoleAction);
        consoleToolbarActionGroup.addSeparator();
        machineConsoleToolbar.bindMainGroup(consoleToolbarActionGroup);
    }

    @Inject
    private void setUpOutputsConsole(WorkspaceAgent workspaceAgent,
                                     OutputsContainerPresenter outputsContainerPresenter) {
        workspaceAgent.openPart(outputsContainerPresenter, PartStackType.INFORMATION);
    }
}
