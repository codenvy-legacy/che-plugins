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
 * Keep the link between a datasource and its database content structure. Default implementation is using it as a cache: when
 * {@link DatabaseDTO} of a datasource is retrieved from server side, {@link DatabaseDTO} is stored in memory through this class.
 */
public interface DatabaseInfoStore {

    /** Store the metadata for the given datasource id. */
    void setDatabaseInfo(String datasourceId, DatabaseDTO info);

    /**
     * Retrieve the stored metadata for the given datasource id. Null is returned is no metadata was stored.
     * 
     * @param datasourceId the id of the datasource
     * @return the previously stored metadata (or null if there weren't any)
     */
    DatabaseDTO getDatabaseInfo(String datasourceId);

    /**
     * Clears the "fetch pending" flag for the given datasource.
     * 
     * @param datasourceId the datasource id
     */
    void clearFetchPending(String datasourceId);

    /**
     * Check if there is a "fetch pending" flag for the given datasource.
     * 
     * @param datasourceId the datasource id
     * @return true iff there is a pending fetch for the datasource
     */
    boolean isFetchPending(String datasourceId);

    /**
     * Sets a "fetch pending" flag for this datasource.
     * 
     * @param datasourceId the datasource id
     */
    void setFetchPending(String datasourceId);

    /**
     * Clears the metadata for the datasource.
     * 
     * @param datasourceId the id of the datasource
     */
    void clearDatabaseInfo(String datasourceId);
}
