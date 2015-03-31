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
package org.eclipse.che.ide.ext.php.client;

import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.ide.ext.php.shared.ProjectAttributes;
import com.google.gwt.resources.client.ClientBundle;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGResource;

/** @author Vladyslav Zhukovskii */
@Singleton
@Extension(title = "PHP", version = "3.0.0")
public class PHPExtension {
    @Inject
    public PHPExtension(ParserResource parserResource, IconRegistry iconRegistry) {
        iconRegistry.registerIcon(new Icon(ProjectAttributes.PHP_CATEGORY + ".samples.category.icon", parserResource.phpCategoryIcon()));
    }

    public interface ParserResource extends ClientBundle {
        @Source("org/eclipse/che/ide/ext/php/client/image/php.svg")
        SVGResource phpCategoryIcon();
    }
}
