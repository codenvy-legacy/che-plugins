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
package org.eclipse.che.ide.ext.cpp.server.inject;

import org.eclipse.che.api.project.server.ValueProviderFactory;
import org.eclipse.che.api.project.server.type.ProjectType;
import org.eclipse.che.ide.ext.cpp.server.project.type.CPPProjectType;
import org.eclipse.che.ide.ext.cpp.server.project.type.CPPValueProviderFactory;
import org.eclipse.che.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

@DynaModule
public class CPPModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), ProjectType.class).addBinding().to(CPPProjectType.class);
        Multibinder.newSetBinder(binder(), ValueProviderFactory.class).addBinding().to(CPPValueProviderFactory.class);
    }
}
