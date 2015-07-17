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
package org.eclipse.che.ide.ext.java.client.dependenciesupdater;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.AsyncRequestFactory;

import static org.eclipse.che.ide.MimeType.APPLICATION_JSON;
import static org.eclipse.che.ide.rest.HTTPHeader.ACCEPT;

/**
 * Implementation of {@link JavaNameEnvironmentServiceClient}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class JavaNameEnvironmentServiceClientImpl implements JavaNameEnvironmentServiceClient {
    private final AsyncRequestFactory asyncRequestFactory;
    private       String         extPath;
    private       MachineManager machineManager;

    private final String UPDATE_DEPENDENCIES          = "/jdt/classpath/update";

    @Inject
    protected JavaNameEnvironmentServiceClientImpl(AsyncRequestFactory asyncRequestFactory, @Named("cheExtensionPath") String extPath,
                                                   MachineManager machineManager) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.extPath = extPath;
        this.machineManager = machineManager;
    }

    /** {@inheritDoc} */
    @Override
    public void updateDependencies(String projectPath, boolean force, AsyncRequestCallback<Boolean> callback) {
        final String requestUrl = extPath + UPDATE_DEPENDENCIES + "?projectpath=" + projectPath + "&machineId=" + machineManager.getDeveloperMachineId();
        asyncRequestFactory.createGetRequest(requestUrl)
                           .header(ACCEPT, APPLICATION_JSON)
                           .send(callback);
    }
}
