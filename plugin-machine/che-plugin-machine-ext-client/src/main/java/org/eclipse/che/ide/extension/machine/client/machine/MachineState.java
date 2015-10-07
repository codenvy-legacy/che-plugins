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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.core.model.machine.MachineStatus;
import org.eclipse.che.api.machine.shared.dto.MachineStateDto;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;

import java.util.Objects;

/**
 * The class which describes machine state entity. The class is wrapper of MachineStateDescriptor.
 *
 * @author Dmitry Shnurenko
 */
public class MachineState {

    private final MachineStateDto descriptor;

    private String activeTabName;

    @Inject
    public MachineState(MachineLocalizationConstant locale, @Assisted MachineStateDto descriptor) {
        this.descriptor = descriptor;

        this.activeTabName = locale.tabInfo();
    }

    /** @return id of current machine */
    public String getId() {
        return descriptor.getId();
    }

    /** @return current machine's display name */
    public String getDisplayName() {
        return descriptor.getName();
    }

    /** @return state of current machine */
    public MachineStatus getStatus() {
        return descriptor.getStatus();
    }

    /** @return type of current machine */
    public String getType() {
        return descriptor.getType();
    }

    /** @return active tab name for current machine */
    public String getActiveTabName() {
        return activeTabName;
    }

    /**
     * Sets active tab name for current machine.
     *
     * @param activeTabName
     *         tab name which need set
     */
    public void setActiveTabName(String activeTabName) {
        this.activeTabName = activeTabName;
    }


    /** @return workspace id for current machine */
    public String getWorkspaceId() {
        return descriptor.getWorkspaceId();
    }

    /**
     * Returns boolean which defines bounding workspace to current machine
     *
     * @return <code>true</code> machine is bounded to workspace,<code>false</code> machine isn't bounded to workspace
     */
    public boolean isDev() {
        return descriptor.isDev();
    }

    @Override
    public boolean equals(Object machine) {
        return this == machine || !(machine == null || getClass() != machine.getClass()) && Objects.equals(descriptor.getId(),
                                                                                                           ((MachineState)machine).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(descriptor.getId());
    }
}
