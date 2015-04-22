/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package org.eclipse.che.plugin.grunt.client.presenter;

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.runner.dto.RunOptions;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerPresenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test of the custom presenter
 * @author Florent Benoit
 */
@RunWith(GwtMockitoTestRunner.class)
public class SelectGruntTaskPagePresenterTest {

    @Mock
    private SelectGruntTaskPageView view;

    @Mock
    private RunnerManagerPresenter runnerManagerPresenter;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private DtoFactory dtoFactory;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private AppContext appContext;

    @Mock
    private RunOptions runOptions;

    private SelectGruntTaskPagePresenter selectGruntTaskPagePresenter;

    @Captor
    private ArgumentCaptor<Map<String, String>> captor;

    @Before
    public void setUp() {
        selectGruntTaskPagePresenter =
                new SelectGruntTaskPagePresenter(view, runnerManagerPresenter, projectServiceClient, dtoFactory, notificationManager,
                                                 appContext);
    }

    @Test
    public void checkClickCustomTask() throws Exception {

        // set task
        String testingTask = "test-task";
        selectGruntTaskPagePresenter.taskSelected(testingTask);

        when(dtoFactory.createDto(RunOptions.class)).thenReturn(runOptions);
        when(runOptions.withSkipBuild(true)).thenReturn(runOptions);
        when(runOptions.withOptions(anyMap())).thenReturn(runOptions);


        selectGruntTaskPagePresenter.onStartRunClicked();

        verify(view).close();
        verify(runOptions).withOptions(captor.capture());
        Map<String, String> map = captor.getValue();
        assertNotNull(map);
        assertEquals(1, map.size());
        assertTrue(map.containsKey("taskName"));
        assertEquals(testingTask, map.get("taskName"));

        verify(runnerManagerPresenter).launchRunner(runOptions);
    }

}