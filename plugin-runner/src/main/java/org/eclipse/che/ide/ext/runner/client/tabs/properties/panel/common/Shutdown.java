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
package org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common;

import javax.annotation.Nonnull;

/**
 * The enum represents a list of available states of shutdowning process of a runner.
 *
 * @author Andrey Plotnikov
 */
public enum Shutdown {
    BY_TIMEOUT("by timeout"), ALWAYS_ON("always on");

    private final String name;

    Shutdown(@Nonnull String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a value of {@link Shutdown} that is equaled to a given content.
     *
     * @param content
     *         content that needs to analyze
     * @return an instance {@link Shutdown}
     */
    public static Shutdown detect(@Nonnull String content) {
        for (Shutdown shutdown : Shutdown.values()) {
            if (content.equals(shutdown.toString())) {
                return shutdown;
            }
        }

        throw new UnsupportedOperationException(
                "You tried to detect unknown shutdown. Please, check your value. Your shutdown is " + content);
    }

}