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
import org.eclipse.che.ide.bootstrap.ProjectTemplatesComponent;
import org.eclipse.che.ide.bootstrap.ProjectTypeComponent;
import org.eclipse.che.ide.core.Component;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
import org.eclipse.che.ide.util.loging.Log;

import java.util.List;

import static org.eclipse.che.api.machine.shared.MachineStatus.RUNNING;

/**
 * {@link Component} that provides running a Dev-machine after loading IDE.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MachineComponent implements Component {

    public static final String DEFAULT_RECIPE =
            "https://gist.githubusercontent.com/vparfonov/5c633534bfb0c127854f/raw/f176ee3428c2d39d08c7b4762aee6855dc5c8f75/jdk8_maven3_tomcat8";

    private final MachineServiceClient machineServiceClient;
    private final AppContext           appContext;
    private final MachineManager       machineManager;
    private       ProjectTypeComponent projectTypeComponent;
    private ProjectTemplatesComponent projectTemplatesComponent;

    @Inject
    public MachineComponent(MachineServiceClient machineServiceClient,
                            AppContext appContext,
                            MachineManager machineManager,
                            ProjectTypeComponent projectTypeComponent,
                            ProjectTemplatesComponent projectTemplatesComponent) {
        this.machineServiceClient = machineServiceClient;
        this.appContext = appContext;
        this.machineManager = machineManager;
        this.projectTypeComponent = projectTypeComponent;
        this.projectTemplatesComponent = projectTemplatesComponent;
    }

    @Override
    public void start(final Callback<Component, Exception> callback) {
        machineServiceClient.getMachinesStates(null).then(new Operation<List<MachineStateDescriptor>>() {
            @Override
            public void apply(List<MachineStateDescriptor> arg) throws OperationException {
                for (MachineStateDescriptor descriptor : arg) {
                    if (descriptor.isWorkspaceBound() && descriptor.getStatus() == RUNNING) {
                        appContext.setDevMachineId(descriptor.getId());

                        callback.onSuccess(MachineComponent.this);
                        // TODO: should be removed when IDEX-2858 will be done
                        machineServiceClient.getMachine(descriptor.getId()).then(new Operation<MachineDescriptor>() {
                            @Override
                            public void apply(MachineDescriptor arg) throws OperationException {
                                projectTypeComponent.start(new Callback<Component, Exception>() {

                                    @Override
                                    public void onFailure(Exception reason) {
                                        Log.error(MachineManager.class, reason.getMessage());
                                    }

                                    @Override
                                    public void onSuccess(Component result) {
                                        Log.info(getClass(), "projectTypeComponent >>>>>>>>>>>>>>>>>");

                                    }
                                });

                                projectTemplatesComponent.start(new Callback<Component, Exception>() {

                                    @Override
                                    public void onFailure(Exception reason) {
                                        Log.error(MachineManager.class, reason.getMessage());
                                    }

                                    @Override
                                    public void onSuccess(Component result) {
                                        Log.info(getClass(), ">>>>>>>>>>>>>>>>>>>>>> projectTemplatesComponent");

                                    }
                                });
                            }
                        });
                        return;
                    }
                }

                machineManager.startDevMachine(DEFAULT_RECIPE, "Dev");

                callback.onSuccess(MachineComponent.this);
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                callback.onFailure(new Exception(arg.getMessage()));
            }
        });
    }
}
