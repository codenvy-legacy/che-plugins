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
package org.eclipse.che.ide.ext.runner.client.constants;

import javax.validation.constraints.Min;

/**
 * The class store Integer representation of time intervals in milliseconds.
 *
 * @author Dmitry Shnurenko
 */
public enum TimeInterval {

    ONE_SEC(1_000), FIVE_SEC(5_000), THIRTY_SEC(30_000);

    private final int timeInterval;

    TimeInterval(@Min(value=0) int timeInterval) {
        this.timeInterval = timeInterval;
    }

    /** @return time interval value. */
    @Min(value=0)
    public int getValue() {
        return timeInterval;
    }
}