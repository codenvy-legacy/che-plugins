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
package org.eclipse.che.ide.ext.php.server.project.type;

import com.google.inject.Inject;

import org.eclipse.che.api.project.server.type.ProjectType;
import org.eclipse.che.api.project.shared.Constants;
import org.eclipse.che.ide.ext.php.shared.ProjectAttributes;

import java.util.Arrays;

import static org.eclipse.che.ide.api.project.type.RunnerCategory.PHP;

/**
 * @author Vitaly Parfonov
 * @author Dmitry Shnurenko
 */
public class PhpProjectType extends ProjectType {

    @Inject
    public PhpProjectType(PhpValueProviderFactory phpValueProviderFactory) {
        super(ProjectAttributes.PHP_ID, ProjectAttributes.PHP_NAME, true, false);
        addConstantDefinition(Constants.LANGUAGE, "language", ProjectAttributes.PROGRAMMING_LANGUAGE);
        addVariableDefinition(ProjectAttributes.HAS_PHP_FILES, "project has php files", false, phpValueProviderFactory);
        addRunnerCategories(Arrays.asList(PHP.toString()));
    }
}
