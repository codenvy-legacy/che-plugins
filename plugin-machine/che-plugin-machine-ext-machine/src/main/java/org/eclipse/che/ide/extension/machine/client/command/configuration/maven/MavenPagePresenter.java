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
package org.eclipse.che.ide.extension.machine.client.command.configuration.maven;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.command.configuration.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.ConfigurationPage;

/**
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MavenPagePresenter implements MavenPageView.ActionDelegate, ConfigurationPage {

    protected final MavenPageView view;

    @Inject
    public MavenPagePresenter(MavenPageView view) {
        this.view = view;
        view.setDelegate(this);
    }

    @Override
    public void reset(CommandConfiguration configuration) {
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        view.setCommandLine("mvn clean install");
    }
}
