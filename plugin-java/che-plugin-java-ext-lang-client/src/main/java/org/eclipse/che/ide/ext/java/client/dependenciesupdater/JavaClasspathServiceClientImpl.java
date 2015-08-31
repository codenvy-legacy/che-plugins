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
package org.eclipse.che.ide.ext.java.client.dependenciesupdater;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.AsyncRequestFactory;

import java.util.List;

import static org.eclipse.che.ide.MimeType.APPLICATION_JSON;
import static org.eclipse.che.ide.rest.HTTPHeader.ACCEPT;

/**
 * Implementation of {@link JavaClasspathServiceClient}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class JavaClasspathServiceClientImpl implements JavaClasspathServiceClient {

    private final AsyncRequestFactory asyncRequestFactory;
    private final String              extPath;
    private final String              workspaceId;

    @Inject
    protected JavaClasspathServiceClientImpl(AsyncRequestFactory asyncRequestFactory,
                                             @Named("cheExtensionPath") String extPath,
                                             @Named("workspaceId") String workspaceId) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.extPath = extPath;
        this.workspaceId = workspaceId;
    }

    @Override
    public void updateDependencies(String projectPath, boolean force, AsyncRequestCallback<Boolean> callback) {
        final String requestUrl = extPath + "/jdt/" + workspaceId + "/classpath/update?projectpath=" + projectPath;
        asyncRequestFactory.createGetRequest(requestUrl)
                           .header(ACCEPT, APPLICATION_JSON)
                           .send(callback);
    }

    @Override
    public void getClasspath(String projectPath, AsyncRequestCallback<List<String>> callback) {
        final String requestUrl = extPath + "/jdt/" + workspaceId + "/classpath?projectpath=" + projectPath;
        asyncRequestFactory.createGetRequest(requestUrl)
                           .header(ACCEPT, APPLICATION_JSON)
                           .send(callback);
    }
}
