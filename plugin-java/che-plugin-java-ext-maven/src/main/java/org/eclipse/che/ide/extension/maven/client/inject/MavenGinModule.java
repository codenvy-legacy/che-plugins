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
package org.eclipse.che.ide.extension.maven.client.inject;

import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.project.tree.TreeStructureProvider;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardRegistrar;
import org.eclipse.che.ide.extension.maven.client.build.MavenBuildView;
import org.eclipse.che.ide.extension.maven.client.build.MavenBuildViewImpl;
import org.eclipse.che.ide.extension.maven.client.projecttree.MavenNodeFactory;
import org.eclipse.che.ide.extension.maven.client.projecttree.MavenProjectTreeStructureProvider;
import org.eclipse.che.ide.extension.maven.client.wizard.MavenProjectWizardRegistrar;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Singleton;

/**
 * @author Andrey Plotnikov
 * @author Artem Zatsarynnyy
 */
@ExtensionGinModule
public class MavenGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(MavenBuildView.class).to(MavenBuildViewImpl.class).in(Singleton.class);

        install(new GinFactoryModuleBuilder().build(MavenNodeFactory.class));
        GinMultibinder.newSetBinder(binder(), TreeStructureProvider.class).addBinding().to(MavenProjectTreeStructureProvider.class);

        GinMultibinder.newSetBinder(binder(), ProjectWizardRegistrar.class).addBinding().to(MavenProjectWizardRegistrar.class);
    }
}
