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
package org.eclipse.che.ide.ext.openshift.client;

import org.eclipse.che.ide.MimeType;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.DeploymentConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.ImageStream;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.ext.openshift.shared.dto.ProjectRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Route;
import org.eclipse.che.ide.ext.openshift.shared.dto.Service;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.AsyncRequestLoader;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static org.eclipse.che.ide.rest.HTTPHeader.ACCEPT;
import static org.eclipse.che.ide.rest.HTTPHeader.CONTENT_TYPE;

/**
 * @author Sergii Leschenko
 */
public class OpenshiftServiceClientImpl implements OpenshiftServiceClient {
    private final AsyncRequestFactory asyncRequestFactory;
    private final AsyncRequestLoader  loader;
    private final String              openshiftPath;

    @Inject
    public OpenshiftServiceClientImpl(@Named("cheExtensionPath") String extPath,
                                      AsyncRequestFactory asyncRequestFactory,
                                      AsyncRequestLoader loader,
                                      AppContext appContext) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.loader = loader;
        this.openshiftPath = extPath + "/openshift/" + appContext.getWorkspace().getId();
    }

    @Override
    public void getTemplates(String namespace, AsyncRequestCallback<List<Template>> callback) {
        asyncRequestFactory.createGetRequest(openshiftPath + "/" + namespace + "/template")
                           .header(ACCEPT, MimeType.APPLICATION_JSON)
                           .loader(loader, "Getting templates...")
                           .send(callback);
    }

    @Override
    public void processTemplate(String namespace, Template template, AsyncRequestCallback<Template> callback) {
        asyncRequestFactory.createPostRequest(openshiftPath + "/" + namespace + "/template/process", template)
                           .header(ACCEPT, MimeType.APPLICATION_JSON)
                           .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                           .loader(loader, "Processing template...")
                           .send(callback);
    }

    @Override
    public void getProjects(AsyncRequestCallback<List<Project>> callback) {
        asyncRequestFactory.createGetRequest(openshiftPath + "/project")
                           .header(ACCEPT, MimeType.APPLICATION_JSON)
                           .loader(loader, "Getting projects...")
                           .send(callback);
    }

    @Override
    public void createProject(ProjectRequest projectRequest, AsyncRequestCallback<Project> callback) {
        asyncRequestFactory.createPostRequest(openshiftPath + "/project", projectRequest)
                           .header(ACCEPT, MimeType.APPLICATION_JSON)
                           .loader(loader, "Creating project...")
                           .send(callback);
    }

    @Override
    public void createBuildConfig(BuildConfig buildConfig, AsyncRequestCallback<BuildConfig> callback) {
        asyncRequestFactory.createPostRequest(openshiftPath + "/" +  buildConfig.getMetadata().getNamespace() + "/buildconfig", buildConfig)
                           .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                           .header(ACCEPT, MimeType.APPLICATION_JSON)
                           .loader(loader, "Creating build configs...")
                           .send(callback);
    }

    @Override
    public void createImageStream(ImageStream imageStream, AsyncRequestCallback<ImageStream> callback) {
        asyncRequestFactory.createPostRequest(openshiftPath + "/" +  imageStream.getMetadata().getNamespace() +  "/imagestream", imageStream)
                           .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                           .header(ACCEPT, MimeType.APPLICATION_JSON)
                           .loader(loader, "Creating image streams...")
                           .send(callback);
    }

    @Override
    public void createDeploymentConfig(DeploymentConfig deploymentConfig, AsyncRequestCallback<DeploymentConfig> callback) {
        asyncRequestFactory.createPostRequest(openshiftPath + "/" +  deploymentConfig.getMetadata().getNamespace() +  "/deploymentconfig", deploymentConfig)
                           .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                           .header(ACCEPT, MimeType.APPLICATION_JSON)
                           .loader(loader, "Creating deployment config...")
                           .send(callback);
    }

    @Override
    public void createRoute(Route route, AsyncRequestCallback<Route> callback) {
        asyncRequestFactory.createPostRequest(openshiftPath + "/" +  route.getMetadata().getNamespace() +  "/route", route)
                           .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                           .header(ACCEPT, MimeType.APPLICATION_JSON)
                           .loader(loader, "Creating route...")
                           .send(callback);
    }

    @Override
    public void createService(Service service, AsyncRequestCallback<Service> callback) {
        asyncRequestFactory.createPostRequest(openshiftPath + "/" +  service.getMetadata().getNamespace() +  "/service", service)
                           .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                           .header(ACCEPT, MimeType.APPLICATION_JSON)
                           .loader(loader, "Creating service...")
                           .send(callback);
    }
}
