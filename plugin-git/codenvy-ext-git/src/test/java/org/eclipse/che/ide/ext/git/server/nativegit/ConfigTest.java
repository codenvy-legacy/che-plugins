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
package org.eclipse.che.ide.ext.git.server.nativegit;

import org.eclipse.che.api.core.util.LineConsumerFactory;
import org.eclipse.che.ide.ext.git.server.GitConnection;
import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.shared.GitUser;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Eugene Voevodin
 */
public class ConfigTest extends BaseTest {

    private static final String PROPERTY_NAME         = "difftool.prompt";
    private static final String INVALID_PROPERTY_NAME = "someInvalidProperty";
    private static final String PROPERTY_VALUE        = "testValue";

    private GitConnection connection;

    @BeforeMethod
    protected void setUp() throws Exception {
        connection = connectionFactory.getConnection(getRepository().toFile(),
                newDTO(GitUser.class).withName("user").withEmail("user@email.com"),
                LineConsumerFactory.NULL);
    }

    @Test
    public void testAddProperty() throws Exception {
        //write value
        connection.getConfig().add(PROPERTY_NAME, PROPERTY_VALUE);

        //read written value
        String resultValue = connection.getConfig().get(PROPERTY_NAME);

        //clear
        connection.getConfig().unset(PROPERTY_NAME);

        assertEquals(resultValue, PROPERTY_VALUE);
    }

    @Test(expectedExceptions = GitException.class,
            expectedExceptionsMessageRegExp = "error: key does not contain a section: "
                    + INVALID_PROPERTY_NAME + "\n")
    public void testShouldWarnOnInvalidPropertySetting() throws Exception {
        connection.getConfig().add(INVALID_PROPERTY_NAME, PROPERTY_VALUE);
    }

    @Test(expectedExceptions = GitException.class, expectedExceptionsMessageRegExp = "")
    public void testShouldReturnEmptyValueForParameter() throws Exception {
        connection.getConfig().get(PROPERTY_NAME);
    }
}
