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

import org.eclipse.che.ide.ext.git.server.commons.Util;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


/**
 * @author Alexander Garagatyi
 */
public class IsVcsUrlIsSshTest {


    @Test(dataProvider = "data")
    public void shouldBeAbleToIdentifySshUrl(String url, boolean expected) {
        assertEquals(Util.isSSH(url), expected);
    }

    @DataProvider(name = "data")
    public static Object[][] data() {
        return new Object[][]{
                // not a ssh urls
                {"https://github.com/ssh/ssh.git", false},
                //{"git://ssh.com/ssh/ssh.git", false},
                //{"git://garagatyi@ssh.com/ssh/ssh.git", false},
                {"https://ssh@ssh.com/ssh/ssh.git", false},
                //{"git@github.com/codenvy/cloud-ide.git", false},
                {"garagatyi@git@github.com/codenvy/cloud-ide.git", false},
                //{"ssh://git@github.com:codenvy/cloud-ide.git", false},
                {"git@github.com:456:codenvy/cloud-ide.git", false},
                {"ssh@github.com:789:codenvy/cloud-ide.git", false},
                //{"ssh://garagatyi@review.gerrithub.io:garagatyi/JdbmKeyValueStorage", false},

                // valid ssh urls
                {"git@github.com:codenvy/cloud-ide.git", true},
                {"git@github.com:codenvy/cloud-ide", true},
                {"ssh://git@github.com/codenvy/cloud-ide.git", true},
                {"ssh://git@github.com/codenvy/cloud-ide", true},
                {"ssh://garagatyi@review.gerrithub.io:29418/garagatyi/JdbmKeyValueStorage", true}
        };
    }
}
