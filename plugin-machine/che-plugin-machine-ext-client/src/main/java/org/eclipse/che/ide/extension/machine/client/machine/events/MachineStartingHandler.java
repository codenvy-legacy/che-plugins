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
 * Special handler which handles starting machine processes.
 *
 * @author Dmitry Shnurenko
 */
public interface MachineStartingHandler extends EventHandler {

    /**
     * Performs some actions when machine is starting.
     *
     * @param event
     *         event which contains information about starting machine
     */
    void onMachineStarting(MachineStartingEvent event);
}
