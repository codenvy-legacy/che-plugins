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
package org.eclipse.che.ide.ext.datasource.client.store;

import org.eclipse.che.ide.ext.datasource.shared.DatabaseDTO;

/**
 * Interface for an action to execute before storing the metadata for a datasource.
 * 
 * @author "Mickaël Leduque"
 */
public interface PreStoreProcessor {

    /**
     * Execute pre-store processing.
     * 
     * @param databaseDto the dto to process
     * @return the result of the processing
     */
    DatabaseDTO execute(DatabaseDTO databaseDto) throws PreStoreProcessorException;
}
