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
package org.eclipse.che.ide.ext.openshift.client.project;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.api.action.AbstractPerspectiveAction;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.ext.openshift.client.project.wizard.CreateProjectPresenter;

import javax.validation.constraints.NotNull;
import java.util.Collections;

import static org.eclipse.che.ide.workspace.perspectives.project.ProjectPerspective.PROJECT_PERSPECTIVE_ID;

/**
 * Action to handle create new application request.
 *
 * @author Vlad Zhukovskiy
 */
@Singleton
public class CreateApplicationFromTemplateAction extends AbstractPerspectiveAction {

    private final AnalyticsEventLogger   eventLogger;
    private final CreateProjectPresenter wizard;

    @Inject
    public CreateApplicationFromTemplateAction(final AnalyticsEventLogger eventLogger,
                                               final CreateProjectPresenter wizard) {
        super(Collections.singletonList(PROJECT_PERSPECTIVE_ID), "Create Application From Template", null, null, null);
        this.eventLogger = eventLogger;
        this.wizard = wizard;
    }

    /** {@inheritDoc} */
    @Override
    public void updateInPerspective(@NotNull ActionEvent event) {
        event.getPresentation().setEnabledAndVisible(true);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        wizard.createWizardAndShow();
    }
}
