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
package org.eclipse.che.ide.extension.maven.client.actions;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ProjectAction;
import org.eclipse.che.ide.extension.maven.client.MavenLocalizationConstant;
import org.eclipse.che.ide.extension.maven.client.module.CreateMavenModulePresenter;
import org.eclipse.che.ide.extension.maven.shared.MavenAttributes;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author Evgen Vidolob */
@Singleton
public class CreateMavenModuleAction extends ProjectAction {

    private CreateMavenModulePresenter presenter;

    private final AnalyticsEventLogger eventLogger;

    @Inject
    public CreateMavenModuleAction(MavenLocalizationConstant constant, CreateMavenModulePresenter presenter,
                                   AnalyticsEventLogger eventLogger) {
        super(constant.actionCreateMavenModuleText(), constant.actionCreateMavenModuleDescription());
        this.presenter = presenter;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        if (appContext.getCurrentProject() != null) {
            presenter.showDialog(appContext.getCurrentProject());
        }
    }

    @Override
    public void updateProjectAction(ActionEvent e) {
        e.getPresentation().setVisible(appContext.getCurrentProject().getRootProject().getType().equals("maven"));
        e.getPresentation().setEnabled("pom".equals(appContext.getCurrentProject().getAttributeValue(MavenAttributes.PACKAGING)));
    }
}
