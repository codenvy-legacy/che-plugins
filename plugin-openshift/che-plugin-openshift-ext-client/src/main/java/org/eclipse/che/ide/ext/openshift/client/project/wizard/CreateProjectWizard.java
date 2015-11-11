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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.js.JsPromiseError;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.wizard.AbstractWizard;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.client.dto.NewApplicationRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.DeploymentConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.ImageStream;
import org.eclipse.che.ide.ext.openshift.shared.dto.Parameter;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.ext.openshift.shared.dto.Route;
import org.eclipse.che.ide.ext.openshift.shared.dto.Service;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;
import org.eclipse.che.ide.projectimport.wizard.ImportWizardFactory;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Collections.singletonList;
import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_APPLICATION_VARIABLE_NAME;
import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_NAMESPACE_VARIABLE_NAME;
import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_PROJECT_TYPE_ID;

/**
 * Complete wizard handler. Handle create operation and tries to create configs on openshift.
 *
 * @author Vlad Zhukovskiy
 */
public class CreateProjectWizard extends AbstractWizard<NewApplicationRequest> {

    private final OpenshiftServiceClient openshiftClient;
    private final DtoFactory             dtoFactory;
    private final ImportWizardFactory    importWizardFactory;

    private Predicate<Parameter> APP_NAME_PARAM = new Predicate<Parameter>() {
        @Override
        public boolean apply(Parameter parameter) {
            return "APPLICATION_NAME".equals(parameter.getName());
        }
    };

    @Inject
    public CreateProjectWizard(@Assisted NewApplicationRequest newApplicationRequest,
                               OpenshiftServiceClient openshiftClient,
                               DtoFactory dtoFactory,
                               ImportWizardFactory importWizardFactory) {
        super(newApplicationRequest);
        this.openshiftClient = openshiftClient;
        this.dtoFactory = dtoFactory;
        this.importWizardFactory = importWizardFactory;
    }

    @Override
    public void complete(@NotNull final CompleteCallback callback) {
        Parameter appNameParam;

        try {
            appNameParam = Iterables.find(dataObject.getTemplate().getParameters(), APP_NAME_PARAM);
        } catch (NoSuchElementException e) {
            callback.onFailure(e);
            return;
        }

        appNameParam.setValue(dataObject.getImportProject().getProject().getName());

        getProject().thenPromise(setUpMixinType())
                    .thenPromise(processTemplate())
                    .thenPromise(processTemplateMetadata())
                    .then(onSuccess(callback))
                    .catchError(onFailed(callback));
    }


    private Operation<JsArrayMixed> onSuccess(final CompleteCallback callback) {
        return new Operation<JsArrayMixed>() {
            @Override
            public void apply(JsArrayMixed arg) throws OperationException {
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

    private Promise<Project> getProject() {
        if (dataObject.getProject() != null) {
            return Promises.resolve(dataObject.getProject());
        } else if (dataObject.getProjectRequest() != null) {
            return openshiftClient.createProject(dataObject.getProjectRequest());
        } else {
            return Promises.reject(JsPromiseError.create(""));
        }
    }

    private Function<Project, Promise<Project>> setUpMixinType() {
        return new Function<Project, Promise<Project>>() {
            @Override
            public Promise<Project> apply(Project project) throws FunctionException {
                Map<String, List<String>> attributes = new HashMap<>(2);
                attributes.put(OPENSHIFT_APPLICATION_VARIABLE_NAME, singletonList(dataObject.getImportProject().getProject().getName()));
                attributes.put(OPENSHIFT_NAMESPACE_VARIABLE_NAME, singletonList(project.getMetadata().getName()));

                dataObject.getImportProject().getProject().setMixins(singletonList(OPENSHIFT_PROJECT_TYPE_ID));
                dataObject.getImportProject().getProject().withAttributes(attributes);

                return Promises.resolve(project);
            }
        };
    }

    private Function<Project, Promise<Template>> processTemplate() {
        return new Function<Project, Promise<Template>>() {
            @Override
            public Promise<Template> apply(final Project project) throws FunctionException {
                return openshiftClient.processTemplate(project.getMetadata().getName(), dataObject.getTemplate());
            }
        };
    }

    private Function<Template, Promise<JsArrayMixed>> processTemplateMetadata() {
        return new Function<Template, Promise<JsArrayMixed>>() {
            @Override
            public Promise<JsArrayMixed> apply(final Template template) throws FunctionException {
//                Promise<Void> queue = Promises.resolve(null);

                List<Promise<?>> promises = new ArrayList<>();

                for (Object o : template.getObjects()) {
                    final JSONObject object = (JSONObject)o;
                    final JSONValue metadata = object.get("metadata");
                    final String namespace =
                            dataObject.getImportProject().getProject().getAttributes().get(OPENSHIFT_NAMESPACE_VARIABLE_NAME).get(0);
                    ((JSONObject)metadata).put("namespace", new JSONString(namespace));
                    final String kind = ((JSONString)object.get("kind")).stringValue();

                    switch (kind) {
                        case "DeploymentConfig":
                            DeploymentConfig dConfig = dtoFactory.createDtoFromJson(object.toString(), DeploymentConfig.class);
                            promises.add(openshiftClient.createDeploymentConfig(dConfig));
                            break;
                        case "BuildConfig":
                            BuildConfig bConfig = dtoFactory.createDtoFromJson(object.toString(), BuildConfig.class);
                            promises.add(openshiftClient.createBuildConfig(bConfig));
                            break;
                        case "ImageStream":
                            ImageStream stream = dtoFactory.createDtoFromJson(object.toString(), ImageStream.class);
                            promises.add(openshiftClient.createImageStream(stream));
                            break;
                        case "Route":
                            Route route = dtoFactory.createDtoFromJson(object.toString(), Route.class);
                            promises.add(openshiftClient.createRoute(route));
                            break;
                        case "Service":
                            Service service = dtoFactory.createDtoFromJson(object.toString(), Service.class);
                            promises.add(openshiftClient.createService(service));
                            break;
                    }
                }

                return Promises.all(promises.toArray(new Promise<?>[promises.size()]));
            }
        };
    }
}
