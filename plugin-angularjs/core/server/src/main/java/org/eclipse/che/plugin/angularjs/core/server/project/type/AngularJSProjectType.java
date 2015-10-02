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
package org.eclipse.che.plugin.angularjs.core.server.project.type;

import org.eclipse.che.plugin.angularjs.core.shared.ProjectAttributes;
import org.eclipse.che.api.project.server.type.ProjectType;
import org.eclipse.che.ide.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * @author Vitaliy Parfonov
 * @author Dmitry Shnurenko
 */
@Singleton
public class AngularJSProjectType extends ProjectType {

    @Inject
    public AngularJSProjectType(JSValueProviderFactory jsValueProviderFactory) {
        super(ProjectAttributes.ANGULAR_JS_ID, ProjectAttributes.ANGULAR_JS_NAME, true, false);
        addConstantDefinition(Constants.LANGUAGE, Constants.LANGUAGE, ProjectAttributes.PROGRAMMING_LANGUAGE);
        addConstantDefinition(Constants.FRAMEWORK, Constants.FRAMEWORK, ProjectAttributes.ANGULAR_JS_FRAMEWORK);
        addVariableDefinition(ProjectAttributes.HAS_JS_FILES, "project has js files", false, jsValueProviderFactory);
        setDefaultRunner("system:/javascript/web/grunt");
        addRunnerCategories(Arrays.asList(ProjectAttributes.RUNNER_CATEGORY));
    }
}
