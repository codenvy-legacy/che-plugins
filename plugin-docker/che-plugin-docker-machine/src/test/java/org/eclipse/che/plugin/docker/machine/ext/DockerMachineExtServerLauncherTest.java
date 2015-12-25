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

package org.eclipse.che.plugin.docker.machine.ext;

import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.machine.server.MachineManager;
import org.eclipse.che.api.machine.server.model.impl.CommandImpl;
import org.eclipse.che.api.machine.shared.dto.event.MachineStatusEvent;
import org.eclipse.che.dto.server.DtoFactory;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Max Shaposhnik
 *
 */
@Listeners(value = {MockitoTestNGListener.class})
public class DockerMachineExtServerLauncherTest {
    private static final String MACHINE_ID                   = "someMachineId";
    private static final String WORKSPACE_ID                 = "someWorkspaceId";
    private static final String WS_AGENT_LAUNCH_COMMAND_LINE = "./ws-agent-test.sh run";

    @Mock
    private MachineManager machineManager;

    private MachineStatusEvent defaultEvent;
    private EventService       eventService;

    @BeforeMethod
    public void setUp() throws Exception {
        eventService = new EventService();
        DockerMachineExtServerLauncher launcher = new DockerMachineExtServerLauncher(eventService,
                                                                                     machineManager,
                                                                                     WS_AGENT_LAUNCH_COMMAND_LINE);
        defaultEvent = DtoFactory.newDto(MachineStatusEvent.class)
                                 .withMachineId(MACHINE_ID)
                                 .withDev(true)
                                 .withMachineName("machineName")
                                 .withWorkspaceId(WORKSPACE_ID)
                                 .withEventType(MachineStatusEvent.EventType.RUNNING);
        launcher.start();
    }

    @Test(dataProvider = "machineStatusEventTypeProvider")
    public void shouldNotStartWsAgentOnNonRunningEvent(MachineStatusEvent.EventType machineStatusEventType) {
        eventService.publish(defaultEvent.withEventType(machineStatusEventType));

        verifyZeroInteractions(machineManager);
    }


    @DataProvider
    public static Object[][] machineStatusEventTypeProvider() {
        return new Object[][]{{MachineStatusEvent.EventType.CREATING},
                              {MachineStatusEvent.EventType.DESTROYING},
                              {MachineStatusEvent.EventType.DESTROYED},
                              {MachineStatusEvent.EventType.ERROR}};
    }

    @Test
    public void shouldStartWsAgentInDevMachineOnRunningEvent() throws Exception {
        eventService.publish(defaultEvent);

        verify(machineManager).exec(eq(MACHINE_ID),
                                    eq(new CommandImpl(DockerMachineExtServerLauncher.WS_AGENT_PROCESS_NAME,
                                                       WS_AGENT_LAUNCH_COMMAND_LINE,
                                                       "Arbitrary")),
                                    eq(String.format(DockerMachineExtServerLauncher.WS_AGENT_PROCESS_OUTPUT_CHANNEL, WORKSPACE_ID)));
    }

    @Test
    public void shouldNotStartWsAgentInNonDevMachine() throws Exception {
        eventService.publish(defaultEvent.withDev(false));

        verifyZeroInteractions(machineManager);
    }
}
