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
package org.eclipse.che.ide.ext.runner.client.callbacks;

import javax.validation.constraints.NotNull;

/**
 * Class which describes methods to receive a failure response from a remote procedure call.
 *
 * @author Evgen Vidolob
 * @author Dmitry Shnurenko
 */
public interface FailureCallback {
    /**
     * Performs some actions when callback was failed.
     *
     * @param reason
     *         exception which was thrown
     */
    void onFailure(@NotNull Throwable reason);
}