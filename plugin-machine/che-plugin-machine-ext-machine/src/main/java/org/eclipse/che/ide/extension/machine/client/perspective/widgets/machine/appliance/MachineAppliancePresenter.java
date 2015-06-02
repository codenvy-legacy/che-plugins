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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.inject.factories.WidgetsFactory;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance.processes.ProcessesPresenter;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance.terminal.TerminalPresenter;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.Tab;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.container.TabContainerPresenter;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.header.TabHeader;
import org.eclipse.che.ide.part.PartStackPresenter;

import javax.annotation.Nonnull;

/**
 * The class is a container for tab panels which display additional information about machine and adds ability to control machine's
 * processes. The class is a wrapper of {@link TabContainerPresenter} to use logic  of {@link TabContainerPresenter} for control tabs
 * instead {@link PartStackPresenter}
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class MachineAppliancePresenter extends PartStackPresenter {

    private final MachineApplianceView  view;
    private final TabContainerPresenter tabContainer;
    private final ProcessesPresenter    processesPresenter;

    private Tab processesTab;

    @Inject
    public MachineAppliancePresenter(EventBus eventBus,
                                     PartStackEventHandler partStackEventHandler,
                                     MachineApplianceView view,
                                     MachineLocalizationConstant locale,
                                     WidgetsFactory widgetsFactory,
                                     EntityFactory entityFactory,
                                     ProcessesPresenter processesPresenter,
                                     TerminalPresenter terminalPresenter,
                                     TabContainerPresenter tabContainer) {
        super(eventBus, partStackEventHandler, view, null);
        this.view = view;
        this.tabContainer = tabContainer;
        this.processesPresenter = processesPresenter;

        TabHeader processesHeader = widgetsFactory.createTabHeader(locale.tabProcesses());
        processesTab = entityFactory.createTab(processesHeader, processesPresenter);

        TabHeader terminalHeader = widgetsFactory.createTabHeader(locale.tabTerminal());
        Tab terminalTab = entityFactory.createTab(terminalHeader, terminalPresenter);

        tabContainer.addTab(terminalTab);
        tabContainer.addTab(processesTab);

        tabContainer.showTab(processesTab);

        this.view.addContainer(tabContainer.getView());
    }

    /**
     * Shows all information and processes about current machine.
     *
     * @param machine
     *         machine for which need show info
     */
    public void showInfo(@Nonnull Machine machine) {
        tabContainer.showTab(processesTab);

        processesPresenter.getProcesses(machine.getId());
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
