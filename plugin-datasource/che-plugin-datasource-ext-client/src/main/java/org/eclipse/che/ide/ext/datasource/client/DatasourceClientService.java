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
package org.eclipse.che.ide.ext.datasource.client;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import org.eclipse.che.ide.ext.datasource.shared.ExploreTableType;
import org.eclipse.che.ide.ext.datasource.shared.MultipleRequestExecutionMode;
import org.eclipse.che.ide.ext.datasource.shared.request.RequestResultDTO;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client interface for the datasource plugin server services.
 */
public interface DatasourceClientService {

    void fetchDatabaseInfo(@NotNull DatabaseConfigurationDTO configuration,
                           @NotNull AsyncRequestCallback<String> asyncRequestCallback) throws RequestException;

    void fetchDatabaseInfo(@NotNull DatabaseConfigurationDTO configuration,
                           ExploreTableType tableCategory,
                           @NotNull AsyncRequestCallback<String> asyncRequestCallback) throws RequestException;

    void executeSqlRequest(@NotNull DatabaseConfigurationDTO configuration,
                           int resultLimit,
                           @NotNull String sqlRequest,
                           MultipleRequestExecutionMode execmode,
                           @NotNull AsyncRequestCallback<String> asyncRequestCallback) throws RequestException;

    void getAvailableDrivers(@NotNull AsyncRequestCallback<String> asyncRequestCallback) throws RequestException;

    String getRestServiceContext();

    void exportAsCsv(final RequestResultDTO requestResult,
                     final AsyncRequestCallback<String> asyncRequestCallback) throws RequestException;

    void testDatabaseConnectivity(@NotNull DatabaseConfigurationDTO configuration,
                                  @NotNull AsyncRequestCallback<String> asyncRequestCallback) throws RequestException;

    void encryptText(@NotNull String textToEncrypt,
                     @NotNull AsyncRequestCallback<String> asyncRequestCallback) throws RequestException;

}
