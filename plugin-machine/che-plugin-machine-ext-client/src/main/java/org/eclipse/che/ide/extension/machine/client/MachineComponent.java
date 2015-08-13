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
package org.eclipse.che.ide.extension.machine.client;

import com.google.gwt.core.client.Callback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.machine.shared.dto.MachineStateDescriptor;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.core.Component;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;

import java.util.List;

/**
 * {@link Component} that responsible for starting a Dev-machine.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MachineComponent implements Component {

    public static final String DEFAULT_RECIPE =
            "https://gist.githubusercontent.com/gazarenkov/9f11a85a157ab399aca5/raw/b9f66169588f5394f84a1893cfeccd10e18d7fc8/maven";

    private final MachineServiceClient machineServiceClient;
    private final AppContext           appContext;
    private final MachineManager       machineManager;
    private final EntityFactory        entityFactory;

    @Inject
    public MachineComponent(MachineServiceClient machineServiceClient,
                            AppContext appContext,
                            MachineManager machineManager,
                            EntityFactory entityFactory) {
        this.machineServiceClient = machineServiceClient;
        this.appContext = appContext;
        this.machineManager = machineManager;
        this.entityFactory = entityFactory;
    }

    @Override
    public void start(final Callback<Component, Exception> callback) {
        callback.onSuccess(this);

        machineServiceClient.getMachinesStates(null).then(new Operation<List<MachineStateDescriptor>>() {
            @Override
            public void apply(List<MachineStateDescriptor> arg) throws OperationException {
                for (MachineStateDescriptor machineStateDescriptor : arg) {
                    if (machineStateDescriptor.isDev()) {
                        appContext.setDevMachineId(machineStateDescriptor.getId());

                        // TODO: should be removed when IDEX-2858 will be done
                        machineServiceClient.getMachine(machineStateDescriptor.getId()).then(new Operation<MachineDescriptor>() {
                            @Override
                            public void apply(MachineDescriptor arg) throws OperationException {
                                machineManager.setDeveloperMachine(entityFactory.createMachine(arg));
                            }
                        });
                        return;
                    }
                }

                machineManager.startDevMachine(DEFAULT_RECIPE, "Dev");
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                callback.onFailure(new Exception(arg.getMessage()));
            }
        });
    }
}
