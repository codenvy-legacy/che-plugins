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
package org.eclipse.che.ide.ext.runner.client.manager.menu.entry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.manager.menu.entry.MenuEntry.ActionDelegate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGResource;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class MenuEntryWidgetTest {

    private static final String SOME_TEXT = "someText";

    @Mock(answer = RETURNS_DEEP_STUBS)
    private RunnerResources resources;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private SVGResource     image;
    @Mock
    private Element         element;
    @Mock
    private ActionDelegate  delegate;
    @Mock
    private ClickEvent      event;

    private MenuEntryWidget menuEntry;

    @Before
    public void setUp() {
        when(resources.selectedMenuEntry()).thenReturn(image);

        menuEntry = new MenuEntryWidget(resources, SOME_TEXT);
        menuEntry.setDelegate(delegate);
    }

    @Test
    public void constructorShouldBeVerified() {
        verify(menuEntry.image).getElement();
        verify(resources).selectedMenuEntry();
        verify(menuEntry.text).setText(SOME_TEXT);
    }

    @Test
    public void onEntryShouldBeClicked() {
        menuEntry.onClick(event);

        verify(menuEntry.image, times(2)).getElement();
        verify(delegate).onEntryClicked(false);

        menuEntry.onClick(event);

        verify(delegate).onEntryClicked(true);
    }

}