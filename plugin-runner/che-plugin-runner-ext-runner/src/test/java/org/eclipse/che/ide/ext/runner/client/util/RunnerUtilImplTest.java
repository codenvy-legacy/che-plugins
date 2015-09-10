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
package org.eclipse.che.ide.ext.runner.client.util;

import com.google.inject.Provider;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerPresenter;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerView;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.tabs.console.container.ConsoleContainer;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.constraints.Min;

import java.util.Arrays;
import java.util.List;

import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.RAM.MB_100;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(DataProviderRunner.class)
public class RunnerUtilImplTest {

    private static final String SOME_TEXT = "someText";

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    @Mock
    private DialogFactory                    dialogFactory;
    @Mock
    private RunnerLocalizationConstant       locale;
    @Mock
    private Provider<RunnerManagerPresenter> runnerManagerProvider;
    @Mock
    private RunnerManagerPresenter           presenter;
    @Mock
    private NotificationManager              notificationManager;
    @Mock
    private RunnerManagerView                view;
    @Mock
    private MessageDialog                    messageDialog;
    @Mock
    private Runner                           runner;
    @Mock
    private Throwable                        exception;
    @Mock
    private ConsoleContainer                 consoleContainer;
    @Mock
    private AppContext                       applicationContext;
    @Mock
    private CurrentProject                   currentProject;
    @Mock
    private ProjectDescriptor                projectDescriptor;

    private RunnerUtil util;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(runnerManagerProvider.get()).thenReturn(presenter);
        when(presenter.getView()).thenReturn(view);
        when(applicationContext.getCurrentProject()).thenReturn(currentProject);
        when(currentProject.getProjectDescription()).thenReturn(projectDescriptor);

        util = new RunnerUtilImpl(dialogFactory, locale, runnerManagerProvider, consoleContainer, notificationManager, applicationContext);

        when(locale.titlesWarning()).thenReturn(SOME_TEXT);
        when(dialogFactory.createMessageDialog(anyString(), anyString(), any(ConfirmCallback.class))).thenReturn(messageDialog);
    }

    @DataProvider
    public static Object[][] checkIsNonNegativeMemoryValue() {
        return new Object[][]{
                {-1, 1, 1},
                {1, -1, 1},
                {1, 1, -1}
        };
    }

    @Test
    @UseDataProvider("checkIsNonNegativeMemoryValue")
    public void runnerMemoryShouldBeAboveZero(@Min(value=0) int totalMemory, @Min(value=0) int usedMemory, @Min(value=0) int availableMemory) {
        when(locale.messagesIncorrectValue()).thenReturn(SOME_TEXT);

        boolean isCorrect = util.isRunnerMemoryCorrect(totalMemory, usedMemory, availableMemory);

        verifyShowWarning();
        verify(locale).messagesIncorrectValue();

        assertThat(isCorrect, is(false));
    }

    private void verifyShowWarning() {
        verify(dialogFactory).createMessageDialog(SOME_TEXT, SOME_TEXT, null);
        verify(messageDialog).show();
    }

    @Test
    public void errorMessageShouldBeShownWhenMemoryNotMultiple128() throws Exception {
        when(locale.ramSizeMustBeMultipleOf(MB_100.getValue())).thenReturn(SOME_TEXT);

        boolean isCorrect = util.isRunnerMemoryCorrect(125, 123, 125);

        verifyShowWarning();
        verify(locale).ramSizeMustBeMultipleOf(MB_100.getValue());

        assertThat(isCorrect, is(false));
    }

    @Test
    public void errorMessageShouldBeShownWhenUsedMemoryMoreTotalMemory() throws Exception {
        when(locale.messagesTotalRamLessCustom(anyInt(), anyInt())).thenReturn(SOME_TEXT);

        boolean isCorrect = util.isRunnerMemoryCorrect(99, 100, 99);

        verifyShowWarning();
        verify(locale).messagesTotalRamLessCustom(100, 99);

        assertThat(isCorrect, is(false));
    }

    @Test
    public void errorMessageShouldBeShownWhenUsedMemoryMoreAvailableMemory() throws Exception {
        when(locale.messagesAvailableRamLessCustom(anyInt(), anyInt(), anyInt())).thenReturn(SOME_TEXT);

        boolean isCorrect = util.isRunnerMemoryCorrect(257, 200, 128);

        verifyShowWarning();
        verify(locale).messagesAvailableRamLessCustom(200, 257, 129);

        assertThat(isCorrect, is(false));
    }

    @Test
    public void memoryShouldBeCorrect() throws Exception {
        boolean isCorrect = util.isRunnerMemoryCorrect(200, 200, 200);

        assertThat(isCorrect, is(true));

        verify(dialogFactory, never()).createMessageDialog(anyString(), anyString(), any(ConfirmCallback.class));
    }

    @Test
    public void errorShouldBeShown() throws Exception {
        when(exception.getMessage()).thenReturn(null);

        util.showError(runner, SOME_TEXT, exception);

        verifyShowError();
        verify(consoleContainer).printError(runner, SOME_TEXT);
    }

    private void verifyShowError() {
        verify(runner).setStatus(Runner.Status.FAILED);
        verify(presenter).update(runner);

        verify(notificationManager).showNotification(notificationCaptor.capture());

        Notification notification = notificationCaptor.getValue();

        assertThat(notification.isError(), is(true));
        assertThat(notification.isFinished(), is(true));
        assertThat(notification.isImportant(), is(true));
    }

    @Test
    public void errorShouldBeShownWhenExceptionIsNotNull() throws Exception {
        when(exception.getMessage()).thenReturn(SOME_TEXT);

        util.showError(runner, SOME_TEXT, exception);

        verifyShowError();
        verify(consoleContainer).printError(runner, SOME_TEXT + ": " + SOME_TEXT);
    }

    @Test
    public void userShouldBeHasPermissionForRunProject() {
        List<String> permissions = Arrays.asList("run");
        when(projectDescriptor.getPermissions()).thenReturn(permissions);

        assertThat(util.hasRunPermission(), is(true));
    }

    @Test
    public void userShouldNotHasPermissionForRunProjectBecauseProjectIsNull() {
        when(applicationContext.getCurrentProject()).thenReturn(null);

        assertThat(util.hasRunPermission(), is(false));
    }

    @Test
    public void userShouldNotHasPermissionForRunProject() {
        List<String> permissions = Arrays.asList();
        when(projectDescriptor.getPermissions()).thenReturn(permissions);

        assertThat(util.hasRunPermission(), is(false));
    }
}