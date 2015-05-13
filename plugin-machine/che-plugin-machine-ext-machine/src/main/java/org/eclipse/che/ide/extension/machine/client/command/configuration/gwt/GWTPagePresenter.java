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

import org.eclipse.che.ide.extension.machine.client.command.configuration.ConfigurationPage;

import javax.annotation.Nonnull;

/**
 * Page allows to configure GWT-command parameters.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class GWTPagePresenter implements GWTPageView.ActionDelegate, ConfigurationPage<GWTCommandConfiguration> {

    private final GWTPageView             view;
    private       GWTCommandConfiguration configuration;

    @Inject
    public GWTPagePresenter(GWTPageView view) {
        this.view = view;
        view.setDelegate(this);
    }

    @Override
    public void resetFrom(@Nonnull GWTCommandConfiguration configuration) {
        this.configuration = configuration;
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
