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

import static org.eclipse.che.ide.ext.runner.client.constants.TimeInterval.ONE_SEC;
import static org.eclipse.che.ide.ext.runner.client.constants.TimeInterval.FIVE_SEC;
import static org.eclipse.che.ide.ext.runner.client.constants.TimeInterval.THIRTY_SEC;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Andrienko Alexander
 */
@RunWith(MockitoJUnitRunner.class)
public class TimeIntervalTest {
    @Test
    public void timeIntervalShouldBeReturned1() {
        assertThat(ONE_SEC.getValue(), is(1_000));
    }

    @Test
    public void timeIntervalShouldBeReturned2() {
        assertThat(FIVE_SEC.getValue(), is(5_000));
    }

    @Test
    public void timeIntervalShouldBeReturned3() {
        assertThat(THIRTY_SEC.getValue(), is(30_000));
    }
}