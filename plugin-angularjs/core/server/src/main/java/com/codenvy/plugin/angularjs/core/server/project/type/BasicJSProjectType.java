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
package com.codenvy.plugin.angularjs.core.server.project.type;

import org.eclipse.che.api.project.server.type.ProjectType;
import org.eclipse.che.ide.Constants;

import javax.inject.Singleton;
import java.util.Arrays;

import static org.eclipse.che.ide.api.project.type.RunnerCategory.JAVASCRIPT;

/**
 * @author Vitaliy Parfonov
 * @author Dmitry Shnurenko
 */
@Singleton
public class BasicJSProjectType extends ProjectType {

    public BasicJSProjectType() {
        super("BasicJS", "BasicJS Project", true, false);
        addConstantDefinition(Constants.LANGUAGE, Constants.LANGUAGE, "javascript");
        addConstantDefinition(Constants.FRAMEWORK, Constants.FRAMEWORK, "BasicJS");
        setDefaultRunner("system:/java/web/tomcat7");
        addRunnerCategories(Arrays.asList(JAVASCRIPT.toString()));
    }

}
