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

import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;
import com.google.inject.ImplementedBy;

import javax.validation.constraints.NotNull;

/**
 * The utility class that simplify work flow of WebSocket.
 *
 * @author Andrey Plotnikov
 */
@ImplementedBy(WebSocketUtilImpl.class)
public interface WebSocketUtil {

    /**
     * Subscribe a given handler to WebSocket. It means new messages from this chanel will be analyzed.
     *
     * @param channel
     *         channel where handler has to be subscribed
     * @param handler
     *         handler that has to analyze messages from WebSocket
     */
    void subscribeHandler(@NotNull String channel, @NotNull SubscriptionHandler handler);

    /**
     * Unsubsribe a given handler from WebSocket. It means new messages from this chanel will be not analyzed.
     *
     * @param channel
     *         channel where handler is subscribed
     * @param handler
     *         handler that analyzes messages from WebSocket
     */
    void unSubscribeHandler(@NotNull String channel, @NotNull SubscriptionHandler handler);

}