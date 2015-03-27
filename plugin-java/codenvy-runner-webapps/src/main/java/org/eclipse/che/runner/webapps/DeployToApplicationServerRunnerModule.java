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
package org.eclipse.che.runner.webapps;

import org.eclipse.che.api.core.util.SystemInfo;
import org.eclipse.che.api.runner.internal.Runner;
import org.eclipse.che.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/** @author Eugene Voevodin */
@DynaModule
public class DeployToApplicationServerRunnerModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Runner> multiBinderRunners = Multibinder.newSetBinder(binder(), Runner.class);
        multiBinderRunners.addBinding().to(DeployToApplicationServerRunner.class);
        Multibinder<ApplicationServer> multiBinderServers = Multibinder.newSetBinder(binder(), ApplicationServer.class);

        if (SystemInfo.isUnix()) {
            multiBinderServers.addBinding().to(UnixTomcatServer.class);
        } else if (SystemInfo.isWindows()) {
            multiBinderServers.addBinding().to(WindowsTomcatServer.class);
        } else throw new UnsupportedOperationException();
    }
}