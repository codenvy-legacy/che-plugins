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
package org.eclipse.che.ide.ext.runner.client.inject.factories;

import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.common.LogMessagesHandler;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.common.LogMessagesHandler.ErrorHandler;

import javax.validation.constraints.NotNull;

/**
 * The factory for creating an instances of different handlers.
 *
 * @author Andrey Plotnikov
 */
public interface HandlerFactory {
    /**
     * Creates a handler for a given runner. This handler provides an ability to analyze received console message.
     *
     * @param runner
     *         runner that needs to be bound with a handler
     * @param errorHandler
     *         handler that delegate actions which need to perform when error happened
     * @return an instance of {@link LogMessagesHandler}
     */
    @NotNull
    LogMessagesHandler createLogMessageHandler(@NotNull Runner runner, @NotNull ErrorHandler errorHandler);
}