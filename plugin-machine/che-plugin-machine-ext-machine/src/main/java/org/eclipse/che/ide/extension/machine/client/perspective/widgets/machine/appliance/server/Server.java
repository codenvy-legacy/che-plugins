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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance.server;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;

/**
 * The class which describes entity which store name and address of current server.
 *
 * @author Dmitry Shnurenko
 */
public class Server {
    private final String name;
    private final String address;

    @Inject
    public Server(@Assisted("name") String name, @Assisted("address") String address) {
        this.name = name;
        this.address = address;
    }

    /** @return server's name */
    @Nonnull
    public String getName() {
        return name;
    }

    /** @return server's address */
    @Nonnull
    public String getAddress() {
        return address;
    }
}
