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
import com.google.inject.ImplementedBy;

import javax.validation.constraints.NotNull;

/**
 * The utility interface that create instance of Timer.
 *
 * @author Andrienko Alexander
 */
@ImplementedBy(TimerFactoryImpl.class)
public interface TimerFactory {

    /**
     * Method create and return new instance of Timer class.
     *
     * @param timerCallBack
     *         callback with actions for method run of Timer
     */
    @NotNull
    Timer newInstance(@NotNull TimerCallBack timerCallBack);

    /** Callback with actions which will be launch in method run of Timer */
    interface TimerCallBack {
        /** Method with action for method run in Timer */
        void onRun();
    }
}