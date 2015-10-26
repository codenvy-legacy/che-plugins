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


import com.google.common.base.Strings;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper;
import org.eclipse.che.ide.MimeType;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.DeploymentConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.ImageStream;
import org.eclipse.che.ide.ext.openshift.shared.dto.ImageStreamTag;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.ext.openshift.shared.dto.ProjectRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Route;
import org.eclipse.che.ide.ext.openshift.shared.dto.Service;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.AsyncRequestLoader;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static com.google.gwt.http.client.RequestBuilder.PUT;
import static org.eclipse.che.api.promises.client.callback.PromiseHelper.newCallback;
import static org.eclipse.che.api.promises.client.callback.PromiseHelper.newPromise;
import static org.eclipse.che.ide.rest.HTTPHeader.ACCEPT;
import static org.eclipse.che.ide.rest.HTTPHeader.CONTENT_TYPE;

/**
 * @author Sergii Leschenko
 * @author Vlad Zhukovskyi
 */
public class OpenshiftServiceClientImpl implements OpenshiftServiceClient {
    private final AsyncRequestFactory    asyncRequestFactory;
    private final AsyncRequestLoader     loader;
    private final DtoUnmarshallerFactory dtoUnmarshaller;
    private final String                 openshiftPath;

    @Inject
    public OpenshiftServiceClientImpl(@Named("cheExtensionPath") String extPath,
                                      AsyncRequestFactory asyncRequestFactory,
                                      AsyncRequestLoader loader,
                                      AppContext appContext,
                                      DtoUnmarshallerFactory dtoUnmarshaller) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.loader = loader;
        this.dtoUnmarshaller = dtoUnmarshaller;
        openshiftPath = extPath + "/openshift/" + appContext.getWorkspace().getId();
    }

    public Promise<List<Template>> getTemplates(final String namespace) {
        return newPromise(new AsyncPromiseHelper.RequestCall<List<Template>>() {
            @Override
            public void makeCall(AsyncCallback<List<Template>> callback) {
                asyncRequestFactory.createGetRequest(openshiftPath + "/" + namespace + "/template")
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Getting templates...")
                                   .send(newCallback(callback, dtoUnmarshaller.newListUnmarshaller(Template.class)));
            }
        });
    }

    public Promise<Template> processTemplate(final String namespace, final Template template) {
        return newPromise(new AsyncPromiseHelper.RequestCall<Template>() {
            @Override
            public void makeCall(AsyncCallback<Template> callback) {
                asyncRequestFactory.createPostRequest(openshiftPath + "/" + namespace + "/template/process", template)
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Processing template...")
                                   .send(newCallback(callback, dtoUnmarshaller.newUnmarshaller(Template.class)));
            }
        });
    }

    @Override
    public Promise<List<Project>> getProjects() {
        return newPromise(new AsyncPromiseHelper.RequestCall<List<Project>>() {
            @Override
            public void makeCall(AsyncCallback<List<Project>> callback) {
                asyncRequestFactory.createGetRequest(openshiftPath + "/project")
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Getting projects...")
                                   .send(newCallback(callback, dtoUnmarshaller.newListUnmarshaller(Project.class)));
            }
        });
    }

    @Override
    public Promise<Project> createProject(final ProjectRequest request) {
        return newPromise(new AsyncPromiseHelper.RequestCall<Project>() {
            @Override
            public void makeCall(AsyncCallback<Project> callback) {
                asyncRequestFactory.createPostRequest(openshiftPath + "/project", request)
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Creating project...")
                                   .send(newCallback(callback, dtoUnmarshaller.newUnmarshaller(Project.class)));
            }
        });
    }

    @Override
    public Promise<BuildConfig> createBuildConfig(final BuildConfig config) {
        return newPromise(new AsyncPromiseHelper.RequestCall<BuildConfig>() {
            @Override
            public void makeCall(AsyncCallback<BuildConfig> callback) {
                asyncRequestFactory.createPostRequest(openshiftPath + "/" + config.getMetadata().getNamespace() + "/buildconfig", config)
                                   .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Creating build configs...")
                                   .send(newCallback(callback, dtoUnmarshaller.newUnmarshaller(BuildConfig.class)));
            }
        });
    }

    @Override
    public Promise<BuildConfig> updateBuildConfig(final BuildConfig config) {
        return newPromise(new AsyncPromiseHelper.RequestCall<BuildConfig>() {
            @Override
            public void makeCall(AsyncCallback<BuildConfig> callback) {
                asyncRequestFactory.createRequest(PUT, openshiftPath + "/" + config.getMetadata().getNamespace() + "/buildconfig/" +
                                                       config.getMetadata().getName(), config, false)
                                   .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Updating build configs...")
                                   .send(newCallback(callback, dtoUnmarshaller.newUnmarshaller(BuildConfig.class)));
            }
        });
    }

    @Override
    public Promise<List<BuildConfig>> getBuildConfigs(final String namespace) {
        return newPromise(new AsyncPromiseHelper.RequestCall<List<BuildConfig>>() {
            @Override
            public void makeCall(AsyncCallback<List<BuildConfig>> callback) {
                asyncRequestFactory.createGetRequest(openshiftPath + "/" + namespace + "/buildconfig")
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Getting build configs...")
                                   .send(newCallback(callback, dtoUnmarshaller.newListUnmarshaller(BuildConfig.class)));
            }
        });
    }

    @Override
    public Promise<ImageStream> createImageStream(final ImageStream stream) {
        return newPromise(new AsyncPromiseHelper.RequestCall<ImageStream>() {
            @Override
            public void makeCall(AsyncCallback<ImageStream> callback) {
                asyncRequestFactory
                        .createPostRequest(openshiftPath + "/" + stream.getMetadata().getNamespace() + "/imagestream", stream)
                        .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                        .header(ACCEPT, MimeType.APPLICATION_JSON)
                        .loader(loader, "Creating image streams...")
                        .send(newCallback(callback, dtoUnmarshaller.newUnmarshaller(ImageStream.class)));
            }
        });
    }

    @Override
    public Promise<List<ImageStream>> getImageStreams(final String namespace, final String application) {
        return newPromise(new AsyncPromiseHelper.RequestCall<List<ImageStream>>() {
            @Override
            public void makeCall(AsyncCallback<List<ImageStream>> callback) {
                final String url =
                        openshiftPath + "/" + namespace + "/imagestream" + (Strings.isNullOrEmpty(application) ? "" : ("?application=" +
                                                                                                                       application));
                asyncRequestFactory.createGetRequest(url)
                                   .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Getting image stream...")
                                   .send(newCallback(callback, dtoUnmarshaller.newListUnmarshaller(ImageStream.class)));
            }
        });
    }

    @Override
    public Promise<ImageStreamTag> getImageStreamTag(final String namespace, final String imageStream, final String tag) {
        return newPromise(new AsyncPromiseHelper.RequestCall<ImageStreamTag>() {
            @Override
            public void makeCall(AsyncCallback<ImageStreamTag> callback) {
                final String url = openshiftPath + "/" + namespace + "/imagestream/" + imageStream + "/tag/" + tag;
                asyncRequestFactory.createGetRequest(url)
                                   .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Getting image stream tag...")
                                   .send(newCallback(callback, dtoUnmarshaller.newUnmarshaller(ImageStreamTag.class)));
            }
        });
    }

    @Override
    public Promise<DeploymentConfig> createDeploymentConfig(final DeploymentConfig config) {
        return newPromise(new AsyncPromiseHelper.RequestCall<DeploymentConfig>() {
            @Override
            public void makeCall(AsyncCallback<DeploymentConfig> callback) {
                asyncRequestFactory.createPostRequest(openshiftPath + "/" + config.getMetadata().getNamespace() + "/deploymentconfig",
                                                      config)
                                   .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Creating deployment config...")
                                   .send(newCallback(callback, dtoUnmarshaller.newUnmarshaller(DeploymentConfig.class)));
            }
        });
    }

    @Override
    public Promise<Route> createRoute(final Route route) {
        return newPromise(new AsyncPromiseHelper.RequestCall<Route>() {
            @Override
            public void makeCall(AsyncCallback<Route> callback) {
                asyncRequestFactory.createPostRequest(openshiftPath + "/" + route.getMetadata().getNamespace() + "/route", route)
                                   .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Creating route...")
                                   .send(newCallback(callback, dtoUnmarshaller.newUnmarshaller(Route.class)));
            }
        });
    }

    @Override
    public Promise<Service> createService(final Service service) {
        return newPromise(new AsyncPromiseHelper.RequestCall<Service>() {
            @Override
            public void makeCall(AsyncCallback<Service> callback) {
                asyncRequestFactory.createPostRequest(openshiftPath + "/" + service.getMetadata().getNamespace() + "/service", service)
                                   .header(CONTENT_TYPE, MimeType.APPLICATION_JSON)
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Creating service...")
                                   .send(newCallback(callback, dtoUnmarshaller.newUnmarshaller(Service.class)));
            }
        });
    }

    @Override
    public Promise<List<Route>> getRoutes(final String namespace, final String application) {
        return newPromise(new AsyncPromiseHelper.RequestCall<List<Route>>() {
            @Override
            public void makeCall(AsyncCallback<List<Route>> callback) {
                String url = openshiftPath + "/" + namespace + "/route";
                if (application != null) {
                    url += "?application=" + application;
                }
                asyncRequestFactory.createGetRequest(url)
                                   .header(ACCEPT, MimeType.APPLICATION_JSON)
                                   .loader(loader, "Getting routes...")
                                   .send(newCallback(callback, dtoUnmarshaller.newListUnmarshaller(Route.class)));
            }
        });
    }
}
