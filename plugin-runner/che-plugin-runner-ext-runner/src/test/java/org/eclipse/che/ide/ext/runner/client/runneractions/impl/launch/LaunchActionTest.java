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
package org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.tabs.console.container.ConsoleContainer;
import org.eclipse.che.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerPresenter;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerView;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions.OutputAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.subactions.StatusAction;

import org.eclipse.che.ide.ext.runner.client.runneractions.RunnerAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Valeriy Svydenko
 */
@RunWith(MockitoJUnitRunner.class)
public class LaunchActionTest {
    private static final String SOME_TEXT = "some text";

    @Mock
    private Runner            runner;
    @Mock
    private RunnerManagerView view;
    @Mock
    private CurrentProject    project;
    @Mock
    private ProjectDescriptor projectDescriptor;
    @Mock
    private StatusAction      statusAction;

    @Mock
    private NotificationManager        notificationManager;
    @Mock
    private RunnerManagerPresenter     runnerManagerPresenter;
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private AppContext                 appContext;
    @Mock
    private RunnerActionFactory        runnerActionFactory;
    @Mock
    private OutputAction               outputAction;
    @Mock
    private ConsoleContainer           consoleContainer;

    private LaunchAction action;

    @Before
    public void setUp() throws Exception {
        when(runnerActionFactory.createOutput()).thenReturn(outputAction);
        when(runnerManagerPresenter.getView()).thenReturn(view);

        action = new LaunchAction(notificationManager, locale, appContext, consoleContainer, runnerActionFactory);

        verify(outputAction).setListener(Matchers.<RunnerAction.StopActionListener>any());
    }

    @Test
    public void nothingShouldHappenWhenACurrentProjectIsNull() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(null);

        action.perform(runner);

        verify(locale, never()).environmentCooking(anyString());
        verify(outputAction, never()).perform(runner);
        verify(notificationManager, never()).showNotification(Matchers.<Notification>any());
        verify(runnerActionFactory, never()).createStatus(Matchers.<Notification>any());
    }

    @Test
    public void actionShouldBePerforms() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(project);
        when(project.getProjectDescription()).thenReturn(projectDescriptor);
        when(projectDescriptor.getName()).thenReturn(SOME_TEXT);
        when(runnerActionFactory.createStatus(Matchers.<Notification>any())).thenReturn(statusAction);
        when(locale.environmentCooking(anyString())).thenReturn(SOME_TEXT);

        action.perform(runner);

        verify(project).setIsRunningEnabled(false);
        verify(locale).environmentCooking(SOME_TEXT);
        verify(notificationManager).showNotification(Matchers.<Notification>any());
        verify(consoleContainer).printInfo(runner, SOME_TEXT);
        verify(runnerActionFactory).createStatus(Matchers.<Notification>any());
        verify(statusAction).perform(runner);
        verify(outputAction).perform(runner);
    }

}