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
package org.eclipse.che.ide.ext.runner.client.tabs.common.item;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGImage;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class ItemWidgetImplTest {
    private static final String TEST_TEXT = "some text for test";

    @Mock(answer = RETURNS_DEEP_STUBS)
    private RunnerResources           resources;
    @Mock
    private RunnerResources.RunnerCss css;

    @InjectMocks
    private ItemWidgetImpl itemWidget;

    @Before
    public void setUp() {
        when(resources.runnerCss()).thenReturn(css);
        when(css.runnerShadow()).thenReturn(TEST_TEXT);
        when(css.runnerWidgetBorders()).thenReturn(TEST_TEXT);
    }

    @Test
    public void shouldSelect() {
        itemWidget.select();

        verify(resources, times(2)).runnerCss();
        verify(css).runnerShadow();
        verify(css).runnerWidgetBorders();
    }

    @Test
    public void shouldUnSelect() {
        itemWidget.unSelect();

        verify(resources, times(2)).runnerCss();
        verify(css).runnerShadow();
        verify(css).runnerWidgetBorders();
    }

    @Test
    public void userClickEventShouldBeDelegated() throws Exception {
        ItemWidget.ActionDelegate delegate = mock(ItemWidget.ActionDelegate.class);

        itemWidget.setDelegate(delegate);
        itemWidget.onClick(mock(ClickEvent.class));

        verify(delegate).onWidgetClicked();
    }

    @Test
    public void shouldSetName() {
        itemWidget.setName(TEST_TEXT);

        verify(itemWidget.runnerName).setText(TEST_TEXT);
    }

    @Test
    public void shouldSetDescription() {
        itemWidget.setDescription(TEST_TEXT);

        verify(itemWidget.ram).setText(TEST_TEXT);
    }

    @Test
    public void shouldSetStartTime() {
        itemWidget.setStartTime(TEST_TEXT);

        verify(itemWidget.startTime).setText(TEST_TEXT);
    }

    @Test
    public void shouldSetImage() {
        ImageResource imageResource = mock(ImageResource.class);

        itemWidget.setImage(imageResource);

        verify(itemWidget.image).setWidget(any(Image.class));
    }

    @Test
    public void shouldSetSVGImage() {
        SVGImage svgImageResource = mock(SVGImage.class);

        itemWidget.setImage(svgImageResource);

        verify(resources.runnerCss()).itemIcon();
        verify(itemWidget.image).setWidget(any(SVGImage.class));
    }

    @Test
    public void imagePanelShouldBeReturned() {
        SimpleLayoutPanel panel = itemWidget.getImagePanel();

        assertThat(panel, sameInstance(itemWidget.imagePanel));
    }

}
