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
package org.eclipse.che.ide.ext.ruby.server.project.type;

import org.eclipse.che.api.project.server.type.ProjectType;
import org.eclipse.che.api.project.shared.Constants;
import org.eclipse.che.ide.ext.ruby.shared.ProjectAttributes;

import java.util.Arrays;

import static org.eclipse.che.ide.api.project.type.RunnerCategory.RUBY;

/**
 * @author Vitaly Parfonov
 * @author Dmitry Shnurenko
 */
public class RubyProjectType extends ProjectType {

    public RubyProjectType() {
        super(ProjectAttributes.RUBY_ID, ProjectAttributes.RUBY_NAME, true, false);
        addConstantDefinition(Constants.LANGUAGE, "language", ProjectAttributes.PROGRAMMING_LANGUAGE);
        addRunnerCategories(Arrays.asList(RUBY.toString()));
    }
}
