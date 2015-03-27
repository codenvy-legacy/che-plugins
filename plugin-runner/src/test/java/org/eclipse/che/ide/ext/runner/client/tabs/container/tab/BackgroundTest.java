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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.eclipse.che.ide.ext.runner.client.tabs.container.tab.Background.BLACK;
import static org.eclipse.che.ide.ext.runner.client.tabs.container.tab.Background.GREY;
import static org.eclipse.che.ide.ext.runner.client.tabs.container.tab.Background.BLUE;

/**
 * @author Andrienko Alexander
 */
@RunWith(MockitoJUnitRunner.class)
public class BackgroundTest {
    @Test
    public void shouldReturnTabHeight1() throws Exception {
        assertThat(BLACK.toString(), is("#313335"));
    }

    @Test
    public void shouldReturnTabHeight2() throws Exception {
        assertThat(GREY.toString(), is("#474747"));
    }

    @Test
    public void shouldReturnTabHeight3() throws Exception {
        assertThat(BLUE.toString(), is("#256c9f"));
    }
}