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

import javax.validation.constraints.Min;

/**
 * The enum that contains list of constant values of console's lines.
 *
 * @author Andrey Plotnikov
 */
public enum Lines {
    MAXIMUM(1_000), CLEANED(100);

    private final int value;

    Lines(@Min(value = 0) int value) {
        this.value = value;
    }

    /** @return line's count */
    @Min(value = 0)
    public int getValue() {
        return value;
    }
}