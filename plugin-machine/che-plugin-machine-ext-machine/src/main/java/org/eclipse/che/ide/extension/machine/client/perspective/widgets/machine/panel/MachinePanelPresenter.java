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
import com.google.inject.Inject;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.inject.factories.WidgetsFactory;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.MachineWidget;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.info.InfoContainerPresenter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * The class contains business logic to control displaying of machines on special view.
 *
 * @author Dmitry Shnurenko
 */
public class MachinePanelPresenter extends BasePresenter implements MachineWidget.ActionDelegate {

    private final MachinePanel                view;
    private final MachineServiceClient        service;
    private final EntityFactory               entityFactory;
    private final WidgetsFactory              widgetsFactory;
    private final MachineLocalizationConstant locale;
    private final InfoContainerPresenter      infoContainer;

    @Inject
    public MachinePanelPresenter(MachinePanel view,
                                 MachineServiceClient service,
                                 EntityFactory entityFactory,
                                 WidgetsFactory widgetsFactory,
                                 MachineLocalizationConstant locale,
                                 InfoContainerPresenter infoContainer) {
        this.view = view;
        this.service = service;
        this.entityFactory = entityFactory;
        this.widgetsFactory = widgetsFactory;
        this.locale = locale;
        this.infoContainer = infoContainer;
    }

    /** Gets all machines and adds them to special place on view. */
    public void getMachines() {
        Promise<List<MachineDescriptor>> machinesPromise = service.getMachines(null);

        machinesPromise.then(new Operation<List<MachineDescriptor>>() {
            @Override
            public void apply(List<MachineDescriptor> machines) throws OperationException {
                for (MachineDescriptor descriptor : machines) {
                    view.clear();

                    createAndAddWidget(descriptor);
                }
            }
        });
    }

    private void createAndAddWidget(@Nonnull MachineDescriptor descriptor) {
        Machine machine = entityFactory.createMachine();
        machine.setDescriptor(descriptor);

        MachineWidget widget = widgetsFactory.createMachineWidget();
        widget.setDelegate(MachinePanelPresenter.this);

        widget.update(machine);

        view.add(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void onMachineClicked(@Nonnull Machine machine) {
        infoContainer.showInfo(machine);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getTitle() {
        return locale.machineConsoleViewTitle();
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
