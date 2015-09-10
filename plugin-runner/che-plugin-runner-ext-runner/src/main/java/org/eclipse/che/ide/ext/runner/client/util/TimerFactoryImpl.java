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

import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;

import javax.validation.constraints.NotNull;

/**
 * The utility class that crate instance of Timer.
 *
 * @author Andrienko Alexader
 */
public class TimerFactoryImpl implements TimerFactory {

    @Inject
    public TimerFactoryImpl() {
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Timer newInstance(@NotNull final TimerCallBack timerCallBack) {
        return new Timer() {
            @Override
            public void run() {
                timerCallBack.onRun();
            }
        };
    }
}