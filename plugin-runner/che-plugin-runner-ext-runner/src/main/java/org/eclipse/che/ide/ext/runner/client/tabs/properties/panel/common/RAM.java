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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Enums which store information about memory size.
 *
 * @author Dmitry Shnurenko
 */
public enum RAM {
    MB_100(100),
    MB_200(200),
    MB_500(500),
    MB_1000(1000),
    MB_2000(2000),
    MB_4000(4000),
    MB_8000(8000),
    DEFAULT(1000);

    private final int size;

    RAM(int size) {
        this.size = size;
    }

    /** @return integer value of enum. */
    public int getValue() {
        return size;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String toString() {
        return size + " mb";
    }

    /**
     * Returns an instance of {@link RAM} using special memory string value.
     *
     * @param inputMemory
     *         value of string for which need return {@link RAM} enum
     * @return an instance {@link RAM}
     */
    @NotNull
    public static RAM detect(@NotNull String inputMemory) {
        for (RAM size : RAM.values()) {
            if (inputMemory.equals(size.toString())) {
                return size;
            }
        }

        return DEFAULT;
    }

    /**
     * Returns an instance of {@link RAM} using integer value.
     *
     * @param value
     *         value of integer for which need return {@link RAM} enum
     * @return an instance {@link RAM}
     */
    @NotNull
    public static RAM detect(@Min(value=0) int value) {
        for (RAM size : RAM.values()) {
            if (size.getValue() == value) {
                return size;
            }
        }

        return DEFAULT;
    }

}