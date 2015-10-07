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
package org.eclipse.che.ide.ext.java.jdi.client.actions;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ProjectAction;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import org.eclipse.che.ide.ext.java.jdi.client.debug.DebuggerPresenter;

import org.eclipse.che.ide.Constants;

import org.eclipse.che.ide.ext.java.jdi.client.JavaRuntimeResources;

import org.eclipse.che.ide.extension.maven.shared.MavenAttributes;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to run project on runner in debug mode.
 *
 * @author Artem Zatsarynnyy
 * @author Valeriy Svydenko
 */
@Singleton
public class DebugAction extends ProjectAction {

    private final DebuggerPresenter    debuggerPresenter;
    private final AnalyticsEventLogger eventLogger;
    private final AppContext           appContext;

    @Inject
    public DebugAction(DebuggerPresenter debuggerPresenter,
                       JavaRuntimeResources resources,
                       JavaRuntimeLocalizationConstant localizationConstants,
                       AnalyticsEventLogger eventLogger,
                       AppContext appContext) {
        super(localizationConstants.debugAppActionText(), localizationConstants.debugAppActionDescription(), resources.debug());
        this.debuggerPresenter = debuggerPresenter;
        this.eventLogger = eventLogger;
        this.appContext = appContext;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        debuggerPresenter.debug();
    }

    /** {@inheritDoc} */
    @Override
    public void updateProjectAction(ActionEvent e) {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        final String projectTypeId = currentProject.getProjectDescription().getType();
        final String packaging = currentProject.getAttributeValue(MavenAttributes.PACKAGING);

        e.getPresentation().setVisible("war".equals(packaging) || projectTypeId.equals(Constants.CHE_PLUGIN_ID));
        e.getPresentation().setEnabled(currentProject.getRunner() != null && currentProject.getIsRunningEnabled());
    }

}