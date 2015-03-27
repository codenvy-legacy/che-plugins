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
package org.eclipse.che.ide.ext.runner.client.tabs.console.panel;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Andrey Plotnikov
 */
public class MessageTypeTest {

    private static final String SOME_TEXT = "some text";

    @Test
    public void infoMessageShouldBeDetected() throws Exception {
        String content = MessageType.INFO.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), CoreMatchers.is(MessageType.INFO));
    }

    @Test
    public void warningMessageShouldBeDetected() throws Exception {
        String content = MessageType.WARNING.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), CoreMatchers.is(MessageType.WARNING));
    }

    @Test
    public void errorMessageShouldBeDetected() throws Exception {
        String content = MessageType.ERROR.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), CoreMatchers.is(MessageType.ERROR));
    }

    @Test
    public void dockerMessageShouldBeDetected() throws Exception {
        String content = MessageType.DOCKER.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), CoreMatchers.is(MessageType.DOCKER));
    }

    @Test
    public void stdoutMessageShouldBeDetected() throws Exception {
        String content = MessageType.STDOUT.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), CoreMatchers.is(MessageType.STDOUT));
    }

    @Test
    public void stderrMessageShouldBeDetected() throws Exception {
        String content = MessageType.STDERR.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), CoreMatchers.is(MessageType.STDERR));
    }

    @Test(expected = IllegalStateException.class)
    public void exceptionShouldBeThrownWhenIncorrectValueIsInputted() throws Exception {
        MessageType.detect(SOME_TEXT);
    }

}