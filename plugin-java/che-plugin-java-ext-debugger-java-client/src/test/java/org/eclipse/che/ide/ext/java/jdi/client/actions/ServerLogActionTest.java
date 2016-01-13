/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.java.jdi.client.actions;

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.outputspanel.OutputsContainerPresenter;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.CommandConsoleFactory;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.DefaultOutputConsole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Roman Nikitenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class ServerLogActionTest {
    private static final String MACHINE_ID      = "machineID";
    private static final String TAB_TITLE       = "Server Log";
    private static final String SERVER_LOG_PATH = "~/che/ext-server/logs/catalina.out";
    private static final String FILE_CONTENT    = "some logs from server";

    @Mock
    private WorkspaceAgent                  workspaceAgent;
    @Mock
    private AppContext                      appContext;
    @Mock
    private MachineServiceClient            machineServiceClient;
    @Mock
    private AnalyticsEventLogger            eventLogger;
    @Mock
    private CommandConsoleFactory           commandConsoleFactory;
    @Mock
    private OutputsContainerPresenter       outputsContainerPresenter;
    @Mock
    private DefaultOutputConsole            outputConsole;
    @Mock
    private JavaRuntimeLocalizationConstant locale;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private ActionEvent     actionEvent;
    @Mock
    private Promise<String> fileContentPromise;

    @Captor
    private ArgumentCaptor<Operation<String>>       fileContentCaptor;
    @Captor
    private ArgumentCaptor<Operation<PromiseError>> errorOperation;


    @InjectMocks
    private ServerLogAction action;

    @Before
    public void setUp() {
        when(appContext.getDevMachineId()).thenReturn(MACHINE_ID);
        when(locale.serverLogTabTitle()).thenReturn(TAB_TITLE);
        when(commandConsoleFactory.create(anyString())).thenReturn(outputConsole);
        when(machineServiceClient.getFileContent(anyString(), anyString(), anyInt(), anyInt())).thenReturn(fileContentPromise);
        when(fileContentPromise.then(Matchers.<Operation<String>>anyObject())).thenReturn(fileContentPromise);
    }

    @Test
    public void constructorShouldBeVerified() {
        verify(locale).displayServerLogTitle();
        verify(locale).displayServerLogDescription();
    }

    @Test
    public void actionPerformedWhenGetLogsSuccess() throws Exception {
        action.actionPerformed(actionEvent);

        verify(eventLogger).log(action);
        verify(locale).serverLogTabTitle();
        verify(commandConsoleFactory).create(eq(TAB_TITLE));
        verify(outputsContainerPresenter).addConsole(eq(outputConsole));
        verify(workspaceAgent).setActivePart(eq(outputsContainerPresenter));
        verify(machineServiceClient).getFileContent(eq(MACHINE_ID), eq(SERVER_LOG_PATH), eq(1), eq(10_000));
        verify(fileContentPromise).then(fileContentCaptor.capture());
        fileContentCaptor.getValue().apply(FILE_CONTENT);
        verify(outputConsole).printText(eq(FILE_CONTENT));
    }

    @Test
    public void actionPerformedWhenGetLogsFail() throws Exception {
        final PromiseError promiseError = mock(PromiseError.class);

        action.actionPerformed(actionEvent);

        verify(eventLogger).log(action);
        verify(locale).serverLogTabTitle();
        verify(commandConsoleFactory).create(eq(TAB_TITLE));
        verify(outputsContainerPresenter).addConsole(eq(outputConsole));
        verify(workspaceAgent).setActivePart(eq(outputsContainerPresenter));
        verify(machineServiceClient).getFileContent(eq(MACHINE_ID), eq(SERVER_LOG_PATH), eq(1), eq(10_000));
        verify(fileContentPromise).catchError(errorOperation.capture());
        errorOperation.getValue().apply(promiseError);
        verify(promiseError).getMessage();
    }
}
