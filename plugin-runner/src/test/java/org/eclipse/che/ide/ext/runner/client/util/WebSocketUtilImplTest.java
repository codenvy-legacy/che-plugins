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
package org.eclipse.che.ide.ext.runner.client.util;

import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class WebSocketUtilImplTest {

    private static final String SOME_TEXT = "someText";

    @Mock
    private MessageBus          messageBus;
    @Mock
    private SubscriptionHandler handler;
    @InjectMocks
    private WebSocketUtilImpl   util;

    @Test
    public void handlerShouldBeSubscribed() throws Exception {
        util.subscribeHandler(SOME_TEXT, handler);

        verify(messageBus).subscribe(SOME_TEXT, handler);
    }

    @Test
    public void handlerShouldBeUnSubscribed() throws Exception {
        when(messageBus.isHandlerSubscribed(handler, SOME_TEXT)).thenReturn(true);

        util.unSubscribeHandler(SOME_TEXT, handler);

        verify(messageBus).unsubscribe(SOME_TEXT, handler);
    }

    @Test
    public void handlerShouldNotBeUnSubscribedIfItIsUnSubscribed() throws Exception {
        when(messageBus.isHandlerSubscribed(handler, SOME_TEXT)).thenReturn(false);

        util.unSubscribeHandler(SOME_TEXT, handler);

        verify(messageBus, never()).unsubscribe(anyString(), any(SubscriptionHandler.class));
    }

}