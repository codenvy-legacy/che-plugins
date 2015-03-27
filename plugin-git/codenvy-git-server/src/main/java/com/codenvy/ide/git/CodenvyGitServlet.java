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
package com.codenvy.ide.git;

import org.eclipse.jgit.http.server.GitServlet;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Enumeration;

/**
 * Added to make possible to override base-path init param with guice-configured value.
 * @author Max Shaposhnik (mshaposhnik@codenvy.com) on 1/5/15.
 */
@Singleton
public class CodenvyGitServlet extends GitServlet {

    @Inject
    @Named("vfs.local.fs_root_dir")
    String vfsRoot;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(new ServletConfig() {
            @Override
            public String getServletName() {
                return config.getServletName();
            }

            @Override
            public ServletContext getServletContext() {
                return config.getServletContext();
            }

            @Override
            public String getInitParameter(String s) {
                if (!s.equals("base-path")) {
                    return config.getInitParameter(s);
                } else {
                    return vfsRoot;
                }
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return config.getInitParameterNames();
            }
        });
    }
}
