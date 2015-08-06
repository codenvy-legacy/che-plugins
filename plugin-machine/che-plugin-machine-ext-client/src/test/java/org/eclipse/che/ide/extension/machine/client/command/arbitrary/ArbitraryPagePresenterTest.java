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
package org.eclipse.che.ide.extension.machine.client.command.arbitrary;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.eclipse.che.ide.extension.machine.client.command.ConfigurationPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Artem Zatsarynnyy */
@RunWith(MockitoJUnitRunner.class)
public class ArbitraryPagePresenterTest {

    private static final String COMMAND_LINE = "cmd";

    @Mock
    private ArbitraryPageView             arbitraryPageView;
    @Mock
    private ArbitraryCommandConfiguration configuration;

    @InjectMocks
    private ArbitraryPagePresenter arbitraryPagePresenter;

    @Before
    public void setUp() {
        when(configuration.getCommandLine()).thenReturn(COMMAND_LINE);

        arbitraryPagePresenter.resetFrom(configuration);
    }

    @Test
    public void testResetting() throws Exception {
        verify(configuration).getCommandLine();
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);

        arbitraryPagePresenter.go(container);

        verify(container).setWidget(eq(arbitraryPageView));
        verify(configuration, times(2)).getCommandLine();
        verify(arbitraryPageView).setCommandLine(eq(COMMAND_LINE));
    }

    @Test
    public void testOnCommandLineChanged() throws Exception {
        String commandLine = "commandLine";
        when(arbitraryPageView.getCommandLine()).thenReturn(commandLine);

        final ConfigurationPage.DirtyStateListener listener = mock(ConfigurationPage.DirtyStateListener.class);
        arbitraryPagePresenter.setDirtyStateListener(listener);

        arbitraryPagePresenter.onCommandLineChanged();

        verify(arbitraryPageView).getCommandLine();
        verify(configuration).setCommandLine(eq(commandLine));
        verify(listener).onDirtyStateChanged();
    }
}
