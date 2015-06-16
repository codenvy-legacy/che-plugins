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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ServerTest {

    private static final String NAME    = "name";
    private static final String ADDRESS = "address";

    private Server server;

    @Before
    public void setUp() {
        server = new Server(NAME, ADDRESS);
    }

    @Test
    public void nameShouldBeReturned() {
        assertThat(server.getName(), equalTo(NAME));
    }

    @Test
    public void addressShouldBeReturned() {
        assertThat(server.getAddress(), equalTo(ADDRESS));
    }

}