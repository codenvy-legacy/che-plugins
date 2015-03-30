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
package org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Andrey Plotnikov
 */
public class BootTest {

    @Test
    public void contentShouldBeDetected() throws Exception {
        for (Boot boot : Boot.values()) {
            assertThat(boot, is(Boot.detect(boot.toString())));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exceptionShouldBeThrownWhenContentIsIncorrect() throws Exception {
        Boot.detect("some content");
    }

}