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

package org.eclipse.che.ide.ext.java.client.search;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper;
import org.eclipse.che.ide.ext.java.shared.dto.search.FindUsagesRequest;
import org.eclipse.che.ide.ext.java.shared.dto.search.FindUsagesResponse;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.loaders.requestLoader.IdeLoader;

import static org.eclipse.che.api.promises.client.callback.PromiseHelper.newCallback;
import static org.eclipse.che.api.promises.client.callback.PromiseHelper.newPromise;
import static org.eclipse.che.ide.MimeType.APPLICATION_JSON;
import static org.eclipse.che.ide.rest.HTTPHeader.CONTENT_TYPE;

/**
 * Default implementation for <code>JavaSearchService</code>
 *
 * @author Evgen Vidolob
 */
@Singleton
class JavaSearchServiceImpl implements JavaSearchService {

    private final AsyncRequestFactory    asyncRequestFactory;
    private final DtoUnmarshallerFactory unmarshallerFactory;
    private IdeLoader loader;
    private final String                 pathToService;

    @Inject
    public JavaSearchServiceImpl(AsyncRequestFactory asyncRequestFactory,
                                 DtoUnmarshallerFactory unmarshallerFactory,
                                 IdeLoader loader,
                                 @Named("cheExtensionPath") String extPath,
                                 @Named("workspaceId") String workspaceId) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.unmarshallerFactory = unmarshallerFactory;
        this.loader = loader;
        this.pathToService = extPath + "/jdt/" + workspaceId + "/search/";
    }

    @Override
    public Promise<FindUsagesResponse> findUsages(final FindUsagesRequest request) {
        return newPromise(new AsyncPromiseHelper.RequestCall<FindUsagesResponse>() {
            @Override
            public void makeCall(AsyncCallback<FindUsagesResponse> callback) {

                asyncRequestFactory.createPostRequest(pathToService + "find/usages", request)
                                   .header(CONTENT_TYPE, APPLICATION_JSON)
                                   .loader(loader)
                                   .send(newCallback(callback, unmarshallerFactory.newUnmarshaller(FindUsagesResponse.class)));
            }
        });
    }
}
