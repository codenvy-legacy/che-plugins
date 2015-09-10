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
package org.eclipse.che.ide.ext.runner.client.actions;

import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class AbstractRunnerActionsTest {

    private final static String SOME_TEXT = "someText";

    @Mock
    private CurrentProject currentProject;
    @Mock
    private AppContext     appContext;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private ActionEvent    actionEvent;

    private AbstractRunnerActions action;

    @Before
    public void setUp() {
        action = new DummyAction(appContext, SOME_TEXT, SOME_TEXT, null);
    }

    @Test
    public void actionShouldBeUpdatedWhenCurrentProjectIsNotNull() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(currentProject);

        action.updateProjectAction(actionEvent);

        verify(appContext).getCurrentProject();
        verify(actionEvent.getPresentation()).setEnabledAndVisible(true);
    }

    @Test
    public void actionShouldBeUpdatedWhenCurrentProjectIsNull() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(null);

        action.updateProjectAction(actionEvent);

        verify(appContext).getCurrentProject();
        verify(actionEvent.getPresentation()).setEnabledAndVisible(false);
    }

    private class DummyAction extends AbstractRunnerActions {

        public DummyAction(@NotNull AppContext appContext,
                           @NotNull String actionName,
                           @NotNull String actionPrompt,
                           @Nullable SVGResource image) {
            super(appContext, actionName, actionPrompt, image);
        }

        @Override
        public void actionPerformed(ActionEvent event) {
        }
    }

}