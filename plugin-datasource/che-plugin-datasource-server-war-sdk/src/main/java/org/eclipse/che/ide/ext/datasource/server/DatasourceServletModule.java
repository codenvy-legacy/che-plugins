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
package org.eclipse.che.ide.ext.datasource.server;

import org.everrest.guice.servlet.GuiceEverrestServlet;

import org.eclipse.che.inject.DynaModule;
import com.google.inject.servlet.ServletModule;

/**
 * {@link ServletModule} definition for the server-side part of the datasource plugin.
 *
 * @author "Mickaël Leduque"
 */
@DynaModule
public class DatasourceServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        serve("/*").with(GuiceEverrestServlet.class);
    }
}
