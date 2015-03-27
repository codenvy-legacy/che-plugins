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
package org.eclipse.che.ide.ext.datasource.shared.exception;

/**
 * Exception used when a database definition is incorrect.
 * 
 * @author "Mickaël Leduque"
 */
public class DatabaseDefinitionException extends Exception {

    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    public DatabaseDefinitionException() {
    }


    public DatabaseDefinitionException(final String message) {
        super(message);
    }

    public DatabaseDefinitionException(final Throwable cause) {
        super(cause);
    }

    public DatabaseDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DatabaseDefinitionException(final String message,
                                       final Throwable cause,
                                       final boolean enableSuppression,
                                       final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
