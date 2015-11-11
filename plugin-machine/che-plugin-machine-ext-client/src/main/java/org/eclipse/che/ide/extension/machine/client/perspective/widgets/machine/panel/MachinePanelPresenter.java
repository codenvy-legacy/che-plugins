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
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.gwt.client.events.ExtServerStateEvent;
import org.eclipse.che.api.machine.gwt.client.events.ExtServerStateHandler;
import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.api.machine.shared.dto.MachineStateDto;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStartingEvent;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStartingHandler;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStateEvent;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStateHandler;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance.MachineAppliancePresenter;
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
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MachinePanelPresenter extends BasePresenter implements MachinePanelView.ActionDelegate,
                                                                    MachineStateHandler,
                                                                    ExtServerStateHandler,
                                                                    MachineStartingHandler {
    private final MachinePanelView                      view;
    private final MachineServiceClient                  service;
    private final EntityFactory                         entityFactory;
    private final MachineLocalizationConstant           locale;
    private final MachineAppliancePresenter             appliance;
    private final MachineResources                      resources;
    private final Map<MachineStateDto, MachineTreeNode> existingMachineNodes;
    private final Map<MachineStateDto, Machine>         cachedMachines;
    private final MachineTreeNode                       rootNode;
    private final List<MachineTreeNode>                 rootChildren;

    private MachineStateDto selectedMachineState;
    private boolean         isMachineRunning;

    @Inject
    public MachinePanelPresenter(MachinePanelView view,
                                 MachineServiceClient service,
                                 EntityFactory entityFactory,
                                 MachineLocalizationConstant locale,
                                 MachineAppliancePresenter appliance,
                                 EventBus eventBus,
                                 MachineResources resources) {
        this.view = view;
        this.view.setDelegate(this);

        this.service = service;
        this.entityFactory = entityFactory;
        this.locale = locale;
        this.appliance = appliance;
        this.resources = resources;

        this.rootChildren = new ArrayList<>();
        this.rootNode = entityFactory.createMachineNode(null, "root", rootChildren);

        this.existingMachineNodes = new HashMap<>();
        this.cachedMachines = new HashMap<>();

        eventBus.addHandler(MachineStateEvent.TYPE, this);
        eventBus.addHandler(ExtServerStateEvent.TYPE, this);
        eventBus.addHandler(MachineStartingEvent.TYPE, this);
    }

    /** Gets all machines and adds them to special place on view. */
    public Promise<List<MachineStateDto>> showMachines() {
        Promise<List<MachineStateDto>> machinesPromise = service.getMachinesStates(null);

        return machinesPromise.then(new Operation<List<MachineStateDto>>() {
            @Override
            public void apply(List<MachineStateDto> machineStates) throws OperationException {
                if (machineStates.isEmpty()) {
                    appliance.showStub(locale.unavailableMachineInfo());

                    return;
                }

                rootChildren.clear();

                for (MachineStateDto machineState : machineStates) {
                    addNodeToTree(machineState);
                }

                view.setData(rootNode);

                selectFirstNode();
            }
        });
    }

    private void addNodeToTree(MachineStateDto machineState) {
        MachineTreeNode machineNode = entityFactory.createMachineNode(rootNode, machineState, null);

        existingMachineNodes.put(machineState, machineNode);

        if (!rootChildren.contains(machineNode)) {
            rootChildren.add(machineNode);
        }
    }

    private void selectFirstNode() {
        if (!rootChildren.isEmpty()) {
            MachineTreeNode firstNode = rootChildren.get(0);

            view.selectNode(firstNode);
        }
    }

    /**
     * Returns selected machine state.
     */
    public MachineStateDto getSelectedMachineState() {
        return selectedMachineState;
    }

    /** {@inheritDoc} */
    @Override
    public void onMachineSelected(final MachineStateDto selectedMachineState) {
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

                appliance.showStub(locale.unavailableMachineStarting(selectedMachineState.getName()));
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
    public void onExtServerStarted(ExtServerStateEvent event) {
        showMachines();
    }

    /** {@inheritDoc} */
    @Override
    public void onExtServerStopped(ExtServerStateEvent event) {
    }

    /** {@inheritDoc} */
    @Override
    public void onMachineStarting(final MachineStartingEvent event) {
        isMachineRunning = false;

        selectedMachineState = event.getMachineState();

        addNodeToTree(selectedMachineState);

        view.setData(rootNode);

        view.selectNode(existingMachineNodes.get(event.getMachineState()));
    }

    /** {@inheritDoc} */
    @Override
    public void onMachineRunning(final MachineStateEvent event) {
        isMachineRunning = true;

        selectedMachineState = event.getMachineState();

        view.selectNode(existingMachineNodes.get(selectedMachineState));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMachineDestroyed(MachineStateEvent event) {
        MachineStateDto machineState = event.getMachineState();

        MachineTreeNode deletedNode = existingMachineNodes.get(machineState);

        rootChildren.remove(deletedNode);
        existingMachineNodes.remove(machineState);

        view.setData(rootNode);

        selectFirstNode();
    }

    /**
     * Returns <code>true</code> if selected machine running, and <code>false</code> if selected machine isn't running
     */
    public boolean isMachineRunning() {
        return isMachineRunning;
    }
}
