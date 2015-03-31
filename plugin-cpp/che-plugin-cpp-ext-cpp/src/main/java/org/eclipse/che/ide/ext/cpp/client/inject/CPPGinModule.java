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
package org.eclipse.che.ide.ext.cpp.client.inject;

import org.eclipse.che.ide.MimeType;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardRegistrar;
import org.eclipse.che.ide.ext.cpp.client.CPPExtension;
import org.eclipse.che.ide.ext.cpp.client.wizard.CPPProjectWizardRegistrar;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/** @author Vladyslav Zhukovskii */
@ExtensionGinModule
public class CPPGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        GinMultibinder.newSetBinder(binder(), ProjectWizardRegistrar.class).addBinding().to(CPPProjectWizardRegistrar.class);
    }

    @Provides
    @Singleton
    @Named("CPPFileType")
    protected FileType provideCPPFile(CPPExtension.ParserResource res) {
        return new FileType("C++ code file", res.cppFile(), MimeType.TEXT_C, "cpp");
    }

    @Provides
    @Singleton
    @Named("HFileType")
    protected FileType provideHFile(CPPExtension.ParserResource res) {
        return new FileType("C++ header file", res.hFile(), MimeType.TEXT_H, "h");
    }
}
