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
package org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Andrienko Alexander
 */
@RunWith(MockitoJUnitRunner.class)
public class LogMessageTest {
    private static final String TEXT = "some TEXT";
    private static final int    LINE = 12;

    private LogMessage logMessage;

    @Before
    public void setUp() {
        logMessage = new LogMessage(LINE, TEXT);
    }

    @Test
    public void shouldGetNumber() {
        assertThat(logMessage.getNumber(), is(LINE));
    }

    @Test
    public void shouldGetText() {
        assertThat(logMessage.getText(), is(TEXT));
    }

}