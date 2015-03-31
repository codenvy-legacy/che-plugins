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
package org.eclipse.che.plugin.yeoman.client.inject;

import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.plugin.yeoman.client.panel.GeneratedItemView;
import org.eclipse.che.plugin.yeoman.client.panel.YeomanPartViewImpl;

import org.eclipse.che.plugin.yeoman.client.panel.FoldingPanel;
import org.eclipse.che.plugin.yeoman.client.panel.FoldingPanelFactory;
import org.eclipse.che.plugin.yeoman.client.panel.FoldingPanelImpl;
import org.eclipse.che.plugin.yeoman.client.panel.GeneratedItemViewFactory;
import org.eclipse.che.plugin.yeoman.client.panel.GeneratedItemViewImpl;
import org.eclipse.che.plugin.yeoman.client.panel.YeomanPartView;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.Singleton;

/**
 * Gin Module for injection performed in Yeoman
 * @author Florent Benoit
 */
@ExtensionGinModule
public class YeomanModule extends AbstractGinModule {

        /** {@inheritDoc} */
        @Override
        protected void configure() {
            // Add the yeoman panel and its sub elements
            bind(YeomanPartView.class).to(YeomanPartViewImpl.class).in(Singleton.class);
            install(new GinFactoryModuleBuilder().implement(FoldingPanel.class, FoldingPanelImpl.class)
                                                 .build(FoldingPanelFactory.class));
            install(new GinFactoryModuleBuilder().implement(GeneratedItemView.class, GeneratedItemViewImpl.class)
                                                 .build(GeneratedItemViewFactory.class));


        }

}
