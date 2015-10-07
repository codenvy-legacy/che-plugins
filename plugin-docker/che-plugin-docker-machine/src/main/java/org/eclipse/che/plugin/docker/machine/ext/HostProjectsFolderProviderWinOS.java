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
package org.eclipse.che.plugin.docker.machine.ext;

import com.google.inject.Provider;

/**
 * Provide path to the project folder on hosted machine under windows
 * MUST be locate in "user.home" directory in case limitation windows+docker
 *
 * @author Vitalii Parfonov
 */
public class HostProjectsFolderProviderWinOS implements Provider<String> {


    @Override
    public String get() {
        return System.getProperty("user.home") + "\\che\\projects";
    }
}
