/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.gulp.runner;

import org.eclipse.che.api.runner.internal.Runner;
import org.eclipse.che.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Module for Gulp module.
 *
 * @author Florent Benoit
 */
@DynaModule
public class GulpModule extends AbstractModule {

    @Override
    protected void configure() {

        Multibinder<Runner> multiBinderRunners = Multibinder.newSetBinder(binder(), Runner.class);
        multiBinderRunners.addBinding().to(GulpRunner.class);

    }
}
