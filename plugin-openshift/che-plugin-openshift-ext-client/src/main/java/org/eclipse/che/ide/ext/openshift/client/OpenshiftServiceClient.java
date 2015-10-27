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

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.DeploymentConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.ImageStream;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.ext.openshift.shared.dto.ProjectRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Route;
import org.eclipse.che.ide.ext.openshift.shared.dto.Service;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;

import java.util.List;

/**
 * @author Sergii Leschenko
 */
public interface OpenshiftServiceClient {
    Promise<List<Template>> getTemplates(String namespace);

    Promise<Template> processTemplate(String namespace, Template template);

    Promise<List<Project>> getProjects();

    Promise<Project> createProject(ProjectRequest request);

    Promise<BuildConfig> createBuildConfig(BuildConfig config);

    Promise<BuildConfig> updateBuildConfig(BuildConfig config);

    Promise<List<BuildConfig>> getBuildConfigs(String namespace);

    Promise<ImageStream> createImageStream(ImageStream stream);

    Promise<List<ImageStream>> getImageStreams(String namespace, String application);

    Promise<DeploymentConfig> createDeploymentConfig(DeploymentConfig config);

    Promise<Route> createRoute(Route route);

    Promise<Service> createService(Service service);

    Promise<List<Route>> getRoutes(String namespace, String application);
}
