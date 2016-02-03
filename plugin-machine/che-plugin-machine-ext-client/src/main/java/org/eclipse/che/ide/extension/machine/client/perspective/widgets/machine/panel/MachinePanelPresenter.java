/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
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
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.workspace.shared.dto.UsersWorkspaceDto;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.event.ActivePartChangedEvent;
import org.eclipse.che.ide.api.event.ActivePartChangedHandler;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.api.machine.gwt.client.events.MachineStartingEvent;
import org.eclipse.che.api.machine.gwt.client.events.MachineStartingHandler;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStateEvent;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStateHandler;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance.MachineAppliancePresenter;
import org.eclipse.che.api.workspace.gwt.client.event.StartWorkspaceEvent;
import org.eclipse.che.api.workspace.gwt.client.event.StartWorkspaceHandler;
import org.eclipse.che.ide.workspace.start.StopWorkspaceEvent;
import org.eclipse.che.ide.workspace.start.StopWorkspaceHandler;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class contains business logic to control displaying of machines on special view.
 *
 * @author Dmitry Shnurenko
 * @author Artem Zatsarynnyi
 */
@Singleton
public class MachinePanelPresenter extends BasePresenter implements MachinePanelView.ActionDelegate,
                                                                    MachineStateHandler,
                                                                    StartWorkspaceHandler,
                                                                    StopWorkspaceHandler,
                                                                    MachineStartingHandler,
                                                                    ActivePartChangedHandler {
    private final MachinePanelView                      view;
    private final MachineServiceClient                  service;
    private final EntityFactory                         entityFactory;
    private final MachineLocalizationConstant           locale;
    private final MachineAppliancePresenter             appliance;
    private final MachineResources                      resources;
    private final Map<MachineDto, MachineTreeNode>      existingMachineNodes;
    private final Map<MachineDto, Machine>              cachedMachines;
    private final MachineTreeNode                       rootNode;
    private final List<MachineTreeNode>                 machineNodes;
    private final AppContext                            appContext;
    private       MachineDto                            selectedMachineState;
    private       boolean                               isMachineRunning;

    @Inject
    public MachinePanelPresenter(MachinePanelView view,
                                 MachineServiceClient service,
                                 EntityFactory entityFactory,
                                 MachineLocalizationConstant locale,
                                 MachineAppliancePresenter appliance,
                                 EventBus eventBus,
                                 MachineResources resources,
                                 AppContext appContext) {
        this.view = view;
        this.view.setDelegate(this);

        this.service = service;
        this.entityFactory = entityFactory;
        this.locale = locale;
        this.appliance = appliance;
        this.resources = resources;
        this.appContext = appContext;

        this.machineNodes = new ArrayList<>();
        this.rootNode = entityFactory.createMachineNode(null, "root", machineNodes);

        this.existingMachineNodes = new HashMap<>();
        this.cachedMachines = new HashMap<>();

        eventBus.addHandler(MachineStateEvent.TYPE, this);
        eventBus.addHandler(StartWorkspaceEvent.TYPE, this);
        eventBus.addHandler(StopWorkspaceEvent.TYPE, this);
        eventBus.addHandler(MachineStartingEvent.TYPE, this);
        eventBus.addHandler(ActivePartChangedEvent.TYPE, this);
    }

    /** Gets all machines and adds them to special place on view. */
    public Promise<List<MachineDto>> showMachines() {
        return showMachines(appContext.getWorkspace().getId());
    }

    private Promise<List<MachineDto>> showMachines(String workspaceId) {
        Promise<List<MachineDto>> machinesPromise = service.getMachines(workspaceId);

        return machinesPromise.then(new Operation<List<MachineDto>>() {
            @Override
            public void apply(List<MachineDto> machines) throws OperationException {
                machineNodes.clear();
                if (machines.isEmpty()) {
                    appliance.showStub(locale.unavailableMachineInfo());

                    return;
                }

                for (MachineDto machine : machines) {
                    addNodeToTree(machine);
                }

                view.setData(rootNode);

                selectFirstNode();
            }
        });
    }

    private void addNodeToTree(MachineDto machine) {
        MachineTreeNode machineNode = entityFactory.createMachineNode(rootNode, machine, null);

        existingMachineNodes.put(machine, machineNode);

        if (!machineNodes.contains(machineNode)) {
            machineNodes.add(machineNode);
        }
    }

    private void selectFirstNode() {
        if (!machineNodes.isEmpty()) {
            MachineTreeNode firstNode = machineNodes.get(0);

            view.selectNode(firstNode);
        }
    }

    /**
     * Returns selected machine state.
     */
    public MachineDto getSelectedMachineState() {
        return selectedMachineState;
    }

    /** {@inheritDoc} */
    @Override
    public void onMachineSelected(final MachineDto selectedMachineState) {
        this.selectedMachineState = selectedMachineState;

        isMachineRunning = true;

        if (cachedMachines.containsKey(selectedMachineState)) {
            appliance.showAppliance(cachedMachines.get(selectedMachineState));

            return;
        }

        service.getMachine(selectedMachineState.getId()).then(new Operation<MachineDto>() {
            @Override
            public void apply(MachineDto machineDto) throws OperationException {
                Machine machine = entityFactory.createMachine(machineDto);

                cachedMachines.put(selectedMachineState, machine);

                appliance.showAppliance(machine);
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError error) throws OperationException {
                isMachineRunning = false;

                // we show the loader for dev machine so this message isn't necessary for dev machine
                if (!selectedMachineState.getConfig().isDev()) {
                    appliance.showStub(locale.unavailableMachineStarting(selectedMachineState.getConfig().getName()));
                }
            }
        });
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getTitle() {
        return locale.machinePanelTitle();
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

    @Nullable
    @Override
    public SVGResource getTitleSVGImage() {
        return resources.machinesPartIcon();
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getTitleToolTip() {
        return locale.machinePanelTooltip();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onWorkspaceStarted(UsersWorkspaceDto workspace) {
        showMachines(workspace.getId());
    }

    /** {@inheritDoc} */
    @Override
    public void onWorkspaceStopped(UsersWorkspaceDto workspace) {
        showMachines(workspace.getId());
    }

    /** {@inheritDoc} */
    @Override
    public void onMachineStarting(final MachineStartingEvent event) {
        isMachineRunning = false;

        selectedMachineState = event.getMachine();

        addNodeToTree(selectedMachineState);

        view.setData(rootNode);

        view.selectNode(existingMachineNodes.get(event.getMachine()));
    }

    /** {@inheritDoc} */
    @Override
    public void onMachineRunning(final MachineStateEvent event) {
        isMachineRunning = true;

        selectedMachineState = event.getMachine();

        view.selectNode(existingMachineNodes.get(selectedMachineState));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMachineDestroyed(MachineStateEvent event) {
        MachineDto machine = event.getMachine();

        MachineTreeNode deletedNode = existingMachineNodes.get(machine);

        machineNodes.remove(deletedNode);
        existingMachineNodes.remove(machine);

        view.setData(rootNode);

        selectFirstNode();
    }

    /**
     * Returns <code>true</code> if selected machine running, and <code>false</code> if selected machine isn't running
     */
    public boolean isMachineRunning() {
        return isMachineRunning;
    }

    @Override
    public void onActivePartChanged(ActivePartChangedEvent event) {
        if ((event.getActivePart() instanceof MachinePanelPresenter)) {
            showMachines();
        }
    }
}
