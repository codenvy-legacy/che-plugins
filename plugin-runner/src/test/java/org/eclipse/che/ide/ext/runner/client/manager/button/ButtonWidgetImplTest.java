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
package org.eclipse.che.ide.ext.runner.client.manager.button;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.manager.tooltip.TooltipWidget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGResource;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class ButtonWidgetImplTest {

    private static final String SOME_TEXT = "someText";

    @Mock(answer = RETURNS_DEEP_STUBS)
    private RunnerResources             resources;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private SVGResource                 image;
    @Mock
    private TooltipWidget               tooltipWidget;
    @Mock
    private ButtonWidget.ActionDelegate delegate;
    @Mock
    private ClickEvent                  clickEvent;
    @Mock
    private MouseOverEvent              mouseOverEvent;
    @Mock
    private MouseOutEvent               mouseOutEvent;

    private ButtonWidgetImpl button;

    @Before
    public void setUp() throws Exception {
        button = new ButtonWidgetImpl(resources, tooltipWidget, SOME_TEXT, image);

        when(resources.runnerCss().opacityButton()).thenReturn(SOME_TEXT);

        button.setDelegate(delegate);
    }

    @Test
    public void constructorShouldBeVerified() throws Exception {
        verify(tooltipWidget).setDescription(SOME_TEXT);
    }

    @Test
    public void buttonShouldBeDisable() throws Exception {
        button.setDisable();

        verify(button.image).addStyleName(SOME_TEXT);
    }

    @Test
    public void buttonShouldBeEnabled() throws Exception {
        button.setEnable();

        verify(button.image).removeStyleName(SOME_TEXT);
    }

    @Test
    public void buttonActionShouldBeDoneWhenItIsEnabled() throws Exception {
        button.setEnable();

        button.onClick(clickEvent);

        verify(delegate).onButtonClicked();
    }

    @Test
    public void buttonActionShouldNotBeDoneWhenItIsDisabled() throws Exception {
        button.setDisable();

        button.onClick(clickEvent);

        verify(delegate, never()).onButtonClicked();
    }

    @Test
    public void onMouseOutShouldBeDone() throws Exception {
        button.onMouseOut(mouseOutEvent);

        verify(tooltipWidget).hide();
    }

    @Test
    public void onMouseOverShouldBeDone() throws Exception {
        button.onMouseOver(mouseOverEvent);

        verify(tooltipWidget).setPopupPosition(anyInt(), anyInt());
        verify(tooltipWidget).show();
    }

}