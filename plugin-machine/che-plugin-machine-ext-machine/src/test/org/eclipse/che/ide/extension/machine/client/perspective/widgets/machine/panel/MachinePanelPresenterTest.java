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

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.inject.factories.WidgetsFactory;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.MachineWidget;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.info.InfoContainerPresenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class MachinePanelPresenterTest {

    //constructor mocks
    @Mock
    private MachinePanel                view;
    @Mock
    private MachineServiceClient        service;
    @Mock
    private EntityFactory               entityFactory;
    @Mock
    private WidgetsFactory              widgetsFactory;
    @Mock
    private MachineLocalizationConstant locale;
    @Mock
    private InfoContainerPresenter      infoContainer;

    //additional mocks
    @Mock
    private Promise<List<MachineDescriptor>> machinePromise;
    @Mock
    private CurrentProject                   currentProject;
    @Mock
    private ProjectDescriptor                projectDescriptor;
    @Mock
    private MachineDescriptor                machineDescriptor;
    @Mock
    private Machine                          machine;
    @Mock
    private MachineWidget                    machineWidget;
    @Mock
    private AcceptsOneWidget                 container;

    @Captor
    private ArgumentCaptor<Operation<List<MachineDescriptor>>> operationCaptor;

    @InjectMocks
    private MachinePanelPresenter presenter;

    @Before
    public void setUp() {
        when(entityFactory.createMachine()).thenReturn(machine);
        when(widgetsFactory.createMachineWidget()).thenReturn(machineWidget);

        when(service.getMachines(null)).thenReturn(machinePromise);
    }

    @Test
    public void machinesShouldBeReturnedAndWidgetShouldBeCreated() throws Exception {
        presenter.getMachines();

        verify(service).getMachines(null);

        verify(machinePromise).then(operationCaptor.capture());
        operationCaptor.getValue().apply(Arrays.asList(machineDescriptor));

        verify(view).clear();

        verify(entityFactory).createMachine();
        verify(machine).setDescriptor(machineDescriptor);

        verify(widgetsFactory).createMachineWidget();
        verify(machineWidget).setDelegate(presenter);
        verify(machineWidget).update(machine);

        verify(view).add(machineWidget);
    }

    @Test
    public void onMachineShouldBeClicked() {
        presenter.onMachineClicked(machine);

        verify(infoContainer).showInfo(machine);
    }

    @Test
    public void titleShouldBeReturned() {
        presenter.getTitle();

        verify(locale).machineConsoleViewTitle();
    }

    @Test
    public void titleImageShouldBeReturned() {
        ImageResource resource = presenter.getTitleImage();

        assertThat(resource, nullValue(ImageResource.class));
    }

    @Test
    public void titleTooltipShouldBeReturned() {
        presenter.getTitleToolTip();

        verify(locale).machineConsoleViewTooltip();
    }

    @Test
    public void viewShouldBeSetToContainer() {
        presenter.go(container);

        verify(container).setWidget(view);
    }
}