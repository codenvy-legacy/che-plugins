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
package org.eclipse.che.ide.ext.runner.client.runneractions.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.api.runner.gwt.client.RunnerServiceClient;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import org.eclipse.che.ide.ext.runner.client.callbacks.FailureCallback;
import org.eclipse.che.ide.ext.runner.client.callbacks.SuccessCallback;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerPresenter;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import org.eclipse.che.ide.ext.runner.client.tabs.console.container.ConsoleContainer;
import org.eclipse.che.ide.ext.runner.client.util.RunnerUtil;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.StringUnmarshaller;

import javax.validation.constraints.NotNull;

/**
 * Action for getting  logs from current runner.
 *
 * @author Valeriy Svydenko
 */
public class GetLogsAction extends AbstractRunnerAction {

    private final RunnerServiceClient                    service;
    private final AppContext                             appContext;
    private final Provider<AsyncCallbackBuilder<String>> callbackBuilderProvider;
    private final RunnerLocalizationConstant             constant;
    private final RunnerUtil                             runnerUtil;
    private final RunnerManagerPresenter                 presenter;
    private final ConsoleContainer                       consoleContainer;
    private final AnalyticsEventLogger                   eventLogger;

    @Inject
    public GetLogsAction(RunnerServiceClient service,
                         AppContext appContext,
                         Provider<AsyncCallbackBuilder<String>> callbackBuilderProvider,
                         RunnerLocalizationConstant constant,
                         RunnerUtil runnerUtil,
                         ConsoleContainer consoleContainer,
                         RunnerManagerPresenter runnerManagerPresenter,
                         AnalyticsEventLogger eventLogger) {
        this.service = service;
        this.appContext = appContext;
        this.callbackBuilderProvider = callbackBuilderProvider;
        this.constant = constant;
        this.runnerUtil = runnerUtil;
        this.presenter = runnerManagerPresenter;
        this.consoleContainer = consoleContainer;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@NotNull final Runner runner) {
        eventLogger.log(this);

        CurrentProject project = appContext.getCurrentProject();
        if (project == null) {
            return;
        }

        final Link viewLogsLink = runner.getLogUrl();
        if (viewLogsLink == null) {
            return;
        }

        presenter.setActive();

        AsyncRequestCallback<String> callback = callbackBuilderProvider
                .get()
                .unmarshaller(new StringUnmarshaller())
                .success(new SuccessCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        consoleContainer.print(runner, result);
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@NotNull Throwable reason) {
                        runnerUtil.showError(runner, constant.applicationLogsFailed(), reason);
                    }
                })
                .build();

        service.getLogs(viewLogsLink, callback);
    }
}