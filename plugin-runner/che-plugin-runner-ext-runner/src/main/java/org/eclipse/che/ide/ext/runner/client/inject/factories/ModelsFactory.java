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
package org.eclipse.che.ide.ext.runner.client.inject.factories;

import org.eclipse.che.api.project.shared.dto.RunnerEnvironment;
import org.eclipse.che.api.runner.dto.RunOptions;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * The factory for creating an instances of different models which use in the project.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public interface ModelsFactory {

    /**
     * Creates a runner with runner options without environment name. It means the title of the runner will be generated without additional
     * suffix.
     *
     * @param runOptions
     *         options which needs to be used
     * @return an instance of {@link Runner}
     */
    @NotNull
    Runner createRunner(@NotNull RunOptions runOptions);

    /**
     * Creates a runner with runner options and environment name.  It means the title of the runner will be generated with additional
     * suffix.
     *
     * @param runOptions
     *         options which needs to be used
     * @param scope
     *         scope value of current environment
     * @param environmentName
     *         additional part of name for runner
     * @return an instance of {@link Runner}
     */
    @NotNull
    Runner createRunner(@NotNull RunOptions runOptions, @NotNull Scope scope, @Nullable String environmentName);

    /**
     * Creates environments with environment and scope.
     *
     * @param runnerEnvironment
     *         runner environment which we get from server
     * @param scope
     *         scope which need set to environment
     * @return an instance of {@link Environment}
     */
    @NotNull
    Environment createEnvironment(@NotNull RunnerEnvironment runnerEnvironment, @NotNull Scope scope);

}