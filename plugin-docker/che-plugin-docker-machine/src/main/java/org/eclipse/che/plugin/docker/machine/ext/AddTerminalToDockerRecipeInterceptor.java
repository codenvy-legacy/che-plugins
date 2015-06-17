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
package org.eclipse.che.plugin.docker.machine.ext;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.che.api.machine.shared.recipe.Recipe;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Alexander Garagatyi
 */
public class AddTerminalToDockerRecipeInterceptor implements MethodInterceptor {
    public final static String TERMINAL_DOCKERFILE_INSTRUCTIONS = "machine.server.terminal.dockerfile_instructions";

    @Inject
    @Named(TERMINAL_DOCKERFILE_INSTRUCTIONS)
    private String dockerfileInstructions;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        final Object[] arguments = methodInvocation.getArguments();
        Recipe recipe = (Recipe)arguments[0];
        recipe.setScript(recipe.getScript().concat(dockerfileInstructions));
        return methodInvocation.proceed();
    }
}
