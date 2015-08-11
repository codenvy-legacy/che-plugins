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
package org.eclipse.che.ide.extension.machine.client.command.valueproviders;

import javax.annotation.Nonnull;

/**
 * Provides a value of some variable that may be used in commands.
 * <p/>
 * Actual value will be substituted before sending command for execution to the server.
 *
 * @author Artem Zatsarynnyy
 */
public interface CommandValueProvider {

    /** Get key. Key should be like $(key.name). */
    @Nonnull
    String getKey();

    /** Get value. */
    @Nonnull
    String getValue();
}
