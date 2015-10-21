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
package org.eclipse.che.ide.extension.machine.client.machine;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineMetadataDto;
import org.eclipse.che.api.machine.shared.dto.MachineStateDto;
import org.eclipse.che.api.machine.shared.dto.ServerDto;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.eclipse.che.ide.extension.machine.client.machine.Machine.TERMINAL_REF_KEY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class MachineTest {

    private final static String SOME_TEXT = "someText";

    @Mock
    private MachineStateDto             descriptor;
    @Mock
    private ServerDto                   serverDescriptor;
    @Mock
    private MachineMetadataDto          metadataDto;
    @Mock
    private MachineLocalizationConstant locale;
    @Mock
    private MachineServiceClient        serviceClient;

    private MachineState machineState;

    @Before
    public void setUp() {
        Map<String, ServerDto> servers = new HashMap<>();
        servers.put(SOME_TEXT, serverDescriptor);

        machineState = new MachineState(locale, serviceClient, descriptor);

        when(serverDescriptor.getAddress()).thenReturn(SOME_TEXT);
        when(metadataDto.getServers()).thenReturn(servers);
    }

    @Test
    public void constructorShouldBeVerified() {
        verify(locale).tabInfo();
    }

    @Test
    public void defaultActiveTabShouldBeReturned() {
        when(locale.tabInfo()).thenReturn(SOME_TEXT);
        machineState = new MachineState(locale, serviceClient, descriptor);

        String tabName = machineState.getActiveTabName();

        assertThat(tabName, equalTo(SOME_TEXT));
    }

    @Test
    public void activeTabNameShouldBeSet() {
        machineState.setActiveTabName(SOME_TEXT);

        String tabName = machineState.getActiveTabName();

        assertThat(tabName, equalTo(SOME_TEXT));
    }

    @Test
    public void displayNameShouldBeReturned() {
        machineState.getDisplayName();

        verify(descriptor).getName();
    }

    @Test
    public void idShouldBeReturned() {
        machineState.getId();

        verify(descriptor).getId();
    }

    @Test
    public void stateShouldBeReturned() {
        machineState.getStatus();

        verify(descriptor).getStatus();
    }

    @Test
    public void typeShouldBeReturned() {
        machineState.getType();

        verify(descriptor).getType();
    }

    @Test
    @Ignore
    //TODO fix test
    public void terminalUrlShouldBeReturned() {
        when(serverDescriptor.getRef()).thenReturn(TERMINAL_REF_KEY);
        when(serverDescriptor.getUrl()).thenReturn(SOME_TEXT);

//        String url = machineState.getTerminalUrl();

        verify(metadataDto).getServers();
        verify(serverDescriptor).getRef();
        verify(serverDescriptor).getUrl();

//        assertThat(url, equalTo(SOME_TEXT));
    }

    @Test
    @Ignore
    //TODO fix test
    public void nullShouldBeReturnedWhenTerminalRefIsNull() {
        when(serverDescriptor.getRef()).thenReturn(null);

//        String url = machine.getTerminalUrl();

        verify(serverDescriptor, never()).getUrl();

//        assertThat(url, equalTo(""));
    }

    @Test
    public void boundedStateShouldBeReturned() {
        machineState.isDev();

        verify(descriptor).isDev();
    }

    @Test
    @Ignore
    //TODO fix test
    public void projectsRootShouldBeReturned() {
        MachineMetadataDto machineMetadata = mock(MachineMetadataDto.class);
        when(machineMetadata.projectsRoot()).thenReturn(SOME_TEXT);
//        when(descriptor.getMetadata()).thenReturn(machineMetadata);

//        String projectsRoot = machine.getProjectsRoot();

//        verify(descriptor).getMetadata();
        verify(machineMetadata).projectsRoot();
//        assertThat(projectsRoot, equalTo(SOME_TEXT));
    }
}