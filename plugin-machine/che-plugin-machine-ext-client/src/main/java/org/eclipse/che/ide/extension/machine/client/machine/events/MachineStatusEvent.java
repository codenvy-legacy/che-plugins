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
package org.eclipse.che.ide.extension.machine.client.machine.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event that describes the fact that machine state has been changed.
 *
 * @author Artem Zatsarynnyy
 */
public class MachineStatusEvent extends GwtEvent<MachineStatusHandler> {

    /** Type class used to register this event. */
    public static Type<MachineStatusHandler> TYPE = new Type<>();
    private final String        machineId;
    private final MachineAction machineAction;

    /**
     * Create new {@link MachineStatusEvent}.
     *
     * @param machineId
     *         machine ID
     * @param machineAction
     *         the type of action
     */
    protected MachineStatusEvent(String machineId, MachineAction machineAction) {
        this.machineId = machineId;
        this.machineAction = machineAction;
    }

    /**
     * Creates a Machine Running event.
     *
     * @param machineId
     *         running machine ID
     */
    public static MachineStatusEvent createMachineRunningEvent(String machineId) {
        return new MachineStatusEvent(machineId, MachineAction.RUNNING);
    }

    /**
     * Creates a Machine Destroyed event.
     *
     * @param machineId
     *         destroyed machine ID
     */
    public static MachineStatusEvent createMachineDestroyedEvent(String machineId) {
        return new MachineStatusEvent(machineId, MachineAction.DESTROYED);
    }

    @Override
    public Type<MachineStatusHandler> getAssociatedType() {
        return TYPE;
    }

    public String getMachineId() {
        return machineId;
    }

    /** @return the type of action */
    public MachineAction getMachineAction() {
        return machineAction;
    }

    @Override
    protected void dispatch(MachineStatusHandler handler) {
        switch (machineAction) {
            case RUNNING:
                handler.onMachineRunning(this);
                break;
            case DESTROYED:
                handler.onMachineDestroyed(this);
                break;
            default:
                break;
        }
    }

    /** Set of possible type of machine actions. */
    public enum MachineAction {
        RUNNING, DESTROYED
    }
}
