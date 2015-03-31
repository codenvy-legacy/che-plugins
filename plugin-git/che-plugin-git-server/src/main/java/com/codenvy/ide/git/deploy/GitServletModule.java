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
package com.codenvy.ide.git.deploy;

import com.codenvy.ide.git.CodenvyGitServlet;
import com.codenvy.ide.git.VFSPermissionsFilter;
import org.eclipse.che.inject.DynaModule;
import com.google.inject.servlet.ServletModule;

import java.util.HashMap;
import java.util.Map;

/**
 * Servlet module composer for git war.
 */
@DynaModule
public class GitServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        Map<String, String> initParams = new HashMap<>();
        initParams.put("export-all", "1");
        serve("/*").with(CodenvyGitServlet.class, initParams);
        filter("/*").through(VFSPermissionsFilter.class);
    }
}
