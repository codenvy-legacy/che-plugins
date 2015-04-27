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

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.loging.Log;

/**
 * Action to destroy all the machines to which current project is bound.
 *
 * @author Artem Zatsarynnyy
 */
public class TerminateMachineAction extends Action {

    private final AppContext                  appContext;
    private final MachineLocalizationConstant localizationConstant;
    private final MachineServiceClient        machineServiceClient;
    private final String                      workspaceId;
    private final DialogFactory               dialogFactory;
    private final DtoUnmarshallerFactory      dtoUnmarshallerFactory;
    private final AnalyticsEventLogger        eventLogger;

    @Inject
    public TerminateMachineAction(AppContext appContext,
                                  MachineLocalizationConstant localizationConstant,
                                  MachineServiceClient machineServiceClient,
                                  @Named("workspaceId") String workspaceId,
                                  DialogFactory dialogFactory,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                  AnalyticsEventLogger eventLogger) {
        super(localizationConstant.terminateMachineControlTitle(), localizationConstant.terminateMachineControlDescription(), null, null);
        this.appContext = appContext;
        this.localizationConstant = localizationConstant;
        this.machineServiceClient = machineServiceClient;
        this.workspaceId = workspaceId;
        this.dialogFactory = dialogFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        machineServiceClient.getMachines(
                workspaceId, currentProject.getRootProject().getPath(),
                new AsyncRequestCallback<Array<MachineDescriptor>>(dtoUnmarshallerFactory.newArrayUnmarshaller(MachineDescriptor.class)) {
                    @Override
                    protected void onSuccess(Array<MachineDescriptor> result) {
                        if (result.isEmpty()) {
                            dialogFactory.createMessageDialog("", localizationConstant.noMachineIsRunning(), null).show();
                        } else {
                            for (MachineDescriptor machineDescriptor : result.asIterable()) {
                                destroyMachine(machineDescriptor.getId());
                            }
                        }
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(TerminateMachineAction.class, exception);
                    }
                });
    }

    private void destroyMachine(final String machineId) {
        machineServiceClient.destroyMachine(machineId, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                Log.info(TerminateMachineAction.class, "Machine " + machineId + " stopped");
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(TerminateMachineAction.class, exception);
            }
        });
    }
}
