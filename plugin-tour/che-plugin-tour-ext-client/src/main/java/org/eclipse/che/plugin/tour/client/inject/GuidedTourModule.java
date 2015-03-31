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
package org.eclipse.che.plugin.tour.client.inject;

import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.plugin.tour.client.action.impl.ActionManagerExternalAction;
import org.eclipse.che.plugin.tour.client.action.impl.OpenURLExternalAction;
import org.eclipse.che.plugin.tour.client.action.impl.SetActivePanelExternalAction;
import org.eclipse.che.plugin.tour.client.log.console.ConsoleLogImpl;
import org.eclipse.che.plugin.tour.client.action.ExternalAction;
import org.eclipse.che.plugin.tour.client.hopscotch.HopscotchTour;
import org.eclipse.che.plugin.tour.client.hopscotch.core.HopscotchTourImpl;
import org.eclipse.che.plugin.tour.client.log.Log;
import org.eclipse.che.plugin.tour.client.action.impl.OpenFileExternalAction;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;

import javax.inject.Singleton;

/**
 * Gin Module for injection of Guided Tour plugin.
 * @author Florent Benoit
 */
@ExtensionGinModule
public class GuidedTourModule extends AbstractGinModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {

        // bind logger
        bind(Log.class).to(ConsoleLogImpl.class).in(Singleton.class);

        // bind all actions available
        GinMultibinder<ExternalAction> actions = GinMultibinder.newSetBinder(binder(), ExternalAction.class);
        actions.addBinding().to(OpenFileExternalAction.class);
        actions.addBinding().to(ActionManagerExternalAction.class);
        actions.addBinding().to(SetActivePanelExternalAction.class);
        actions.addBinding().to(OpenURLExternalAction.class);

        // bind hopscotch
        bind(HopscotchTour.class).to(HopscotchTourImpl.class).in(Singleton.class);


    }

}
