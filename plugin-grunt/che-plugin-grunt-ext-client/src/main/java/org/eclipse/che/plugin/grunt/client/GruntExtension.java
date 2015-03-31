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
package org.eclipse.che.plugin.grunt.client;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.constraints.Anchor;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.plugin.grunt.client.menu.CustomGruntRunAction;
import org.eclipse.che.plugin.grunt.client.menu.LocalizationConstant;

import static org.eclipse.che.ide.api.action.IdeActions.GROUP_BUILD_TOOLBAR;
import static org.eclipse.che.ide.api.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static org.eclipse.che.ide.api.action.IdeActions.GROUP_RUN;
import static org.eclipse.che.ide.api.constraints.Anchor.AFTER;
import static org.eclipse.che.ide.ext.runner.client.constants.ActionId.RUN_WITH;


/**
 * Extension registering Grunt commands
 *
 * @author Florent Benoit
 */
@Singleton
@Extension(title = "Grunt")
public class GruntExtension {

    @Inject
    public GruntExtension(ActionManager actionManager,
                          LocalizationConstant localizationConstant,
                          CustomGruntRunAction customGruntRunAction) {

        actionManager.registerAction(localizationConstant.gruntCustomRunId(), customGruntRunAction);

        // Get Run menu
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN);

        // Get Main Toolbar
        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);

        // create constraint
        Constraints afterBuildConstraints = new Constraints(AFTER, GROUP_BUILD_TOOLBAR);

        // Add Custom Grunt Run in build menu
        mainToolbarGroup.add(customGruntRunAction, afterBuildConstraints);

        // Add Custom Grunt Run in Run menu
        runMenuActionGroup.add(customGruntRunAction, new Constraints(Anchor.BEFORE, RUN_WITH.getId()));
    }
}
