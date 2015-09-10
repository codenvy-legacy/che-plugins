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

import javax.validation.constraints.NotNull;

/**
 * The Validator of the environment address.
 *
 * @author Valeriy Svydenko
 */
public class EnvironmentIdValidator {
    private static final String URL_REGEX = "\\b(project?|system):/{1,2}"
                                            + "[-A-Za-z0-9+&@#%?=~_|!:,.;]"
                                            + "*[-A-Za-z0-9+&@#/%=~_|]";

    /**
     * Checks if a field has a valid environment address.
     *
     * @param environmentId
     *         the address of environment
     * @return true if address is valid
     */
    public static boolean isValid(@NotNull String environmentId) {
        return environmentId.matches(URL_REGEX);
    }
}
