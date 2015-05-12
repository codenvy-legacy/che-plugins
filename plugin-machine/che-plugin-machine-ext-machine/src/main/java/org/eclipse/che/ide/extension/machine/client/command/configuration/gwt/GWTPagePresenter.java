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
package org.eclipse.che.ide.extension.machine.client.command.configuration.gwt;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.ConfigurationPage;

import javax.annotation.Nonnull;

/**
 * @author Artem Zatsarynnyy
 */
@Singleton
public class GWTPagePresenter implements GWTPageView.ActionDelegate, ConfigurationPage {

    private final GWTPageView             view;
    private       GWTCommandConfiguration configuration;

    @Inject
    public GWTPagePresenter(GWTPageView view) {
        this.view = view;
        view.setDelegate(this);
    }

    @Override
    public void reset(@Nonnull CommandConfiguration configuration) {
        if (!(configuration instanceof GWTCommandConfiguration)) {
            throw new IllegalArgumentException("Configuration should be GWTCommandConfiguration instance only.");
        }
        this.configuration = (GWTCommandConfiguration)configuration;
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);

        view.setDevModeParameters(configuration.getDevModeParameters());
        view.setVmOptionsField(configuration.getVmOptions());
    }

    @Override
    public void onDevModeParametersChanged(String devModeParameters) {
        configuration.setDevModeParameters(devModeParameters);
    }

    @Override
    public void onVmOptionsChanged(String vmOptions) {
        configuration.setVmOptions(vmOptions);
    }
}
