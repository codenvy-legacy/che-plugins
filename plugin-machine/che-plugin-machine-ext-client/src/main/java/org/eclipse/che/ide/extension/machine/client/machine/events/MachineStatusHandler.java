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

import com.google.gwt.event.shared.EventHandler;

/**
 * A handler for handling {@link MachineStatusEvent}.
 *
 * @author Artem Zatsarynnyy
 */
public interface MachineStatusHandler extends EventHandler {

    /**
     * Called when machine has been run.
     *
     * @param event
     *         the fired {@link MachineStatusEvent}
     */
    void onMachineRunning(MachineStatusEvent event);

    /**
     * Called when machine has been destroyed.
     *
     * @param event
     *         the fired {@link MachineStatusEvent}
     */
    void onMachineDestroyed(MachineStatusEvent event);
}
