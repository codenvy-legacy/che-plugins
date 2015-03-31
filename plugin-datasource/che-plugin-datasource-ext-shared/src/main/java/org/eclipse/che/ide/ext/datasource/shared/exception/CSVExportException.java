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
 * Exception for CSV export.
 * 
 * @author "Mickaël Leduque"
 */
public class CSVExportException extends Exception {

    /** UID for serialization */
    private static final long serialVersionUID = 1L;

    public CSVExportException() {
    }

    public CSVExportException(final String message) {
        super(message);
    }

    public CSVExportException(final Throwable cause) {
        super(cause);
    }

    public CSVExportException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CSVExportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

}
