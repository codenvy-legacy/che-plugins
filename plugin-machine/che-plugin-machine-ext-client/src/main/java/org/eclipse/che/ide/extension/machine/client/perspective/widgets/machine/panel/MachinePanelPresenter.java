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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance.MachineAppliancePresenter;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.InputCallback;
import org.eclipse.che.ide.ui.dialogs.input.InputDialog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The class contains business logic to control displaying of machines on special view.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class MachinePanelPresenter extends BasePresenter implements MachinePanelView.ActionDelegate {

    private final MachinePanelView            view;
    private final MachineServiceClient        service;
    private final EntityFactory               entityFactory;
    private final MachineLocalizationConstant locale;
    private final MachineAppliancePresenter   appliance;
    private final Provider<MachineManager>    managerProvider;
    private final DialogFactory               dialogFactory;
    private final List<Machine>               machineList;

    private Machine selectedMachine;
    private boolean isFirstNode;

    @Inject
    public MachinePanelPresenter(MachinePanelView view,
                                 MachineServiceClient service,
                                 EntityFactory entityFactory,
                                 MachineLocalizationConstant locale,
                                 MachineAppliancePresenter appliance,
                                 Provider<MachineManager> managerProvider,
                                 DialogFactory dialogFactory) {
        this.view = view;
        this.view.setDelegate(this);

        this.service = service;
        this.entityFactory = entityFactory;
        this.locale = locale;
        this.appliance = appliance;
        this.managerProvider = managerProvider;
        this.dialogFactory = dialogFactory;

        this.machineList = new ArrayList<>();
    }

    /** Gets all machines and adds them to special place on view. */
    public void showMachines() {
        Promise<List<MachineDescriptor>> machinesPromise = service.getMachines(null);

        machinesPromise.then(new Operation<List<MachineDescriptor>>() {
            @Override
            public void apply(List<MachineDescriptor> machines) throws OperationException {
                machineList.clear();
                isFirstNode = true;

                List<MachineTreeNode> rootChildren = new ArrayList<>();
                List<MachineTreeNode> environmentChildren = new ArrayList<>();

                MachineTreeNode rootNode = entityFactory.createMachineNode(null, "root", rootChildren);

                MachineTreeNode environmentNode = entityFactory.createMachineNode(rootNode, "environment", environmentChildren);
                rootChildren.add(environmentNode);

                MachineTreeNode selectedNode = null;

                for (MachineDescriptor descriptor : machines) {
                    Machine machine = entityFactory.createMachine(descriptor);

                    machineList.add(machine);

                    MachineTreeNode machineNode = entityFactory.createMachineNode(environmentNode, machine, null);

                    environmentChildren.add(machineNode);

                    if (isFirstNode) {
                        selectedNode = machineNode;

                        isFirstNode = false;
                    }
                }

                view.setData(rootNode);

                view.selectNode(selectedNode);
            }
        });
    }

    /** Creates machine and adds it in special place on view. */
    public void createMachine() {
        final InputCallback inputCallback = new InputCallback() {
            @Override
            public void accepted(String value) {
                MachineManager manager = managerProvider.get();
                manager.startMachine(true, value);
            }
        };

        final String defaultName = generateDefaultName();
        final InputDialog dialog = dialogFactory.createInputDialog(locale.machineCreateTitle(),
                                                                   locale.machineCreateMessage(),
                                                                   defaultName,
                                                                   0,
                                                                   defaultName.length(),
                                                                   inputCallback,
                                                                   null);
        dialog.show();
    }

    private String generateDefaultName() {
        final Set<String> machineNames = new HashSet<>();
        for (Machine machine : machineList) {
            machineNames.add(machine.getDisplayName());
        }

        int index = 1;
        String name = "Machine (" + index + ')';
        while (machineNames.contains(name)) {
            name = "Machine (" + ++index + ')';
        }

        return name;
    }

    /** Destroys machine and removes it from view. */
    public void destroyMachine() {
        machineList.remove(selectedMachine);

        MachineManager manager = managerProvider.get();
        manager.destroyMachine(selectedMachine.getId());
    }

    /** {@inheritDoc} */
    @Override
    public void onMachineSelected(@Nonnull Machine selectedMachine) {
        this.selectedMachine = selectedMachine;

        appliance.showAppliance(selectedMachine);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getTitle() {
        return locale.machineConsoleViewTitle();
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }

    /** {@inheritDoc} */
    @Override
    public IsWidget getView() {
        return view;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getTitleToolTip() {
        return locale.machineConsoleViewTooltip();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
