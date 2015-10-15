/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors:
 * Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.openshift.client.project.wizard;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper.RequestCall;
import org.eclipse.che.api.promises.client.js.JsPromiseError;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.wizard.AbstractWizard;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.client.dto.NewApplicationRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.DeploymentConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.ImageStream;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.ext.openshift.shared.dto.ProjectRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Route;
import org.eclipse.che.ide.ext.openshift.shared.dto.Service;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;
import org.eclipse.che.ide.projectimport.wizard.ImportWizardFactory;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;

import javax.validation.constraints.NotNull;

import static org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper.createFromAsyncRequest;

/**
 * Complete wizard handler. Handle create operation and tries to create configs on openshift.
 *
 * @author Vlad Zhukovskiy
 */
public class CreateProjectWizard extends AbstractWizard<NewApplicationRequest> {

    private final OpenshiftServiceClient openshiftClient;
    private final DtoUnmarshallerFactory dtoUnmarshaller;
    private final DtoFactory             dtoFactory;
    private final ImportWizardFactory importWizardFactory;

    @Inject
    public CreateProjectWizard(@Assisted NewApplicationRequest newApplicationRequest,
                               OpenshiftServiceClient openshiftClient,
                               DtoUnmarshallerFactory dtoUnmarshaller,
                               DtoFactory dtoFactory,
                               ImportWizardFactory importWizardFactory) {
        super(newApplicationRequest);
        this.openshiftClient = openshiftClient;
        this.dtoUnmarshaller = dtoUnmarshaller;
        this.dtoFactory = dtoFactory;
        this.importWizardFactory = importWizardFactory;
    }

    @Override
    public void complete(@NotNull final CompleteCallback callback) {
        getOrCreateOpenShiftProject().thenPromise(processTemplate())
                                     .then(onSuccess(callback))
                                     .catchError(onFailed(callback));
    }

    private Operation<Template> onSuccess(final CompleteCallback callback) {
        return new Operation<Template>() {
            @Override
            public void apply(Template arg) throws OperationException {
                callback.onCompleted();
                importWizardFactory.newWizard(dataObject.getImportProject()).complete(callback);
            }
        };
    }

