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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.eclipse.che.ide.ext.datasource.shared.DatabaseDTO;
import org.eclipse.che.ide.util.loging.Log;
import com.google.inject.Singleton;

/**
 * Implementation of the {@link DatabaseInfoStore} interface.
 * 
 * @author "Mickaël Leduque"
 */
@Singleton
public class DatabaseInfoStoreImpl implements DatabaseInfoStore {

    /** The database metadata cache. */
    private final Map<String, DatabaseDTO> data         = new HashMap<String, DatabaseDTO>();

    /** Per-datasource "fetch pending" flags. */
    private final HashSet<String>          fetchPending = new HashSet<>();

    /** Actions that are executed on database metadata before storing it. */
    private final Set<PreStoreProcessor>   preStoreProcessors;

    @Inject
    public DatabaseInfoStoreImpl(final @Nullable Set<PreStoreProcessor> preStoreProcessors) {
        this.preStoreProcessors = preStoreProcessors;
    }

    @Override
    public void setDatabaseInfo(final String datasourceId, final DatabaseDTO info) {
        final DatabaseDTO modifiedInfo = preStoreProcessing(info);
        this.data.put(datasourceId, modifiedInfo);
    }

    @Override
    public DatabaseDTO getDatabaseInfo(final String datasourceId) {
        return this.data.get(datasourceId);
    }

    @Override
    public void setFetchPending(final String datasourceId) {
        this.fetchPending.add(datasourceId);
    }

    @Override
    public boolean isFetchPending(final String datasourceId) {
        return this.fetchPending.contains(datasourceId);
    }

    @Override
    public void clearFetchPending(final String datasourceId) {
        this.fetchPending.remove(datasourceId);
    }

    /**
     * Execute all processors on the given database metadata. The order the processors are called in is unspecified.
     * 
     * @param databaseDTO the metadata to process
     * @return the modified metadata
     */
    private DatabaseDTO preStoreProcessing(final DatabaseDTO databaseDTO) {
        if (this.preStoreProcessors == null) {
            return databaseDTO;
        }
        DatabaseDTO modifiedDto = databaseDTO;
        for (final PreStoreProcessor processor : this.preStoreProcessors) {
            try {
                modifiedDto = processor.execute(modifiedDto);
            } catch (final PreStoreProcessorException e) {
                Log.error(DatabaseInfoStoreImpl.class, "Pre store processing error - " + e.getLocalizedMessage());
                // keep the last successfully processed dto
                break;
            }
        }
        return modifiedDto;
    }

    @Override
    public void clearDatabaseInfo(final String datasourceId) {
        this.data.remove(datasourceId);
        clearFetchPending(datasourceId);
    }
}
