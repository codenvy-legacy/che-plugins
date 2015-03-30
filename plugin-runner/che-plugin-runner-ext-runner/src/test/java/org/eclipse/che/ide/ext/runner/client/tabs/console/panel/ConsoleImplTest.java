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

import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.inject.factories.WidgetFactory;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.eclipse.che.ide.ext.runner.client.tabs.console.panel.MessageType.DOCKER;
import static org.eclipse.che.ide.ext.runner.client.tabs.console.panel.MessageType.ERROR;
import static org.eclipse.che.ide.ext.runner.client.tabs.console.panel.MessageType.INFO;
import static org.eclipse.che.ide.ext.runner.client.tabs.console.panel.MessageType.WARNING;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Andrey Plotnikov
 */
@RunWith(GwtMockitoTestRunner.class)
public class ConsoleImplTest {

    private static final String SOME_TEXT = "some text";

    @Captor
    private ArgumentCaptor<HTML> htmlArgumentCaptor;

    // additional field
    @Mock
    private SafeHtml                  content;
    @Mock
    private MessageBuilder            messageBuilder;
    @Mock
    private RunnerResources.RunnerCss css;
    @Mock
    private Widget                    widget1;
    @Mock
    private Widget                    widget2;

    // constructor field
    @Mock(answer = RETURNS_DEEP_STUBS)
    private RunnerResources          res;
    @Mock
    private Runner                   runner;
    @Mock
    private Provider<MessageBuilder> messageBuilderProvider;
    @Mock
    private WidgetFactory            widgetFactory;

    @InjectMocks
    private ConsoleImpl console;

    @Before
    public void setUp() throws Exception {
        when(messageBuilderProvider.get()).thenReturn(messageBuilder);

        when(messageBuilder.type(any(MessageType.class))).thenReturn(messageBuilder);
        when(messageBuilder.message(anyString())).thenReturn(messageBuilder);
        when(messageBuilder.build()).thenReturn(content);
        when(res.runnerCss()).thenReturn(css);
        when(css.wrappedText()).thenReturn(SOME_TEXT);
    }

    @Test
    public void infoMessageShouldBePrinted() throws Exception {
        console.printInfo(SOME_TEXT);

        verify(console.output).add(any(HTML.class));

        verify(messageBuilder).type(INFO);
        verify(messageBuilder).message(INFO.getPrefix() + ' ' + SOME_TEXT);
    }

    @Test
    public void errorMessageShouldBePrinted() throws Exception {
        console.printError(SOME_TEXT);

        verify(console.output).add(any(HTML.class));

        verify(messageBuilder).type(ERROR);
        verify(messageBuilder).message(ERROR.getPrefix() + ' ' + SOME_TEXT);
    }

    @Test
    public void warningMessageShouldBePrinted() throws Exception {
        console.printWarn(SOME_TEXT);

        verify(console.output).add(any(HTML.class));

        verify(messageBuilder).type(WARNING);
        verify(messageBuilder).message(WARNING.getPrefix() + ' ' + SOME_TEXT);
    }

