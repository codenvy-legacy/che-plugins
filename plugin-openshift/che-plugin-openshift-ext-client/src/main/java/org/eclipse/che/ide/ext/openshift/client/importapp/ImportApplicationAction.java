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
package org.eclipse.che.ide.ext.openshift.client.importapp;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.api.action.AbstractPerspectiveAction;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.oauth.OpenshiftAuthorizationHandler;

import javax.validation.constraints.NotNull;
import java.util.Collections;

import static org.eclipse.che.ide.workspace.perspectives.project.ProjectPerspective.PROJECT_PERSPECTIVE_ID;

/**
 * Action for importing existing OpenShift application to Che.
 *
 * @author Anna Shumilova
 */
@Singleton
public class ImportApplicationAction extends AbstractPerspectiveAction {

    private final AnalyticsEventLogger          eventLogger;
    private final ImportApplicationPresenter    presenter;
    private final AppContext                    appContext;
    private final OpenshiftAuthorizationHandler openshiftAuthorizationHandler;


    @Inject
    public ImportApplicationAction(final AnalyticsEventLogger eventLogger, OpenshiftLocalizationConstant locale,
                                   final ImportApplicationPresenter presenter,
                                   AppContext appContext,
                                   OpenshiftAuthorizationHandler openshiftAuthorizationHandler) {
        super(Collections.singletonList(PROJECT_PERSPECTIVE_ID), locale.importApplicationAction(),
              locale.linkProjectWithExistingApplicationAction(), null, null);
        this.eventLogger = eventLogger;
        this.presenter = presenter;
        this.appContext = appContext;
        this.openshiftAuthorizationHandler = openshiftAuthorizationHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateInPerspective(@NotNull ActionEvent event) {
        event.getPresentation().setEnabled(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        this.presenter.show();
    }
}