    private Operation<PromiseError> onFailed(final CompleteCallback callback) {
        return new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                callback.onFailure(arg.getCause());
            }
        };
    }

    private Promise<Project> getOrCreateOpenShiftProject() {
        if (dataObject.getProject() != null) {
            return Promises.resolve(dataObject.getProject());
        } else if (dataObject.getProjectRequest() != null) {
            return createFromAsyncRequest(createOpenShiftProjectRC(dataObject.getProjectRequest()));
        } else {
            return Promises.reject(JsPromiseError.create(""));
        }
    }

    private RequestCall<Project> createOpenShiftProjectRC(final ProjectRequest request) {
        return new RequestCall<Project>() {
            @Override
            public void makeCall(AsyncCallback<Project> callback) {
                openshiftClient.createProject(request, _callback(callback, dtoUnmarshaller.newUnmarshaller(Project.class)));
            }
        };
    }

    private Function<Project, Promise<Template>> processTemplate() {
        return new Function<Project, Promise<Template>>() {
            @Override
            public Promise<Template> apply(final Project project) throws FunctionException {
                return createFromAsyncRequest(processTemplateRC(dataObject.getTemplate(), project))
                        .thenPromise(processTemplateMetadata(project));
            }
        };
    }

    private RequestCall<Template> processTemplateRC(final Template template, final Project project) {
        return new RequestCall<Template>() {
            @Override
            public void makeCall(AsyncCallback<Template> callback) {
                openshiftClient.processTemplate(project.getMetadata().getName(),
                                                template,
                                                _callback(callback, dtoUnmarshaller.newUnmarshaller(Template.class)));
            }
        };
    }

    private Function<Template, Promise<Template>> processTemplateMetadata(final Project project) {
        return new Function<Template, Promise<Template>>() {
            @Override
            public Promise<Template> apply(final Template template) throws FunctionException {
                Promise<Void> queue = Promises.resolve(null);

                for (Object o : template.getObjects()) {
                    final JSONObject object = (JSONObject)o;
                    final JSONValue metadata = object.get("metadata");
                    ((JSONObject)metadata).put("namespace", new JSONString(project.getMetadata().getName()));
                    final String kind = ((JSONString)object.get("kind")).stringValue();

                    switch (kind) {
                        case "DeploymentConfig":
                            queue.thenPromise(createDeploymentConfig(dtoFactory.createDtoFromJson(object.toString(),
                                                                                                  DeploymentConfig.class)));
                            break;
                        case "BuildConfig":
                            queue.thenPromise(createBuildConfig(dtoFactory.createDtoFromJson(object.toString(), BuildConfig.class)));
                            break;
                        case "ImageStream":
                            queue.thenPromise(createImageStream(dtoFactory.createDtoFromJson(object.toString(), ImageStream.class)));
                            break;
                        case "Route":
                            queue.thenPromise(createRoute(dtoFactory.createDtoFromJson(object.toString(), Route.class)));
                            break;
                        case "Service":
                            queue.thenPromise(createService(dtoFactory.createDtoFromJson(object.toString(), Service.class)));
                            break;
                    }
                }

                return queue.thenPromise(new Function<Void, Promise<Template>>() {
                    @Override
                    public Promise<Template> apply(Void arg) throws FunctionException {
                        return Promises.resolve(template);
                    }
                });
            }
        };
    }

    private Function<Void, Promise<Void>> createDeploymentConfig(final DeploymentConfig config) {
        return new Function<Void, Promise<Void>>() {
            @Override
            public Promise<Void> apply(Void arg) throws FunctionException {
                return createFromAsyncRequest(createDeploymentConfigRC(config)).thenPromise(new VoidFunction<DeploymentConfig>());
            }
        };
    }

    private RequestCall<DeploymentConfig> createDeploymentConfigRC(final DeploymentConfig config) {
        return new RequestCall<DeploymentConfig>() {
            @Override
            public void makeCall(AsyncCallback<DeploymentConfig> callback) {
                openshiftClient
                        .createDeploymentConfig(config, _callback(callback, dtoUnmarshaller.newUnmarshaller(DeploymentConfig.class)));
            }
        };
    }

    private Function<Void, Promise<Void>> createBuildConfig(final BuildConfig config) {
        return new Function<Void, Promise<Void>>() {
            @Override
            public Promise<Void> apply(Void arg) throws FunctionException {
                return createFromAsyncRequest(createBuildConfigRC(config)).thenPromise(new VoidFunction<BuildConfig>());
            }
        };
    }

    private RequestCall<BuildConfig> createBuildConfigRC(final BuildConfig config) {
        return new RequestCall<BuildConfig>() {
            @Override
            public void makeCall(AsyncCallback<BuildConfig> callback) {
                openshiftClient.createBuildConfig(config, _callback(callback, dtoUnmarshaller.newUnmarshaller(BuildConfig.class)));
            }
        };
    }

    private Function<BuildConfig, Promise<Void>> setUpImportConfiguration() {
        return new Function<BuildConfig, Promise<Void>>() {
            @Override
            public Promise<Void> apply(BuildConfig config) throws FunctionException {


                return Promises.resolve(null);
            }
        };
    }

    private Function<Void, Promise<Void>> createImageStream(final ImageStream stream) {
        return new Function<Void, Promise<Void>>() {
            @Override
            public Promise<Void> apply(Void arg) throws FunctionException {
                return createFromAsyncRequest(createImageStreamRC(stream)).thenPromise(new VoidFunction<ImageStream>());
            }
        };
    }

    private RequestCall<ImageStream> createImageStreamRC(final ImageStream stream) {
        return new RequestCall<ImageStream>() {
            @Override
            public void makeCall(AsyncCallback<ImageStream> callback) {
                openshiftClient.createImageStream(stream, _callback(callback, dtoUnmarshaller.newUnmarshaller(ImageStream.class)));
            }
        };
    }

    private Function<Void, Promise<Void>> createRoute(final Route route) {
        return new Function<Void, Promise<Void>>() {
            @Override
            public Promise<Void> apply(Void arg) throws FunctionException {
                return createFromAsyncRequest(createRouteRC(route)).thenPromise(new VoidFunction<Route>());
            }
        };
    }

    private RequestCall<Route> createRouteRC(final Route route) {
        return new RequestCall<Route>() {
            @Override
            public void makeCall(AsyncCallback<Route> callback) {
                openshiftClient.createRoute(route, _callback(callback, dtoUnmarshaller.newUnmarshaller(Route.class)));
            }
        };
    }

    private Function<Void, Promise<Void>> createService(final Service service) {
        return new Function<Void, Promise<Void>>() {
            @Override
            public Promise<Void> apply(Void arg) throws FunctionException {
                return createFromAsyncRequest(createServiceRC(service)).thenPromise(new VoidFunction<Service>());
            }
        };
    }

    private RequestCall<Service> createServiceRC(final Service service) {
        return new RequestCall<Service>() {
            @Override
            public void makeCall(AsyncCallback<Service> callback) {
                openshiftClient.createService(service, _callback(callback, dtoUnmarshaller.newUnmarshaller(Service.class)));
            }
        };
    }

    class VoidFunction<T> implements Function<T, Promise<Void>> {
        @Override
        public Promise<Void> apply(T arg) throws FunctionException {
            return Promises.resolve(null);
        }
    }

    protected <T> AsyncRequestCallback<T> _callback(final AsyncCallback<T> callback, Unmarshallable<T> u) {
        return new AsyncRequestCallback<T>(u) {
            @Override
            protected void onSuccess(T result) {
                callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        };
    }
}
