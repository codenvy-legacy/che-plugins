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

import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * The class which allows us map machine id to special name.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class MachineNameManager {

    private final Map<String, String> machines;

    public MachineNameManager() {
        machines = new HashMap<>();
    }

    /**
     * The methods map machine id to special name.
     *
     * @param machineId
     *         machine id which need map to name
     * @param machineName
     *         machine name
     */
    public void addName(@Nonnull String machineId, @Nonnull String machineName) {
        machines.put(machineId, machineName);
    }

    /**
     * Removes machine name from manager using machine id.
     *
     * @param machineId
     *         machine id for which need remove name
     */
    public void removeName(@Nonnull String machineId) {
        machines.remove(machineId);
    }

    /**
     * Returns name of machine using special machine id.
     *
     * @param machineId
     *         machine id for which need get name
     * @return name of machine
     */
    @Nonnull
    public String getNameById(@Nonnull String machineId) {
        String name = machines.get(machineId);

        return name == null ? "" : name;
    }
}
