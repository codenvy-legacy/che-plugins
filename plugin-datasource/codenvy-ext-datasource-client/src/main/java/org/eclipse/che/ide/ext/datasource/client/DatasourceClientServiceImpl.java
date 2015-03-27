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

import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.datasource.client.inject.DatasourceGinModule;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import org.eclipse.che.ide.ext.datasource.shared.ExploreRequestDTO;
import org.eclipse.che.ide.ext.datasource.shared.ExploreTableType;
import org.eclipse.che.ide.ext.datasource.shared.MultipleRequestExecutionMode;
import org.eclipse.che.ide.ext.datasource.shared.RequestParameterDTO;
import org.eclipse.che.ide.ext.datasource.shared.ServicePaths;
import org.eclipse.che.ide.ext.datasource.shared.TextDTO;
import org.eclipse.che.ide.ext.datasource.shared.request.RequestResultDTO;
import org.eclipse.che.ide.rest.AsyncRequest;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Implementation (REST) for the datasource server services client interface.
 */
@Singleton
public class DatasourceClientServiceImpl implements DatasourceClientService {

    private final String              restServiceContext;
    private final DtoFactory          dtoFactory;
    private final AsyncRequestFactory asyncRequestFactory;

    /**
     * @param restContext rest context
     * @param loader loader to show on server request
     */
    @Inject
    protected DatasourceClientServiceImpl(final @Named(DatasourceGinModule.DATASOURCE_CONTEXT_NAME) String restContext,
                                          final DtoFactory dtoFactory,
                                          final AsyncRequestFactory asyncRequestFactory) {
        restServiceContext = restContext;
        this.dtoFactory = dtoFactory;
        this.asyncRequestFactory = asyncRequestFactory;
    }

    @Override
    public void fetchDatabaseInfo(final @NotNull DatabaseConfigurationDTO configuration,
                                  final ExploreTableType tableCategory,
                                  final @NotNull AsyncRequestCallback<String> asyncRequestCallback) throws RequestException {
        final String url = formatUrl(restServiceContext, ServicePaths.DATABASE_EXPLORE_PATH, "", null);
        ExploreRequestDTO request = dtoFactory.createDto(ExploreRequestDTO.class);
        request.setDatasourceConfig(configuration);
        request.setExploreTableType(tableCategory);
        final AsyncRequest postRequest = asyncRequestFactory.createPostRequest(url, request, false);
        postRequest.send(asyncRequestCallback);
    }

    @Override
    public void fetchDatabaseInfo(final @NotNull DatabaseConfigurationDTO configuration,
                                  final @NotNull AsyncRequestCallback<String> asyncRequestCallback) throws RequestException {
        fetchDatabaseInfo(configuration, ExploreTableType.STANDARD, asyncRequestCallback);
    }

    @Override
    public void executeSqlRequest(final DatabaseConfigurationDTO configuration,
                                  final int resultLimit,
                                  final String sqlRequest,
                                  final MultipleRequestExecutionMode execMode,
                                  final AsyncRequestCallback<String> asyncRequestCallback)
                                                                                          throws RequestException {
        final String url = formatUrl(restServiceContext, ServicePaths.EXECUTE_SQL_REQUEST_PATH, "", null);
        final RequestParameterDTO requestParameterDTO = dtoFactory.createDto(RequestParameterDTO.class)
                                                                  .withDatabase(configuration)
                                                                  .withResultLimit(resultLimit)
                                                                  .withSqlRequest(sqlRequest)
                                                                  .withMultipleRequestExecutionMode(execMode);
        final AsyncRequest postRequest = asyncRequestFactory.createPostRequest(url, requestParameterDTO, false);
        postRequest.send(asyncRequestCallback);
    }

    @Override
    public void getAvailableDrivers(AsyncRequestCallback<String> asyncRequestCallback) throws RequestException {
        String url = formatUrl(restServiceContext, ServicePaths.DATABASE_TYPES_PATH, "", null);
        final AsyncRequest getRequest = asyncRequestFactory.createGetRequest(url, false);
        getRequest.send(asyncRequestCallback);
    }

    @Override
    public String getRestServiceContext() {
        return restServiceContext;
    }

    @Override
    public void exportAsCsv(final RequestResultDTO requestResult,
                            final AsyncRequestCallback<String> asyncRequestCallback) throws RequestException {
        String url = formatUrl(restServiceContext, ServicePaths.RESULT_CSV_PATH, "", null);
        AsyncRequest postRequest = asyncRequestFactory.createPostRequest(url, requestResult, false);
        postRequest.send(asyncRequestCallback);
    }

    @Override
    public void testDatabaseConnectivity(final @NotNull DatabaseConfigurationDTO configuration,
                                         final @NotNull AsyncRequestCallback<String> asyncRequestCallback) throws RequestException {
        String url = formatUrl(restServiceContext, ServicePaths.TEST_DATABASE_CONNECTIVITY_PATH, "", null);
        final AsyncRequest postRequest = asyncRequestFactory.createPostRequest(url, configuration, false);
        postRequest.send(asyncRequestCallback);
    }

    @Override
    public void encryptText(final String textToEncrypt,
                            final AsyncRequestCallback<String> asyncRequestCallback) throws RequestException {
        TextDTO textDTO = dtoFactory.createDto(TextDTO.class).withValue(textToEncrypt);
        String url = formatUrl(restServiceContext, ServicePaths.ENCRYPT_TEXT_PATH, "", null);
        AsyncRequest postRequest = asyncRequestFactory.createPostRequest(url, textDTO, false);
        postRequest.send(asyncRequestCallback);
    }

    /**
     * Builds the target REST service url.
     *
     * @param context the rest context
     * @param root the root of the service
     * @param service the rest service
     * @param param the parameters
     * @return the url
     */
    private String formatUrl(final String context, final String root, final String service, final String param) {
        StringBuilder sb = new StringBuilder(context);
        sb.append("/")
          .append(root);
        if (service != null && !service.isEmpty()) {
            sb.append("/")
              .append(service);
        }

        if (param != null) {
            sb.append('/')
              .append(param);
        }
        Log.debug(DatasourceClientServiceImpl.class, "Create REST URL : " + sb.toString());
        return sb.toString();
    }
}
