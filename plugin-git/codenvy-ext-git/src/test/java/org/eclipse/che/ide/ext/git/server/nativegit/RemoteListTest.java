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

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.api.core.util.LineConsumerFactory;
import org.eclipse.che.ide.ext.git.server.GitConnection;
import org.eclipse.che.ide.ext.git.shared.CloneRequest;
import org.eclipse.che.ide.ext.git.shared.Remote;
import org.eclipse.che.ide.ext.git.shared.RemoteListRequest;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Eugene Voevodin
 */
public class RemoteListTest extends BaseTest {
    @Test
    public void testRemoteList() throws ServerException, URISyntaxException, UnauthorizedException {
        File repository2 = new File(getTarget().toAbsolutePath().toString(), "repository2");
        repository2.mkdir();
        forClean.add(repository2);

        GitConnection connection = connectionFactory
                .getConnection(repository2.getAbsolutePath(), getUser(), LineConsumerFactory.NULL);
        connection.clone(newDTO(CloneRequest.class).withRemoteUri(getRepository().toAbsolutePath().toString()));
        assertEquals(connection.remoteList(newDTO(RemoteListRequest.class)).size(), 1);
        //create new remote
        NativeGit nativeGit = new NativeGit(repository2);
        nativeGit.createRemoteAddCommand()
                .setName("newremote")
                .setUrl("newremote.url")
                .execute();
        assertEquals(connection.remoteList(newDTO(RemoteListRequest.class)).size(), 2);
        RemoteListRequest request = newDTO(RemoteListRequest.class);
        request.setRemote("newremote");
        List<Remote> one = connection.remoteList(request);
        assertEquals(one.get(0).getUrl(), "newremote.url");
        assertEquals(one.size(), 1);
    }
}
