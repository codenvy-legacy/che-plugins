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
package org.eclipse.che.ide.ext.git.server;

import org.eclipse.che.ide.ext.git.server.commons.Util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author andrew00x
 */
public class GetHostNameFromSshUrlTest {
    @Test
    public void testSshWithAuthority() {
        Assert.assertEquals("host.com", Util.getHost("ssh://user@host.com/some/path"));
    }

    @Test
    public void testSsh() {
        Assert.assertEquals("host.com", Util.getHost("ssh://host.com/some/path"));
    }

    @Test
    public void testGit() {
        Assert.assertEquals("host.com", Util.getHost("git://host.com/user/repo"));
    }

    @Test
    public void testSshWithPort() {
        Assert.assertEquals("host.com", Util.getHost("ssh://host.com:port/some/path"));
    }

    @Test
    public void testWithAuthority() {
        Assert.assertEquals("host.com", Util.getHost("user@host.com:login/repo"));
    }
}
