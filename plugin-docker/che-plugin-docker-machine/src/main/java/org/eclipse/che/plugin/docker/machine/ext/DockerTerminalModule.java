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

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

import org.eclipse.che.api.machine.shared.Recipe;
import org.eclipse.che.plugin.docker.machine.DockerInstanceProvider;

import java.lang.reflect.Method;

/**
 * Guice module for terminal feature in docker machines
 *
 * @author Alexander Garagatyi
 */
// Not a DynaModule, install manually
public class DockerTerminalModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DockerMachineTerminalLauncher.class).asEagerSingleton();

        bindConstant().annotatedWith(Names.named(DockerMachineTerminalLauncher.START_TERMINAL_COMMAND))
                      .to("~/che/terminal/terminal -addr :4411 -cmd /bin/sh -static ~/che/terminal/");

        AddTerminalToDockerRecipeInterceptor addTerminalToDockerRecipeInterceptor = new AddTerminalToDockerRecipeInterceptor();

        requestInjection(addTerminalToDockerRecipeInterceptor);

        bindInterceptor(Matchers.subclassesOf(DockerInstanceProvider.class), new AbstractMatcher<Method>() {
                            @Override
                            public boolean matches(Method method) {
                                if ("createInstance".equals(method.getName())) {
                                    final Class<?>[] parameterTypes = method.getParameterTypes();
                                    if (parameterTypes.length > 0) {
                                        if (parameterTypes[0].isAssignableFrom(Recipe.class)) {
                                            return true;
                                        }
                                    }
                                }
                                return false;
                            }
                        },
                        addTerminalToDockerRecipeInterceptor);


        bind(String.class).annotatedWith(Names.named(AddTerminalToDockerRecipeInterceptor.TERMINAL_DOCKERFILE_INSTRUCTIONS)).toProvider(
                TerminalDockerfileInstructionsProvider.class);
    }
}
