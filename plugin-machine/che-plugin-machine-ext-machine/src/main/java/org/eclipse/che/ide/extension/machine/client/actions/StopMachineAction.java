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
package org.eclipse.che.ide.extension.machine.client.actions;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.loging.Log;

/**
 *
 * @author Artem Zatsarynnyy
 */
public class StopMachineAction extends Action {

    private final AppContext           appContext;
    private final MachineServiceClient machineServiceClient;
    private final String               workspaceId;
    private final DialogFactory        dialogFactory;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;

    @Inject
    public StopMachineAction(AppContext appContext,
                             MachineServiceClient machineServiceClient,
                             @Named("workspaceId") String workspaceId,
                             DialogFactory dialogFactory,
                             DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super("Stop Machine", "", null, null);
        this.appContext = appContext;
        this.machineServiceClient = machineServiceClient;
        this.workspaceId = workspaceId;
        this.dialogFactory = dialogFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        machineServiceClient.getMachines(
                workspaceId, null,
                new AsyncRequestCallback<Array<MachineDescriptor>>(dtoUnmarshallerFactory.newArrayUnmarshaller(MachineDescriptor.class)) {
                    @Override
                    protected void onSuccess(Array<MachineDescriptor> result) {
                        if (result.isEmpty()) {
                            dialogFactory.createMessageDialog("", "No machine is running", null).show();
                        } else {
                            for (MachineDescriptor machineDescriptor : result.asIterable()) {
                                stopMachine(machineDescriptor.getId());
                            }
                        }
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(StopMachineAction.class, exception);
                    }
                });
    }

    private void stopMachine (final String machineId) {
        machineServiceClient.destroyMachine(machineId, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                Log.info(StopMachineAction.class, "Machine " + machineId + " stopped");
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(StopMachineAction.class, exception);
            }
        });
    }
}
