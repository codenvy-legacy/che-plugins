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

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.event.ProjectActionEvent;
import org.eclipse.che.ide.api.event.ProjectActionHandler;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.extension.machine.client.actions.EditConfigurationsAction;
import org.eclipse.che.ide.extension.machine.client.actions.ExecuteCommandAction;
import org.eclipse.che.ide.extension.machine.client.actions.TerminateMachineAction;
import org.eclipse.che.ide.extension.machine.client.console.ClearConsoleAction;
import org.eclipse.che.ide.extension.machine.client.console.MachineConsolePresenter;
import org.eclipse.che.ide.extension.machine.client.console.MachineConsoleToolbar;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
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

    public static final String GROUP_MACHINE_CONSOLE_TOOLBAR = "MachineConsoleToolbar";

    @Inject
    public MachineExtension(MachineResources machineResources,
                            EventBus eventBus,
                            final AppContext appContext,
                            final MachineServiceClient machineServiceClient,
                            final MachineManager machineManager,
                            final MachineConsolePresenter machineConsolePresenter) {

        machineResources.machine().ensureInjected();

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                final String projectPath = event.getProject().getPath();

                // start machine and bind project
                Promise<Array<MachineDescriptor>> machinesPromise = machineServiceClient.getMachines(appContext.getWorkspace().getId(),
                                                                                                     projectPath);
                machinesPromise.then(new Operation<Array<MachineDescriptor>>() {
                    @Override
                    public void apply(Array<MachineDescriptor> arg) throws OperationException {
                        if (arg.isEmpty()) {
                            machineManager.startMachineAndBindProject(projectPath);
                        } else {
                            machineManager.setDevMachineId(arg.get(0).getId());
                        }
                    }
                });
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                machineManager.setDevMachineId(null);
                machineConsolePresenter.clear();
            }
        });
    }

    @Inject
    private void prepareActions(MachineLocalizationConstant localizationConstant,
                                ActionManager actionManager,
                                ExecuteCommandAction executeCommandAction,
                                EditConfigurationsAction editConfigurationsAction,
                                TerminateMachineAction terminateMachineAction) {
        final DefaultActionGroup mainMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_MENU);

        final String runGroupId = "run";
        final DefaultActionGroup machinesMenu = new DefaultActionGroup(localizationConstant.mainMenuRunName(), true, actionManager);
        actionManager.registerAction(runGroupId, machinesMenu);

        mainMenu.add(machinesMenu, new Constraints(AFTER, GROUP_CODE));

        // register actions
        actionManager.registerAction("executeCommand", executeCommandAction);
        actionManager.registerAction("editConfigurations", editConfigurationsAction);
        actionManager.registerAction("stopMachine", terminateMachineAction);

        // add actions in main menu
        machinesMenu.add(executeCommandAction);
        machinesMenu.add(editConfigurationsAction);
        machinesMenu.addSeparator();
        machinesMenu.add(terminateMachineAction);
    }

    @Inject
    private void prepareMachineConsole(ActionManager actionManager,
                                       ClearConsoleAction clearConsoleAction,
                                       WorkspaceAgent workspaceAgent,
                                       MachineConsolePresenter machineConsolePresenter,
                                       @MachineConsoleToolbar ToolbarPresenter machineConsoleToolbar) {
        workspaceAgent.openPart(machineConsolePresenter, PartStackType.INFORMATION);

        // add toolbar to Machine console
        final DefaultActionGroup consoleToolbarActionGroup = new DefaultActionGroup(GROUP_MACHINE_CONSOLE_TOOLBAR, false, actionManager);
        consoleToolbarActionGroup.add(clearConsoleAction);
        consoleToolbarActionGroup.addSeparator();
        machineConsoleToolbar.bindMainGroup(consoleToolbarActionGroup);
    }
}
