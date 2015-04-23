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
package org.eclipse.che.ide.extension.machine.client.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.extension.machine.client.console.MachineConsoleToolbar;
import org.eclipse.che.ide.extension.machine.client.console.MachineConsoleView;
import org.eclipse.che.ide.extension.machine.client.console.MachineConsoleViewImpl;
import org.eclipse.che.ide.ui.toolbar.ToolbarPresenter;

/** @author Artem Zatsarynnyy */
@ExtensionGinModule
public class MachineGinModule extends AbstractGinModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(MachineConsoleView.class).to(MachineConsoleViewImpl.class).in(Singleton.class);
        bind(ToolbarPresenter.class).annotatedWith(MachineConsoleToolbar.class).to(ToolbarPresenter.class).in(Singleton.class);
    }
}
