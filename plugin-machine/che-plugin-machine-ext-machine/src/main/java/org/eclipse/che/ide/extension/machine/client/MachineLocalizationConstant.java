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
    @Key("mainMenu.run.name")
    String mainMenuRunName();

    @Key("control.terminateMachine.text")
    String terminateMachineControlTitle();

    @Key("control.terminateMachine.description")
    String terminateMachineControlDescription();

    @Key("control.executeCommand.text")
    String executeCommandControlTitle();

    @Key("control.executeCommand.description")
    String executeCommandControlDescription();

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


    /* ExecuteCommandView */
    @Key("view.executeCommand.title")
    String executeCommandViewTitle();

    @Key("view.executeCommand.execute")
    String executeCommandViewExecute();

    @Key("view.executeCommand.cancel")
    String executeCommandViewCancel();
}
