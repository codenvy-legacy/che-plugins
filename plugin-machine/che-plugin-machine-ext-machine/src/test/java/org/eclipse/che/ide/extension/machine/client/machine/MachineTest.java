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

import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.machine.shared.dto.ServerDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.eclipse.che.ide.extension.machine.client.machine.Machine.TERMINAL_URL_KEY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
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
    private MachineDescriptor descriptor;
    @Mock
    private ServerDescriptor  serverDescriptor;

    private Map<String, ServerDescriptor> servers;

    @InjectMocks
    private Machine machine;

    @Before
    public void setUp() {
        servers = new HashMap<>();

        when(serverDescriptor.getAddress()).thenReturn(SOME_TEXT);
        when(descriptor.getServers()).thenReturn(servers);

        machine.setDescriptor(descriptor);
    }

    @Test
    public void idShouldBeReturned() {
        machine.getId();

        verify(descriptor).getId();
    }

    @Test
    public void stateShouldBeReturned() {
        machine.getState();

        verify(descriptor).getState();
    }

    @Test
    public void typeShouldBeReturned() {
        machine.getType();

        verify(descriptor).getType();
    }

    @Test
    public void terminalUrlShouldBeReturned() {
        servers.put(TERMINAL_URL_KEY, serverDescriptor);

        String url = machine.getTerminalUrl();

        verify(descriptor).getServers();
        verify(serverDescriptor).getAddress();

        assertThat(url, equalTo("http://someText"));
    }

    @Test
    public void emptyStringShouldBeReturnedWhenServerDescriptorNotFound() {
        servers.put(SOME_TEXT, serverDescriptor);

        String url = machine.getTerminalUrl();

        verify(serverDescriptor, never()).getAddress();

        assertThat(url, equalTo(""));
    }

    @Test
    public void boundedStateShouldBeReturned() {
        machine.isWorkspaceBound();

        verify(descriptor).isWorkspaceBound();
    }
}