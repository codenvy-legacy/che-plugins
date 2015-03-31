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
import org.eclipse.che.ide.ext.git.shared.InitRequest;

import org.testng.annotations.Test;

import java.io.File;
import java.net.URISyntaxException;


import static org.testng.Assert.assertTrue;

/**
 * @author Eugene Voevodin
 */
public class InitTest extends BaseTest {

    @Test
    public void testInit() throws GitException, URISyntaxException {
        //given
        File destination = new File(getTarget().toAbsolutePath().toString(), "repository2");
        destination.mkdir();
        forClean.add(destination);
        GitConnection connection = connectionFactory.getConnection(destination,
                newDTO(GitUser.class).withName("user").withEmail("user@email.com"), LineConsumerFactory.NULL);
        //when
        connection.init(newDTO(InitRequest.class).withWorkingDir(null).withBare(false));
        //then
        assertTrue(new File(destination, ".git").exists());
    }
}
