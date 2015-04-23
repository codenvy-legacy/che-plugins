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
package org.eclipse.che.ide.extension.machine.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Represents the localization constants contained in resource bundle: 'MachineLocalizationConstant.properties'.
 *
 * @author Artem Zatsarynnyy
 */
public interface MachineLocalizationConstant extends Messages {

    /* Actions */
    @Key("mainMenu.machines.name")
    String mainMenuMachinesName();

    @Key("control.stopMachine.text")
    String stopMachineControlTitle();

    @Key("control.stopMachine.description")
    String stopMachineControlDescription();

    @Key("control.clearMachineConsole.text")
    String clearConsoleControlTitle();

    @Key("control.clearMachineConsole.description")
    String clearConsoleControlDescription();


    /* Messages */
    @Key("messages.noMachineIsRunning")
    String noMachineIsRunning();


    /* MachineConsoleView */
    @Key("view.machineConsole.title")
    String machineConsoleViewTitle();

    @Key("view.machineConsole.tooltip")
    String machineConsoleViewTooltip();
}
