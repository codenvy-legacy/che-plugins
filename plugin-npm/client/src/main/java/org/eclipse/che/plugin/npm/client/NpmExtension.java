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
package org.eclipse.che.plugin.npm.client;

import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.extension.builder.client.BuilderLocalizationConstant;
import org.eclipse.che.plugin.npm.client.menu.LocalizationConstant;
import org.eclipse.che.plugin.npm.client.menu.NpmInstallAction;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Extension registering NPM commands
 * @author Florent Benoit
 */
@Singleton
@Extension(title = "NPM")
public class NpmExtension {

    @Inject
    public NpmExtension(ActionManager actionManager,
                          BuilderLocalizationConstant builderLocalizationConstant,
                          LocalizationConstant localizationConstantNpm,
                          final NpmInstallAction npmInstallAction) {

        // Register action
        actionManager.registerAction(localizationConstantNpm.npmInstallId(), npmInstallAction);


        // For now the NPM dependencies are installed by the runner

        // Get Build menu
        //DefaultActionGroup buildMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_BUILD);

        // create constraint
        //Constraints afterBuildConstraints = new Constraints(AFTER, builderLocalizationConstant.buildProjectControlId());

        // Add NPM in build menu
        //buildMenuActionGroup.add(npmInstallAction, afterBuildConstraints);

    }
}
