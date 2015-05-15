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
package org.eclipse.che.ide.ext.runner.client.manager.menu;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.ext.runner.client.manager.menu.entry.MenuEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class MenuWidgetImplTest {

    @Mock
    private MenuEntry entry;

    @InjectMocks
    private MenuWidgetImpl menu;

    @Test
    public void spanPanelShouldBeGot() {
        SimplePanel panel = menu.getSpan();

        assertThat(panel, sameInstance(menu.span));
    }

    @Test
    public void entryShouldBeAdded() {
        menu.addEntry(entry);

        verify(menu.entityPanel).add(entry);
    }
}