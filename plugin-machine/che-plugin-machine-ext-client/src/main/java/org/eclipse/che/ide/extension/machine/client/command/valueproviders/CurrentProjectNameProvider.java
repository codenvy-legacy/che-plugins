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
package org.eclipse.che.ide.extension.machine.client.command.valueproviders;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;

import javax.validation.constraints.NotNull;

/**
 * Provides current project's name.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CurrentProjectNameProvider implements CommandPropertyValueProvider {

    private static final String KEY = "${project.current.name}";

    private final AppContext appContext;

    @Inject
    public CurrentProjectNameProvider(AppContext appContext) {
        this.appContext = appContext;
    }

    @NotNull
    @Override
    public String getKey() {
        return KEY;
    }

    @NotNull
    @Override
    public String getValue() {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            return currentProject.getProjectDescription().getName();
        }

        return "";
    }
}
