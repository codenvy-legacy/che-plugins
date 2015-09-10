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

import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * @author Andrey Plotnikov
 */
@Singleton
public class WebSocketUtilImpl implements WebSocketUtil {

    private final MessageBus messageBus;

    @Inject
    public WebSocketUtilImpl(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    /** {@inheritDoc} */
    @Override
    public void subscribeHandler(@NotNull String channel, @NotNull SubscriptionHandler handler) {
        try {
            messageBus.subscribe(channel, handler);
        } catch (WebSocketException e) {
            Log.error(getClass(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void unSubscribeHandler(@NotNull String channel, @NotNull SubscriptionHandler handler) {
        if (!messageBus.isHandlerSubscribed(handler, channel)) {
            return;
        }

        try {
            messageBus.unsubscribe(channel, handler);
        } catch (WebSocketException e) {
            Log.error(getClass(), e);
        }
    }

}