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

import javax.annotation.Nonnull;

/**
 * The enum contains all available list of message's type of console.
 *
 * @author Andrey Plotnikov
 */
public enum MessageType {
    DOCKER("[DOCKER]", "#00B7EC"),
    INFO("[INFO]", "lightgreen"),
    WARNING("[WARNING]", "#FFBA00"),
    ERROR("[ERROR]", "#F62217"),
    STDOUT("[STDOUT]", "lightgreen"),
    STDERR("[STDERR]", "#F62217");

    private final String prefix;
    private final String color;

    MessageType(@Nonnull String prefix, @Nonnull String color) {
        this.prefix = prefix;
        this.color = color;
    }

    /** @return prefix of the current message type */
    @Nonnull
    public String getPrefix() {
        return prefix;
    }

    /** @return color of message type */
    @Nonnull
    public String getColor() {
        return color;
    }

    /**
     * Detect type of message by content.
     *
     * @param content
     *         content that needs to be analyzed for detecting type of message
     * @return type of message
     */
    @Nonnull
    public static MessageType detect(@Nonnull String content) {
        for (MessageType type : MessageType.values()) {
            if (content.startsWith(type.getPrefix())) {
                return type;
            }
        }

        throw new IllegalStateException("You tried to detect unknown message. Please, check your message. Your message: " + content);
    }

}