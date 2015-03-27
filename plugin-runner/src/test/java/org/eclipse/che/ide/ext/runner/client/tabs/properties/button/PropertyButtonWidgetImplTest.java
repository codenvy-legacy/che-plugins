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
package org.eclipse.che.ide.ext.runner.client.tabs.properties.button;

import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.Background;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Andrienko
 */
@RunWith(GwtMockitoTestRunner.class)
public class PropertyButtonWidgetImplTest {

    private static final String TEXT = "some text";

    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private RunnerResources            resources;
    @Mock
    private Background                 background;

    @Mock
    private RunnerResources.RunnerCss css;

    private PropertyButtonWidgetImpl buttonWidget;

    @Before
    public void setUp() {
        when(resources.runnerCss()).thenReturn(css);
        when(background.toString()).thenReturn(TEXT);
        when(css.opacityButton()).thenReturn(TEXT);

        buttonWidget = new PropertyButtonWidgetImpl(locale, resources, TEXT, background);
    }

    @Test
    public void prepareActionShouldBePerformed() {
        verify(buttonWidget.button).setText(TEXT);
        verify(buttonWidget.button).removeStyleName(TEXT);
    }

    @Test
    public void buttonShouldBeEnable() {
        reset(buttonWidget.button);
        buttonWidget.setEnable(true);

        verify(buttonWidget.button).removeStyleName(TEXT);
    }

    @Test
    public void buttonShouldBeDisable() {
        reset(buttonWidget.button);
        buttonWidget.setEnable(false);

        verify(buttonWidget.button).addStyleName(TEXT);
    }

    @Test
    public void widgetShouldBeOnClicked() {
        PropertyButtonWidget.ActionDelegate delegate = mock(PropertyButtonWidget.ActionDelegate.class);
        ClickEvent event = mock(ClickEvent.class);

        buttonWidget.setDelegate(delegate);
        buttonWidget.onClick(event);

        verify(delegate).onButtonClicked();
    }

}