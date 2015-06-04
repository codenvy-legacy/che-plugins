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
package org.eclipse.che.ide.extension.machine.client.actions;

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.workspace.perspectives.general.PerspectiveManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.eclipse.che.ide.extension.machine.client.perspective.MachinePerspective.MACHINE_PERSPECTIVE_ID;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class MachinePerspectiveActionTest {

    private static final String SOME_TEXT = "someText";

    //constructor mocks
    @Mock
    private PerspectiveManager          perspectiveManager;
    @Mock
    private Resources                   resources;
    @Mock
    private MachineLocalizationConstant locale;

    //additional mocks
    @Mock(answer = RETURNS_DEEP_STUBS)
    private ActionEvent actionEvent;

    @InjectMocks
    private MachinePerspectiveAction action;

    @Test
    public void constructorShouldBeVerified() {
        verify(locale).perspectiveActionDescription();
        verify(locale).perspectiveActionTooltip();
        verify(resources).closeProject();
    }

    @Test
    public void actionShouldBeDisable() {
        when(perspectiveManager.getPerspectiveId()).thenReturn(MACHINE_PERSPECTIVE_ID);

        action.update(actionEvent);

        verify(perspectiveManager).getPerspectiveId();
        verify(actionEvent.getPresentation()).setEnabled(false);
    }

    @Test
    public void actionShouldBeEnable() {
        when(perspectiveManager.getPerspectiveId()).thenReturn(SOME_TEXT);

        action.update(actionEvent);

        verify(perspectiveManager).getPerspectiveId();
        verify(actionEvent.getPresentation()).setEnabled(true);
    }

    @Test
    public void actionShouldBePerformed() {
        action.actionPerformed(actionEvent);

        verify(actionEvent.getPresentation()).setEnabled(false);
        verify(perspectiveManager).setPerspectiveId(MACHINE_PERSPECTIVE_ID);
    }

}