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

import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.extension.machine.client.actions.BindProjectToMachineAction;
import org.eclipse.che.ide.extension.machine.client.actions.StopMachineAction;
import org.eclipse.che.ide.extension.machine.client.console.ClearConsoleAction;
import org.eclipse.che.ide.extension.machine.client.console.MachineConsolePresenter;
import org.eclipse.che.ide.extension.machine.client.console.MachineConsoleToolbar;
import org.eclipse.che.ide.ui.toolbar.ToolbarPresenter;

import static org.eclipse.che.ide.api.action.IdeActions.GROUP_CODE;
import static org.eclipse.che.ide.api.action.IdeActions.GROUP_MAIN_MENU;
import static org.eclipse.che.ide.api.constraints.Anchor.AFTER;

/**
 * Machine extension entry point.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Machine", version = "1.0.0")
public class MachineExtension {

    public static final String MACHINES_GROUP_MAIN_MENU      = "Machines";
    public static final String GROUP_MACHINE_CONSOLE_TOOLBAR = "MachineConsoleToolbar";

    @Inject
    public MachineExtension(MachineResources machineResources) {
        machineResources.machine().ensureInjected();
    }

    @Inject
    private void prepareActions(ActionManager actionManager,
                                StopMachineAction stopMachineAction,
                                BindProjectToMachineAction bindProjectToMachineAction) {
        final DefaultActionGroup mainMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_MENU);

        final DefaultActionGroup machinesMenu = new DefaultActionGroup(MACHINES_GROUP_MAIN_MENU, true, actionManager);
        actionManager.registerAction("machines", machinesMenu);
        final Constraints afterCodeConstraint = new Constraints(AFTER, GROUP_CODE);
        mainMenu.add(machinesMenu, afterCodeConstraint);

        // register actions
        actionManager.registerAction("stopMachine", stopMachineAction);
        actionManager.registerAction("bindProjectToMachine", bindProjectToMachineAction);

        // add actions in main menu
        machinesMenu.add(stopMachineAction);
        machinesMenu.add(bindProjectToMachineAction);
    }

    @Inject
    private void prepareMachineConsole(ActionManager actionManager,
                                       ClearConsoleAction clearConsoleAction,
                                       WorkspaceAgent workspaceAgent,
                                       MachineConsolePresenter machineConsolePresenter,
                                       @MachineConsoleToolbar ToolbarPresenter builderConsoleToolbar) {
        workspaceAgent.openPart(machineConsolePresenter, PartStackType.INFORMATION);

        // add toolbar with indicators to Builder console
        DefaultActionGroup consoleToolbarActionGroup = new DefaultActionGroup(GROUP_MACHINE_CONSOLE_TOOLBAR, false, actionManager);
        consoleToolbarActionGroup.add(clearConsoleAction);
        consoleToolbarActionGroup.addSeparator();
        builderConsoleToolbar.bindMainGroup(consoleToolbarActionGroup);
    }
}