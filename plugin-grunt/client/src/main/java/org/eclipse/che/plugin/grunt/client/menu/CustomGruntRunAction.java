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
package org.eclipse.che.plugin.grunt.client.menu;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.api.project.shared.dto.RunnersDescriptor;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.plugin.grunt.client.GruntResources;
import org.eclipse.che.plugin.grunt.client.presenter.SelectGruntTaskPagePresenter;

import com.google.inject.Inject;

import java.util.logging.Logger;

/**
 * Action that perform a custom run.
 * @author Florent Benoit
 */
public class CustomGruntRunAction extends Action {

    private static final Logger LOG = Logger.getLogger(CustomGruntRunAction.class.getName());

    private final AnalyticsEventLogger analyticsEventLogger;

    private AppContext appContext;

    private SelectGruntTaskPagePresenter selectGruntTaskPagePresenter;

    @Inject
    public CustomGruntRunAction(LocalizationConstant localizationConstant,
                                AppContext appContext,
                                AnalyticsEventLogger analyticsEventLogger,
                                GruntResources gruntResources,
                                SelectGruntTaskPagePresenter selectGruntTaskPagePresenter) {
        super(localizationConstant.gruntCustomRunText(), localizationConstant.gruntCustomRunDescription(), null,
              gruntResources.customRunIcon());
        this.appContext = appContext;
        this.analyticsEventLogger = analyticsEventLogger;
        this.selectGruntTaskPagePresenter = selectGruntTaskPagePresenter;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        analyticsEventLogger.log(this);
        selectGruntTaskPagePresenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            final RunnersDescriptor runners = currentProject.getProjectDescription().getRunners();
            String defaultRunner = null;
            if (runners != null) {
                defaultRunner = runners.getDefault();
            }
            if (defaultRunner != null && defaultRunner.contains("grunt")) {
                e.getPresentation().setVisible(true);
            } else {
                e.getPresentation().setVisible(false);
            }
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
