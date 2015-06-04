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

import org.eclipse.che.api.machine.shared.MachineState;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.machine.shared.dto.ServerDescriptor;

import java.util.Objects;

/**
 * The class which describes machine entity. The class is wrapper of MachineDescriptor.
 *
 * @author Dmitry Shnurenko
 */
public class Machine {

    public static final String TERMINAL_URL_KEY = "4300";

    private MachineDescriptor descriptor;

    /** @return id of current machine */
    public String getId() {
        return descriptor.getId();
    }

    /** @return state of current machine */
    public MachineState getState() {
        return descriptor.getState();
    }

    /** @return type of current machine */
    public String getType() {
        return descriptor.getType();
    }

    /** @return special url which references on terminal content. */
    public String getTerminalUrl() {
        ServerDescriptor serverDescriptor = descriptor.getServers().get(TERMINAL_URL_KEY);

        if (serverDescriptor == null) {
            return "";
        }

        return "http://" + serverDescriptor.getAddress();
    }

    /**
     * Sets descriptor to current machine.
     *
     * @param descriptor
     *         descriptor which need set
     */
    public void setDescriptor(MachineDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public boolean equals(Object machine) {
        return this == machine || !(machine == null || getClass() != machine.getClass()) && Objects.equals(descriptor.getId(),
                                                                                                           ((Machine)machine).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(descriptor.getId());
    }
}
