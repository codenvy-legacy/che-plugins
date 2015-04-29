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
package org.eclipse.che.ide.extension.machine.client.command.configuration.gwt;

import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandType;

import javax.annotation.Nonnull;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
public class GWTCommandConfiguration implements CommandConfiguration {

    private final String      name;
    private final CommandType type;
    private       String      devModeParameters;
    private       String      vmOptions;

    public GWTCommandConfiguration(String name, CommandType type) {
        this.name = name;
        this.type = type;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public CommandType getType() {
        return type;
    }

    @Override
    public String getCommand() {
        return "java -version";
    }

    public String getDevModeParameters() {
        return devModeParameters;
    }

    public void setDevModeParameters(String devModeParameters) {
        this.devModeParameters = devModeParameters;
    }

    public String getVmOptions() {
        return vmOptions;
    }

    public void setVmOptions(String vmOptions) {
        this.vmOptions = vmOptions;
    }
}
