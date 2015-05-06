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

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class EnvironmentIdValidatorTest {
    @Test
    public void urlShouldBeValid() throws Exception {
        assertTrue(EnvironmentIdValidator.isValid("system://lala"));
    }

    @Test
    public void urlShouldBeValid2() throws Exception {
        assertTrue(EnvironmentIdValidator.isValid("project:/la.com"));
    }

    @Test
    public void urlShouldBeValid3() throws Exception {
        assertTrue(EnvironmentIdValidator.isValid("project://la%20la"));
    }

    @Test
    public void urlShouldBeValid4() throws Exception {
        assertTrue(EnvironmentIdValidator.isValid("system://la%20la"));
    }

    @Test
    public void urlShouldNotBeValid() throws Exception {
        assertFalse(EnvironmentIdValidator.isValid("project://valid .com"));
    }

    @Test
    public void urlShouldNotBeValid2() throws Exception {
        assertFalse(EnvironmentIdValidator.isValid("project://valid "));
    }

    @Test
    public void urlShouldNotBeValid3() throws Exception {
        assertFalse(EnvironmentIdValidator.isValid(""));
    }

    @Test
    public void urlShouldNotBeValid4() throws Exception {
        assertFalse(EnvironmentIdValidator.isValid("project:///notvalid"));
    }
}
