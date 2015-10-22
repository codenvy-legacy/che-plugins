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

import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.DeploymentConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.ImageStream;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.ext.openshift.shared.dto.ProjectRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Route;
import org.eclipse.che.ide.ext.openshift.shared.dto.Service;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;
import org.eclipse.che.ide.rest.AsyncRequestCallback;

import java.util.List;

/**
 * @author Sergii Leschenko
 */
public interface OpenshiftServiceClient {
    void getTemplates(String namespace, AsyncRequestCallback<List<Template>> callback);

    void processTemplate(String namespace, Template template, AsyncRequestCallback<Template> callback);


    void getProjects(AsyncRequestCallback<List<Project>> callback);

    void createProject(ProjectRequest projectRequest, AsyncRequestCallback<Project> callback);


    void createBuildConfig(BuildConfig buildConfig, AsyncRequestCallback<BuildConfig> callback);

    void updateBuildConfig(BuildConfig buildConfig, AsyncRequestCallback<BuildConfig> callback);

    void getBuildConfigs(String namespace, AsyncRequestCallback<List<BuildConfig>> callback);

    void createImageStream(ImageStream imageStream, AsyncRequestCallback<ImageStream> callback);

    void createDeploymentConfig(DeploymentConfig deploymentConfig, AsyncRequestCallback<DeploymentConfig> callback);

    void createRoute(Route route, AsyncRequestCallback<Route> callback);

    void createService(Service service, AsyncRequestCallback<Service> callback);

    void getRoutes(String namespace, String application, AsyncRequestCallback<List<Route>> callback);
}
