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
package org.eclipse.che.ide.ext.runner.client.state;

import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.eclipse.che.ide.ext.runner.client.state.State.RUNNERS;
import static org.eclipse.che.ide.ext.runner.client.state.State.TEMPLATE;
import static org.hamcrest.core.Is.is;

/**
 * @author Andrienko Alexander
 */
public class StateTest {
    @Test
    public void shouldReturnRunnersValue() {
        assertThat(State.valueOf("RUNNERS"), is(RUNNERS));
    }

    @Test
    public void shouldReturnTemplateValue() {
        assertThat(State.valueOf("TEMPLATE"), is(TEMPLATE));
    }
}