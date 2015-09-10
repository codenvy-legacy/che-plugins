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
package org.eclipse.che.ide.ext.runner.client.tabs.container.tab;

import javax.validation.constraints.NotNull;

/**
 * Enum contains values of background color.
 *
 * @author Dmitry Shnurenko
 */
public enum Background {
    BLACK("#313335"), GREY("#474747"), BLUE("#256c9f");

    private final String color;

    Background(@NotNull String color) {
        this.color = color;
    }

    /** @return value of background color */
    @Override
    @NotNull
    public String toString() {
        return color;
    }
}