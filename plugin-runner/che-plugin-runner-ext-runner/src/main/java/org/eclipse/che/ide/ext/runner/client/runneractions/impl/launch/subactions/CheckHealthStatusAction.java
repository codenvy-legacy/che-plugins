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
package org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.tabs.console.container.ConsoleContainer;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerPresenter;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import org.eclipse.che.ide.ext.runner.client.util.TimerFactory;
import org.eclipse.che.ide.ext.runner.client.util.WebSocketUtil;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.rest.StringUnmarshallerWS;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.validation.constraints.NotNull;

import static org.eclipse.che.ide.api.notification.Notification.Status.FINISHED;
import static org.eclipse.che.ide.api.notification.Notification.Type.INFO;
import static org.eclipse.che.ide.api.notification.Notification.Type.WARNING;
import static org.eclipse.che.ide.ext.runner.client.constants.TimeInterval.THIRTY_SEC;

/**
 * The action that checks status of runner. It pings runner every 30 second and the client side knows that the runner is alive.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class CheckHealthStatusAction extends AbstractRunnerAction {

    /** WebSocket channel to check application's health. */
    private static final String APP_HEALTH_CHANNEL = "runner:app_health:";

    private static final String STATUS    = "status";
    private static final String URL       = "url";
    private static final String OK_STATUS = "OK";

    private final AppContext                 appContext;
    private final RunnerLocalizationConstant locale;
    private final RunnerManagerPresenter     presenter;
    private final WebSocketUtil              webSocketUtil;
    private final Notification               notification;
    private final ConsoleContainer           consoleContainer;

    // The server makes the limited quantity of tries checking application's health,
    // so we're waiting for some time (about 30 sec.) and assume that app health is OK.
    private Timer changeAppAliveTimer;

    private SubscriptionHandler<String> runnerHealthHandler;
    private String                      webSocketChannel;
    private CurrentProject              project;
    private TimerFactory                timerFactory;

    @Inject
    public CheckHealthStatusAction(AppContext appContext,
                                   RunnerLocalizationConstant locale,
                                   RunnerManagerPresenter presenter,
                                   WebSocketUtil webSocketUtil,
                                   ConsoleContainer consoleContainer,
                                   TimerFactory timerFactory,
                                   @NotNull @Assisted Notification notification) {
        this.appContext = appContext;
        this.locale = locale;
        this.presenter = presenter;
        this.webSocketUtil = webSocketUtil;
        this.consoleContainer = consoleContainer;
        this.timerFactory = timerFactory;
        this.notification = notification;
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@NotNull final Runner runner) {
        project = appContext.getCurrentProject();

        if (runner.getApplicationURL() == null) {
            return;
        }

        changeAppAliveTimer = timerFactory.newInstance(new TimerFactory.TimerCallBack() {
            /** {@inheritDoc} */
            @Override
            public void onRun() {
                presenter.update(runner);

                String projectName = project.getProjectDescription().getName();
                String notificationMessage = locale.applicationMaybeStarted(projectName);

                notification.update(notificationMessage, WARNING, FINISHED, null, true);
                consoleContainer.printWarn(runner, notificationMessage);
            }
        });

        changeAppAliveTimer.schedule(THIRTY_SEC.getValue());

        webSocketChannel = APP_HEALTH_CHANNEL + runner.getProcessId();

        final String projectName = project.getProjectDescription().getName();
        final String notificationMessage = locale.applicationStarted(projectName);
        runnerHealthHandler = new SubscriptionHandler<String>(new StringUnmarshallerWS()) {
            @Override
            protected void onMessageReceived(String result) {
                JSONObject jsonObject = JSONParser.parseStrict(result).isObject();
                if(!jsonObjectIsValid(jsonObject)) {
                    return;
                }
                changeAppAliveTimer.cancel();
                runner.setStatus(Runner.Status.DONE);
                presenter.update(runner);
                notification.update(notificationMessage, INFO, FINISHED, null, true);
                consoleContainer.printInfo(runner, notificationMessage);
                stop();
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                Log.error(getClass(), exception);
            }
        };

        webSocketUtil.subscribeHandler(webSocketChannel, runnerHealthHandler);
    }

    private boolean jsonObjectIsValid(JSONObject jsonObject) {
        if (jsonObject == null || !jsonObject.containsKey(URL) || !jsonObject.containsKey(STATUS)) {
            return false;
        }

        String urlStatus = jsonObject.get(STATUS).isString().stringValue();

        return OK_STATUS.equals(urlStatus);
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        if (webSocketChannel == null || runnerHealthHandler == null) {
            // It is impossible to perform stop event twice.
            return;
        }
        webSocketUtil.unSubscribeHandler(webSocketChannel, runnerHealthHandler);

        if (changeAppAliveTimer != null) {
            changeAppAliveTimer.cancel();
        }

        super.stop();

        webSocketChannel = null;
        runnerHealthHandler = null;
    }

}