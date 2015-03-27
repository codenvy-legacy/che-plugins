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

/**
 * Helper to work with strings
 *
 * @author Kevin Pollet
 */
public final class StringHelper {
    /**
     * Disable instantiation.
     */
    private StringHelper() {
    }

    /**
     * Return if the given string is {@code null} or empty. A {@link String} is empty if it contains only whitespaces.
     *
     * @param string
     *         the {@link String} to test.
     * @return {@code true} if the {@link String} is {@code null} or empty, {@code false} otherwise.
     */
    public static boolean isNullOrEmpty(final String string) {
        return string == null || string.trim().isEmpty();
    }
}
