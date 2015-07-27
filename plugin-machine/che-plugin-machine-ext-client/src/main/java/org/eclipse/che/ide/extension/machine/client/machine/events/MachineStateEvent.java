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

import org.eclipse.che.ide.extension.machine.client.machine.Machine;

/**
 * Event that describes the fact that machine state has been changed.
 *
 * @author Artem Zatsarynnyy
 */
public class MachineStateEvent extends GwtEvent<MachineStateHandler> {

    /** Type class used to register this event. */
    public static Type<MachineStateHandler> TYPE = new Type<>();
    private final Machine       machine;
    private final MachineAction machineAction;

    /**
     * Create new {@link MachineStateEvent}.
     *
     * @param machine
     *         machine
     * @param machineAction
     *         the type of action
     */
    protected MachineStateEvent(Machine machine, MachineAction machineAction) {
        this.machine = machine;
        this.machineAction = machineAction;
    }

    /**
     * Creates a Machine Running event.
     *
     * @param machine
     *         running machine
     */
    public static MachineStateEvent createMachineRunningEvent(Machine machine) {
        return new MachineStateEvent(machine, MachineAction.RUNNING);
    }

    /**
     * Creates a Machine Destroyed event.
     *
     * @param machine
     *         destroyed machine
     */
    public static MachineStateEvent createMachineDestroyedEvent(Machine machine) {
        return new MachineStateEvent(machine, MachineAction.DESTROYED);
    }

    @Override
    public Type<MachineStateHandler> getAssociatedType() {
        return TYPE;
    }

    public Machine getMachine() {
        return machine;
    }

    /** @return the type of action */
    public MachineAction getMachineAction() {
        return machineAction;
    }

    @Override
    protected void dispatch(MachineStateHandler handler) {
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
