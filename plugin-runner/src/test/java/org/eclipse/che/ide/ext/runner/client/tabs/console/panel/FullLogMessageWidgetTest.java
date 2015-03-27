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
package org.eclipse.che.ide.ext.runner.client.tabs.console.panel;

import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;

/**
 * @author Andrey Plotnikov
 */
@RunWith(GwtMockitoTestRunner.class)
public class FullLogMessageWidgetTest {

    private static final String SOME_TEXT = "some text";

    @Mock(answer = RETURNS_DEEP_STUBS)
    private RunnerResources            resources;
    @Mock
    private RunnerLocalizationConstant locale;

    @Test
    public void widgetShouldBeInitialized() throws Exception {
        new FullLogMessageWidget(resources, locale, SOME_TEXT);

        verify(resources.runnerCss()).logLink();
        verify(locale).fullLogTraceConsoleLink();
    }
}