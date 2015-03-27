/*******************************************************************************
 * Copyright (c) 2014-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.editor.codemirror.base.client.inject;

import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.editor.codemirror.base.client.BaseCodemirrorInitializer;
import org.eclipse.che.ide.editor.codemirror.base.client.BaseCodemirrorPromise;
import org.eclipse.che.ide.editor.codemirror.base.client.MinimalCodemirrorInitializer;
import org.eclipse.che.ide.editor.codemirror.base.client.MinimalCodemirrorPromise;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

@ExtensionGinModule
public class BaseCodeMirrorInitGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(BaseCodemirrorPromise.class).in(Singleton.class);
        bind(BaseCodemirrorInitializer.class);
        bind(MinimalCodemirrorPromise.class).in(Singleton.class);
        bind(MinimalCodemirrorInitializer.class);
    }
}
