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
package org.eclipse.che.ide.extension.machine.client.machine;

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
public class MachineNameManagerTest {

    private static final String MACHINE_ID   = "someText";
    private static final String MACHINE_NAME = "someText1";

    private MachineNameManager manager;

    @Before
    public void setUp() {
        manager = new MachineNameManager();
    }

    @Test
    public void nameShouldBeSaved() {
        manager.addName(MACHINE_ID, MACHINE_NAME);

        assertThat(manager.getNameById(MACHINE_ID), equalTo(MACHINE_NAME));
    }

    @Test
    public void nameShouldBeRemoved() {
        manager.addName(MACHINE_ID, MACHINE_NAME);

        assertThat(manager.getNameById(MACHINE_ID), equalTo(MACHINE_NAME));

        manager.removeName(MACHINE_ID);

        assertThat(manager.getNameById(MACHINE_ID), equalTo(""));
    }
}