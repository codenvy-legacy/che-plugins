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
package org.eclipse.che.ide.ext.openshift.client.url;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.core.rest.shared.dto.ServiceError;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.shared.dto.Route;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_APPLICATION_VARIABLE_NAME;
import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_NAMESPACE_VARIABLE_NAME;

/**
 * Presenter for showing application urls.
 *
 * @author Sergii Leschenko
 */
@Singleton
public class ShowApplicationUrlPresenter implements ShowApplicationUrlView.ActionDelegate {

    private final DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    private final ShowApplicationUrlView        view;
    private final OpenshiftServiceClient        service;
    private final AppContext                    appContext;
    private final NotificationManager           notificationManager;
    private final OpenshiftLocalizationConstant locale;
    private final DtoFactory                    dtoFactory;

    @Inject
    public ShowApplicationUrlPresenter(ShowApplicationUrlView view,
                                       OpenshiftServiceClient service,
                                       DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                       AppContext appContext,
                                       NotificationManager notificationManager,
                                       OpenshiftLocalizationConstant locale,
                                       DtoFactory dtoFactory) {
        this.appContext = appContext;
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.service = service;
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.view.setDelegate(this);
    }

    public void showDialog() {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }
        final ProjectDescriptor projectDescription = currentProject.getProjectDescription();
        service.getRoutes(getAttributeValue(projectDescription, OPENSHIFT_NAMESPACE_VARIABLE_NAME),
                          getAttributeValue(projectDescription, OPENSHIFT_APPLICATION_VARIABLE_NAME),
                          new AsyncRequestCallback<List<Route>>(dtoUnmarshallerFactory.newListUnmarshaller(Route.class)) {
                              @Override
                              protected void onSuccess(List<Route> result) {
                                  List<String> urls = new ArrayList<>();
                                  for (Route route : result) {
                                      urls.add(route.getSpec().getHost());
                                  }
                                  view.setURLs(urls);
                                  view.showDialog();
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  final ServiceError serviceError = dtoFactory.createDtoFromJson(exception.getLocalizedMessage(), ServiceError.class);
                                  notificationManager.showError(locale.getRoutesError() + ". " + serviceError.getMessage());
                              }
                          }
                         );
    }

    /** Returns first value of attribute of null if it is absent in project descriptor */
    private String getAttributeValue(ProjectDescriptor projectDescriptor, String attibuteValue) {
        final List<String> values = projectDescriptor.getAttributes().get(attibuteValue);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }
}
