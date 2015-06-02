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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.info;

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.container.TabContainerView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class InfoContainerViewImplTest {

    //additional mock
    @Mock
    private TabContainerView tabContainerView;

    @InjectMocks
    private InfoContainerViewImpl view;

    @Test
    public void tabContainerShouldBeAdded() {
        view.addContainer(tabContainerView);

        verify(view.infoContainer).setWidget(tabContainerView);
    }
}