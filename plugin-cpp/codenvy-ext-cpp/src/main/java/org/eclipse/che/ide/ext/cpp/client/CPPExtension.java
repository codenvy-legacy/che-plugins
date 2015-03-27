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
package org.eclipse.che.ide.ext.cpp.client;

import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.filetypes.FileTypeRegistry;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.ide.ext.cpp.shared.ProjectAttributes;
import com.google.gwt.resources.client.ClientBundle;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.vectomatic.dom.svg.ui.SVGResource;

/** @author Vladyslav Zhukovskii */
@Singleton
@Extension(title = "C++", version = "3.0.0")
public class CPPExtension {
    public interface ParserResource extends ClientBundle {
        @Source("org/eclipse/che/ide/ext/cpp/client/image/cpp-category.svg")
        SVGResource cppCategoryIcon();

        @Source("org/eclipse/che/ide/ext/cpp/client/image/cpp.svg")
        SVGResource cppFile();

        @Source("org/eclipse/che/ide/ext/cpp/client/image/h.svg")
        SVGResource hFile();
    }

    @Inject
    public CPPExtension(ParserResource parserResource, IconRegistry iconRegistry, FileTypeRegistry fileTypeRegistry,
                        @Named("CPPFileType") FileType cppFile, @Named("HFileType") FileType hFile) {
        fileTypeRegistry.registerFileType(cppFile);
        fileTypeRegistry.registerFileType(hFile);

        iconRegistry.registerIcon(new Icon(ProjectAttributes.CPP_CATEGORY + ".samples.category.icon", parserResource.cppCategoryIcon()));
        iconRegistry.registerIcon(new Icon("cpp/h.file.small.icon", parserResource.hFile()));
        iconRegistry.registerIcon(new Icon("cpp/cpp.file.small.icon", parserResource.cppFile()));
    }
}
