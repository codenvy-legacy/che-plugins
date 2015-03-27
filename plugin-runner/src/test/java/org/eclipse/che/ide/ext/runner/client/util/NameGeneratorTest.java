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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Alexander Andrienko
 */
@RunWith(GwtMockitoTestRunner.class)
public class NameGeneratorTest {

    @Test
    public void nameShouldBeGenerated() {
        String expectedName = cutSecond(NameGenerator.PREFIX_NAME + NameGenerator.DATE_TIME_FORMAT.format(new Date()));
        String actualName = cutSecond(NameGenerator.generate());

        assertThat(actualName, is(expectedName));
    }

    private String cutSecond(String name) {
        return name.substring(0, 19) + name.substring(20, name.length());
    }

    @SuppressWarnings("unchecked")
    @Test(expected = UnsupportedOperationException.class)
    public void prepareActionShouldBePerformed() throws Throwable {
        Constructor<NameGenerator> constructor= (Constructor<NameGenerator>) NameGenerator.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        try {
            //creation instance of NameGenerator
            constructor.newInstance();
        } catch (InvocationTargetException exception) {
            assertThat(exception.getCause().getMessage(), is("Creation instance for this class is unsupported operation"));
            throw exception.getCause();
        }
    }
}
