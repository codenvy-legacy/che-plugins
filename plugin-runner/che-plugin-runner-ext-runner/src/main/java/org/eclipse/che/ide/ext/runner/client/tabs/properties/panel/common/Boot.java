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

import javax.validation.constraints.NotNull;

/**
 * The enum represents a list of available states of booting process of a runner.
 *
 * @author Andrey Plotnikov
 */
public enum Boot {
    RUNNER_START("runner starts"), IDE_OPENS("ide opens");

    private final String name;

    Boot(@NotNull String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a value of {@link Boot} that is equaled to a given content.
     *
     * @param content
     *         content that needs to analyze
     * @return an instance {@link Boot}
     */
    public static Boot detect(@NotNull String content) {
        for (Boot boot : Boot.values()) {
            if (content.equals(boot.toString())) {
                return boot;
            }
        }

        throw new UnsupportedOperationException("You tried to detect unknown boot. Please, check your value. Your boot is " + content);
    }

}