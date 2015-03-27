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
package org.eclipse.che.ide.ext.bitbucket.shared;

import javax.annotation.Nonnull;

/**
 * Ensure parameter preconditions.
 *
 * @author Kevin Pollet
 */
public final class Preconditions {
    /**
     * Disable instantiation.
     */
    private Preconditions() {
    }

    /**
     * Checks that the given expression is {@code true}.
     *
     * @param expression
     *         the expression.
     * @param parameterName
     *         the parameter name, cannot be {@code null}.
     * @throws IllegalArgumentException
     *         if the given expression is {@code false}.
     */
    public static void checkArgument(final boolean expression, @Nonnull final String parameterName) throws IllegalArgumentException {
        if (!expression) {
            throw new IllegalArgumentException("'" + parameterName + "' parameter is not valid");
        }
    }
}
