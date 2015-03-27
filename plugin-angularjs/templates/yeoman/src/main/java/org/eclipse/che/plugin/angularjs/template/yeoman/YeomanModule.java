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
package org.eclipse.che.plugin.angularjs.template.yeoman;

import org.eclipse.che.inject.DynaModule;
import org.eclipse.che.plugin.angularjs.api.server.AngularProjectTemplateExtension;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * @author Florent Benoit
 */
@DynaModule
public class YeomanModule extends AbstractModule {
        public void configure() {
            Multibinder<AngularProjectTemplateExtension> binder = Multibinder.newSetBinder(binder(), AngularProjectTemplateExtension.class);
            binder.addBinding().to(YeomanTemplateExtension.class);
    }
}
