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
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

/**
 * Guice module that adds volume for local storage in docker dev machines
 *
 * @author Alexander Garagatyi
 */
// Not a DynaModule, install manually
public class LocalStorageModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<String> volumesMultibinder =
                Multibinder.newSetBinder(binder(), String.class, Names.named("machine.docker.dev_machine.machine_volumes"));
        volumesMultibinder.addBinding().toProvider(LocalStorageDockerVolumePathProvider.class);
    }

    /**
     * Provides volumes configuration of machine for local storage
     *
     * @author Alexander Garagatyi
     */
    private class LocalStorageDockerVolumePathProvider implements Provider<String> {
        @Inject
        @Named("local.storage.path")
        private String localStoragePath;

        @Override
        public String get() {
            return localStoragePath + ":/local-storage";
        }
    }
}
