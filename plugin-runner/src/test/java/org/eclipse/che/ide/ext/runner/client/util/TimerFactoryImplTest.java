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
package org.eclipse.che.ide.ext.runner.client.util;

import com.google.gwt.user.client.Timer;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class TimerFactoryImplTest {

    @Mock
    private TimerFactory.TimerCallBack timerCallBack;
    @InjectMocks
    private TimerFactoryImpl timerFactory;

    @Test
    public void shouldCheckNewInstanceIsNotNull() {
        Timer timer = timerFactory.newInstance(timerCallBack);
        assertThat(timer, notNullValue());
    }

    @Test
    public void shouldMakeNewInstance() {
        Timer timer = timerFactory.newInstance(timerCallBack);
        timer.run();
        verify(timerCallBack).onRun();
    }

}