    @Test
    public void messageShouldBeCleanedWhenTheAmountIs1000AndLogUrlIsAbsent() throws Exception {
        when(console.output.getWidgetCount()).thenReturn(1000);

        console.printInfo(SOME_TEXT);

        verify(messageBuilder).type(INFO);
        verify(messageBuilder).message(INFO.getPrefix() + ' ' + SOME_TEXT);

        verify(console.output, times(100)).remove(0);
        verify(console.output, never()).insert(any(HTML.class), eq(0));
        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void messageShouldBeCleanedWhenTheAmountIs1000AndLogHrefIsAbsent() throws Exception {
        when(console.output.getWidgetCount()).thenReturn(1000);

        Link logLink = mock(Link.class);
        when(runner.getLogUrl()).thenReturn(logLink);

        console.printInfo(SOME_TEXT);

        verify(messageBuilder).type(INFO);
        verify(messageBuilder).message(INFO.getPrefix() + ' ' + SOME_TEXT);

        verify(console.output, times(100)).remove(0);
        verify(console.output, never()).insert(any(HTML.class), eq(0));
        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void messageShouldBeCleanedWhenTheAmountIs1000AndLogUrlIsExist() throws Exception {
        when(console.output.getWidgetCount()).thenReturn(1000);

        FullLogMessageWidget messageWidget = mock(FullLogMessageWidget.class);
        when(widgetFactory.createFullLogMessage(anyString())).thenReturn(messageWidget);

        Link logLink = mock(Link.class);
        when(logLink.getHref()).thenReturn(SOME_TEXT);

        when(runner.getLogUrl()).thenReturn(logLink);

        console.printInfo(SOME_TEXT);

        verify(messageBuilder).type(INFO);
        verify(messageBuilder).message(INFO.getPrefix() + ' ' + SOME_TEXT);

        verify(widgetFactory).createFullLogMessage(anyString());

        verify(console.output, times(100)).remove(0);
        verify(console.output).insert(messageWidget, 0);
        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void consoleShouldBeCleaned() throws Exception {
        console.clear();

        verify(console.output).clear();
    }

    @Test
    public void someMessageShouldBePrinted() throws Exception {
        String message = INFO.getPrefix() + SOME_TEXT;
        console.print(message);

        verify(messageBuilder).type(INFO);
        verify(messageBuilder).message(message);

        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void wrappedTextCssShouldNotBeApplied() {
        String message = INFO.getPrefix() + SOME_TEXT;
        console.changeWrapTextParam();
        console.print(message);
        verify(res, times(2)).runnerCss();
        verify(css, times(2)).wrappedText();

        verify(messageBuilder).type(INFO);
        verify(messageBuilder).message(message);

        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void emptyMessageShouldNotBePrinted() {
        console.print("");

        verifyNoMoreInteractions(messageBuilderProvider);
    }

    @Test
    public void dockerErrorMessageShouldBePrinted() throws Exception {
        String message = DOCKER.getPrefix() + ' ' + ERROR.getPrefix() + SOME_TEXT;
        console.print(message);

        verify(messageBuilder).type(DOCKER);
        verify(messageBuilder).type(ERROR);
        verify(messageBuilder).message(message);

        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void complexMessageShouldBePrinted() throws Exception {
        String content = INFO.getPrefix() + SOME_TEXT;
        String message = content + '\n' + content;

        console.print(message);

        verify(messageBuilder, times(2)).type(INFO);
        verify(messageBuilder, times(2)).message(content);

        verify(console.output, times(2)).add(any(HTML.class));
    }

    @Test
    public void complexMessageShouldBePrinted2() throws Exception {
        String content = INFO.getPrefix() + SOME_TEXT;
        String message = content + '\n';

        console.print(message);

        verify(messageBuilder).type(INFO);
        verify(messageBuilder).message(content);

        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void shouldChangeWrapTextParamWhenIsWrappedTextIsTrue() {
        when(console.output.getWidgetCount()).thenReturn(2);
        when(console.output.getWidget(0)).thenReturn(widget1);
        when(console.output.getWidget(1)).thenReturn(widget2);

        console.changeWrapTextParam();

        verify(res).runnerCss();
        verify(css).wrappedText();

        verify(console.output, times(3)).getWidgetCount();
        verify(console.output).getWidget(0);
        verify(console.output).getWidget(1);

        verify(widget1).addStyleName(SOME_TEXT);
        verify(widget2).addStyleName(SOME_TEXT);
    }

    @Test
    public void shouldChangeWrapTextParamWhenIsWrappedTextIsFalse() {
        when(console.output.getWidgetCount()).thenReturn(2);
        when(console.output.getWidget(0)).thenReturn(widget1);
        when(console.output.getWidget(1)).thenReturn(widget2);

        console.changeWrapTextParam();
        reset(widget1, widget2);

        console.changeWrapTextParam();

        verify(res, times(2)).runnerCss();
        verify(css, times(2)).wrappedText();

        verify(console.output, times(6)).getWidgetCount();
        verify(console.output, times(2)).getWidget(0);
        verify(console.output, times(2)).getWidget(1);

        verify(widget1).removeStyleName(SOME_TEXT);
        verify(widget2).removeStyleName(SOME_TEXT);
    }

    @Test
    public void wrapTextShouldReturnFalse() {
        assertThat(console.isWrapText(), is(false));
    }

    @Test
    public void wrapTextShouldReturnTrue() {
        console.changeWrapTextParam();
        assertThat(console.isWrapText(), is(true));
    }

}