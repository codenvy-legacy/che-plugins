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

import org.junit.Test;

import static org.eclipse.che.ide.ext.runner.client.TestUtil.getContentByPath;
import static org.eclipse.che.ide.ext.runner.client.tabs.console.panel.MessageType.DOCKER;
import static org.eclipse.che.ide.ext.runner.client.tabs.console.panel.MessageType.ERROR;
import static org.eclipse.che.ide.ext.runner.client.tabs.console.panel.MessageType.INFO;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Andrey Plotnikov
 */
public class MessageBuilderTest {

    private static final String SOME_TEXT  = "some text";
    private static final String OTHER_TEXT = "other text";

    @Test
    public void messageWithTypeAndContentShouldBeGenerated() throws Exception {
        String content = new MessageBuilder().type(INFO)
                                             .message(INFO.getPrefix() + ' ' + SOME_TEXT)
                                             .build()
                                             .asString();

        assertThat(content, equalTo(getContentByPath(getClass(), "MessageWithTypeAndContent.txt")));
    }

    @Test
    public void messageWithTypeAndContentShouldBeGenerated2() throws Exception {
        String content = new MessageBuilder().type(INFO)
                                             .message(INFO.getPrefix() + ' ' + SOME_TEXT)
                                             .message(INFO.getPrefix() + ' ' + OTHER_TEXT)
                                             .build()
                                             .asString();

        assertThat(content, equalTo(getContentByPath(getClass(), "MessageWithTypeAndContent2.txt")));
    }

    @Test
    public void messageWithTwoTypesAndContentShouldBeGenerated() throws Exception {
        String content = new MessageBuilder().type(DOCKER)
                                             .type(ERROR)
                                             .message(DOCKER.getPrefix() + ' ' + ERROR.getPrefix() + ' ' + SOME_TEXT)
                                             .build()
                                             .asString();

        assertThat(content, equalTo(getContentByPath(getClass(), "MessageWithTwoTypesAndContent.txt")));
    }

    @Test
    public void messageWithContentWithoutTypeShouldBeGenerated() throws Exception {
        String content = new MessageBuilder().message(SOME_TEXT)
                                             .build()
                                             .asString();

        assertThat(content, equalTo(getContentByPath(getClass(), "MessageWithoutTypeWithContent.txt")));
    }

    @Test
    public void messageWithoutTypeAndContentShouldBeGenerated() throws Exception {
        String content = new MessageBuilder().build()
                                             .asString();

        assertThat(content, equalTo(getContentByPath(getClass(), "MessageWithoutTypeAndContent.txt")));
    }

}