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
package org.eclipse.che.plugin.angularjs.core.shared;

/** @author Roman Nikitenko */
public interface ProjectAttributes {
    String BASIC_JS_ID          = "BasicJS";
    String BASIC_JS_NAME        = "BasicJS Project";
    String BASIC_JS_FRAMEWORK   = "BasicJS";

    String GRUNT_ID             = "GruntJS";
    String GRUNT_NAME           = "GruntJS Project";

    String GULP_ID              = "GulpJS";
    String GULP_NAME            = "GulpJS Project";

    String ANGULAR_JS_ID        = "AngularJS";
    String ANGULAR_JS_NAME      = "AngularJS Project";
    String ANGULAR_JS_FRAMEWORK = "AngularJS";

    String RUNNER_CATEGORY      = "javascript";
    String PROGRAMMING_LANGUAGE = "javascript";

    String HAS_CONFIG_FILE      = "hasConfigFile";
    String HAS_JS_FILES         = "hasJSFiles";
}
