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
package org.eclipse.che.ide.extension.machine.client.command.valueproviders;

import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.api.machine.shared.dto.MachineStateDto;
import org.eclipse.che.api.machine.shared.dto.ServerDto;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStateEvent;
import org.eclipse.che.ide.extension.machine.client.machine.events.MachineStateHandler;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.transformEntries;

/**
 * Provide mapping internal port, e.g. 8080 to external port.
 * Intermediate solution.
 *
 * @author Vlad Zhukovskiy
 */
@Singleton
public class MachinePortProvider implements MachineStateHandler {

    public static final String KEY_TEMPLATE = "${machine.port.%}";

    private final MachineServiceClient                 machineServiceClient;
    private final CommandPropertyValueProviderRegistry commandPropertyRegistry;

    private Collection<CommandPropertyValueProvider> providers;

    private final Operation<MachineDto> registerProviders = new Operation<MachineDto>() {
        @Override
        public void apply(MachineDto machine) throws OperationException {
            providers = getProviders(machine);
            commandPropertyRegistry.register(Sets.newHashSet(providers));
        }
    };

    @Inject
    public MachinePortProvider(EventBus eventBus,
                               MachineServiceClient machineServiceClient,
                               CommandPropertyValueProviderRegistry commandPropertyRegistry,
                               AppContext appContext) {
        this.machineServiceClient = machineServiceClient;
        this.commandPropertyRegistry = commandPropertyRegistry;

        eventBus.addHandler(MachineStateEvent.TYPE, this);
        if (!isNullOrEmpty(appContext.getDevMachineId())) {
            machineServiceClient.getMachine(appContext.getDevMachineId()).then(registerProviders);
        }
    }

    @Override
    public void onMachineRunning(MachineStateEvent event) {
        final MachineStateDto machineState = event.getMachineState();
        if (machineState.isDev()) {
            machineServiceClient.getMachine(machineState.getId()).then(registerProviders);
        }
    }

    private Collection<CommandPropertyValueProvider> getProviders(MachineDto machine) {
        EntryTransformer<String, ServerDto, CommandPropertyValueProvider> machineToProvider =
                new EntryTransformer<String, ServerDto, CommandPropertyValueProvider>() {
                    @Override
                    public CommandPropertyValueProvider transformEntry(String internalPort, ServerDto serverConfiguration) {
                        String[] hostPort = serverConfiguration.getAddress().split(":");
                        if (hostPort.length != 2) {
                            return null;
                        }

                        return new PortProvider(internalPort, hostPort[1]);
                    }
                };

        Map<String, CommandPropertyValueProvider> providers = transformEntries(machine.getMetadata().getServers(), machineToProvider);

        return providers.values();
    }

    @Override
    public void onMachineDestroyed(MachineStateEvent event) {
        final MachineStateDto machineState = event.getMachineState();
        if (machineState.isDev() && providers != null) {
            commandPropertyRegistry.getProviders().removeAll(providers);
        }
    }

    private class PortProvider implements CommandPropertyValueProvider {

        String internalPort;
        String externalPort;

        PortProvider(String internalPort, String externalPort) {
            this.internalPort = internalPort;
            this.externalPort = externalPort;
        }

        @Override
        public String getKey() {
            return KEY_TEMPLATE.replace("%", internalPort);
        }

        @Override
        public String getValue() {
            return externalPort;
        }
    }
}
