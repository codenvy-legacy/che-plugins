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
package org.eclipse.che.ide.ext.cpp.server.project.type;

import com.google.inject.Inject;

import org.eclipse.che.api.project.server.type.ProjectType;
import org.eclipse.che.api.project.shared.Constants;
import org.eclipse.che.ide.ext.cpp.shared.ProjectAttributes;

import java.util.Arrays;

import static org.eclipse.che.ide.api.project.type.RunnerCategory.CPP;

/**
 * @author Vitaly Parfonov
 * @author Dmitry Shnurenko
 */
public class CPPProjectType extends ProjectType {

    @Inject
    public CPPProjectType(CPPValueProviderFactory cppValueProviderFactory) {
        super(ProjectAttributes.CPP_ID, ProjectAttributes.CPP_NAME, true, false);
        addConstantDefinition(Constants.LANGUAGE, "language", ProjectAttributes.PROGRAMMING_LANGUAGE);
        addVariableDefinition(ProjectAttributes.HAS_CPP_FILES, "project has cpp files", false, cppValueProviderFactory);
        addRunnerCategories(Arrays.asList(CPP.toString()));
    }
}
