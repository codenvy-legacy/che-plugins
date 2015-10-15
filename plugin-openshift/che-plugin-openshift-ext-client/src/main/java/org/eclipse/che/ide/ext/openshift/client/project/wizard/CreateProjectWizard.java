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
package org.eclipse.che.ide.ext.openshift.client.project.wizard;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper.RequestCall;
import org.eclipse.che.api.promises.client.js.JsPromiseError;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.wizard.AbstractWizard;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.client.dto.NewApplicationRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.DeploymentConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;
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

    @Inject
    public CreateProjectWizard(@Assisted NewApplicationRequest newApplicationRequest,
                               OpenshiftServiceClient openshiftClient,
                               DtoUnmarshallerFactory dtoUnmarshaller,
                               DtoFactory dtoFactory) {
        super(newApplicationRequest);
        this.openshiftClient = openshiftClient;
        this.dtoUnmarshaller = dtoUnmarshaller;
        this.dtoFactory = dtoFactory;
    }

    @Override
    public void complete(@NotNull CompleteCallback callback) {
        Promise<Project> promise;

        if (dataObject.getProjectRequest() != null) {
            promise = createOpenShiftProject();
        } else if (dataObject.getProject() != null) {
            promise = Promises.resolve(dataObject.getProject());
        } else {
            promise = Promises.reject(JsPromiseError.create(""));
        }

        promise.thenPromise(processTemplate());
    }

    private Promise<Project> createOpenShiftProject() {
        return createFromAsyncRequest(createOpenShiftProjectRC());
    }

    private RequestCall<Project> createOpenShiftProjectRC() {
        return new RequestCall<Project>() {
            @Override
            public void makeCall(AsyncCallback<Project> callback) {
                openshiftClient.createProject(dataObject.getProjectRequest(),
                                              _callback(callback, dtoUnmarshaller.newUnmarshaller(Project.class)));
            }
        };
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

    private Function<Project, Promise<Template>> processTemplate() {
        return new Function<Project, Promise<Template>>() {
            @Override
            public Promise<Template> apply(Project arg) throws FunctionException {
                //process template
                return Promises.resolve(dataObject.getTemplate());
            }
        };
    }

    private Function<Template, Promise<Template>> createDeploymentConfig() {
        return new Function<Template, Promise<Template>>() {
            @Override
            public Promise<Template> apply(final Template template) throws FunctionException {

                //if template doesn't have deployment config, then resolve promise else create deployment config

                return createFromAsyncRequest(createDeploymentConfigRC(dtoFactory.createDto(DeploymentConfig.class)))
                        .thenPromise(new Function<DeploymentConfig, Promise<Template>>() {
                            @Override
                            public Promise<Template> apply(DeploymentConfig arg) throws FunctionException {
                                return Promises.resolve(template);
                            }
                        });
            }
        };
    }

    private RequestCall<DeploymentConfig> createDeploymentConfigRC(final DeploymentConfig deploymentConfig) {
        return new RequestCall<DeploymentConfig>() {
            @Override
            public void makeCall(AsyncCallback<DeploymentConfig> callback) {
                openshiftClient.createDeploymentConfig(deploymentConfig,
                                                       _callback(callback, dtoUnmarshaller.newUnmarshaller(DeploymentConfig.class)));
            }
        };
    }
}
