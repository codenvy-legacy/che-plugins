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
 * The class contains values of tabs size
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public enum TabType {
    LEFT("21px", "70px"),
    RIGHT("20px", "70px");

    private final String height;
    private final String width;

    TabType(@NotNull String height, @NotNull String width) {
        this.height = height;
        this.width = width;
    }

    /** @return string value of height. */
    @NotNull
    public String getHeight() {
        return height;
    }

    /** @return string value of width. */
    @NotNull
    public String getWidth() {
        return width;
    }
}