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
import java.util.Set;

/**
 * Registry for {@link CommandValueProvider}s.
 *
 * @author Artem Zatsarynnyy
 */
public interface CommandValueProviderRegistry {

    /** Returns keys of all registered {@link CommandValueProvider}s. */
    Set<String> getKeys();

    /** Returns value for the given key. */
    @Nonnull
    String getValue(String key);
}
