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
package org.eclipse.che.ide.ext.runner.client.tabs.container.tab;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabType.LEFT;
import static org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabType.RIGHT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TabTypeTest {
    @Test
    public void shouldReturnTabHeightOfRightPanel() throws Exception {
        assertThat(RIGHT.getHeight(), is("20px"));
    }

    @Test
    public void shouldReturnTabHeightOfLeftPanel() throws Exception {
        assertThat(LEFT.getHeight(), is("21px"));
    }

    @Test
    public void shouldReturnTabWidthOfRightPanel() throws Exception {
        assertThat(RIGHT.getWidth(), is("70px"));
    }

    @Test
    public void shouldReturnTabWidthOfLeftPanel() throws Exception {
        assertThat(LEFT.getWidth(), is("70px"));
    }
}