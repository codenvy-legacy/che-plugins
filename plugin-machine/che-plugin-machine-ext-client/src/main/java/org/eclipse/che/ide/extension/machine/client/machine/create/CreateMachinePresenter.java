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
package org.eclipse.che.ide.extension.machine.client.machine.create;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;

/**
 * Presenter for creating machine.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CreateMachinePresenter implements CreateMachineView.ActionDelegate {

    private final CreateMachineView view;
    private final MachineManager    machineManager;

    @Inject
    public CreateMachinePresenter(CreateMachineView view, MachineManager machineManager) {
        this.view = view;
        this.machineManager = machineManager;

        view.setDelegate(this);
    }

    public void showDialog() {
        view.show();
    }

    @Override
    public void onNameChanged() {
        view.setCreateButtonState(!view.getMachineName().isEmpty());
        view.setReplaceButtonState(!view.getMachineName().isEmpty());
    }

    @Override
    public void onCreateClicked() {
        final String machineName = view.getMachineName();
        machineManager.startMachine(machineName);

        view.close();
    }

    @Override
    public void onReplaceDevMachineClicked() {
        final String machineName = view.getMachineName();
        machineManager.startAndBindMachine(machineName);

        view.close();
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }
}
