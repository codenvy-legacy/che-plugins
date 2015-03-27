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
package org.eclipse.che.ide.ext.runner.client.constants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.eclipse.che.ide.ext.runner.client.constants.ActionId.CHOOSE_RUNNER_ID;
import static org.eclipse.che.ide.ext.runner.client.constants.ActionId.RUN_APP_ID;
import static org.eclipse.che.ide.ext.runner.client.constants.ActionId.RUN_WITH;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Andrienko Alexander
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionIdTest {
    @Test
    public void timeIntervalShouldBeReturned1() {
        assertThat(RUN_APP_ID.getId(), is("runApp"));
    }

    @Test
    public void timeIntervalShouldBeReturned2() {
        assertThat(CHOOSE_RUNNER_ID.getId(), is("chooseRunner"));
    }

    @Test
    public void timeIntervalShouldBeReturned3() {
        assertThat(RUN_WITH.getId(), is("runWith"));
    }
}