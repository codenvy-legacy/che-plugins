/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package org.eclipse.che.plugin.docker.machine.ext.provider;

import org.eclipse.che.api.machine.server.WsAgentLauncherImpl;
import org.eclipse.che.plugin.docker.machine.ServerConf;

import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Provides server conf that describes workspace agent server
 *
 * @author Alexander Garagatyi
 */
@Singleton
public class WsAgentServerConfProvider implements Provider<ServerConf> {
    public static final String WS_AGENT_SERVER_REFERENCE = "extensions";

    @Override
    public ServerConf get() {
        return new ServerConf(WS_AGENT_SERVER_REFERENCE, Integer.toString(WsAgentLauncherImpl.WS_AGENT_PORT), "http");
    }
}
