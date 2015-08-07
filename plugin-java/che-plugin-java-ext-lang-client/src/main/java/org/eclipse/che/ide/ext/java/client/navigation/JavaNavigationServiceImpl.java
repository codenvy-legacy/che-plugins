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
package org.eclipse.che.ide.ext.java.client.navigation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.ext.java.shared.Jar;
import org.eclipse.che.ide.ext.java.shared.JarEntry;
import org.eclipse.che.ide.ext.java.shared.OpenDeclarationDescriptor;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.AsyncRequestFactory;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class JavaNavigationServiceImpl implements JavaNavigationService {

    private final String              restContext;
    private final AsyncRequestFactory requestFactory;
    private final AppContext          appContext;

    @Inject
    public JavaNavigationServiceImpl(@Named("cheExtensionPath") String restContext,
                                     AsyncRequestFactory asyncRequestFactory,
                                     AppContext appContext) {
        this.restContext = restContext;
        this.requestFactory = asyncRequestFactory;
        this.appContext = appContext;
    }

    @Override
    public void findDeclaration(String projectPath, String fqn, int offset, AsyncRequestCallback<OpenDeclarationDescriptor> callback) {
        String url = getContext() + "/jdt/navigation/find-declaration?projectpath=" + projectPath + "&fqn=" + fqn + "&offset=" + offset;
        requestFactory.createGetRequest(url).send(callback);
    }

    private String getContext() {
        if (appContext.getDevMachineId() == null) {
            throw new IllegalStateException("Developer machine ID is null. Can't create request URL");
        }
        return restContext + "/" + appContext.getDevMachineId();
    }

    public void getExternalLibraries(String projectPath, AsyncRequestCallback<Array<Jar>> callback) {
        String url = getContext() + "/jdt/navigation/libraries?projectpath=" + projectPath;
        requestFactory.createGetRequest(url).send(callback);
    }

    @Override
    public void getLibraryChildren(String projectPath, int libId, AsyncRequestCallback<Array<JarEntry>> callback) {
        String url = getContext() + "/jdt/navigation/lib/children?projectpath=" + projectPath + "&root=" + libId;
        requestFactory.createGetRequest(url).send(callback);
    }

    @Override
    public void getChildren(String projectPath, int libId, String path, AsyncRequestCallback<Array<JarEntry>> callback) {
        String url = getContext() + "/jdt/navigation/children?projectpath=" + projectPath + "&root=" + libId + "&path=" + path;
        requestFactory.createGetRequest(url).send(callback);
    }

    @Override
    public void getEntry(String projectPath, int libId, String path, AsyncRequestCallback<JarEntry> callback) {
        String url = getContext() + "/jdt/navigation/entry?projectpath=" + projectPath + "&root=" + libId + "&path=" + path;
        requestFactory.createGetRequest(url).send(callback);
    }

    @Override
    public void getContent(String projectPath, int libId, String path, AsyncRequestCallback<String> callback) {
        String url = getContentUrl(projectPath, libId, path);

        requestFactory.createGetRequest(url).send(callback);
    }

    @Override
    public String getContentUrl(String projectPath, int libId, String path) {
        return getContext() + "/jdt/navigation/content?projectpath=" + projectPath + "&root=" + libId + "&path=" + path;
    }
}
