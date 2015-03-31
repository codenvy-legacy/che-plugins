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
package org.eclipse.che.ide.ext.datasource.server.ssl;

import javax.ws.rs.Path;

import com.google.inject.Inject;

/**
 * JaxRS service that gives access to Java SSL KeyStore.
 */
@Path("ssl-keystore")
public class SslKeyStoreService {

    protected KeyStoreObject   keyStoreObject;
    protected TrustStoreObject trustStoreObject;

    // userProfileDao, injected with ...
    @Inject
    public SslKeyStoreService(KeyStoreObject keyStoreObject, TrustStoreObject trustStoreObject) {
        this.keyStoreObject = keyStoreObject;
        this.trustStoreObject = trustStoreObject;
    }

    @Path("keystore")
    public KeyStoreObject getClientKeyStore() throws Exception {
        return keyStoreObject;
    }

    @Path("truststore")
    public TrustStoreObject getTrustStore() throws Exception {
        return trustStoreObject;
    }


    public static String getDefaultTrustorePassword() {
        if (System.getProperty("com.codenvy.security.masterpwd") == null) {
            System.setProperty("com.codenvy.security.masterpwd", "changeMe");
        }
        return System.getProperty("com.codenvy.security.masterpwd");
    }

    public static String getDefaultKeystorePassword() {
        if (System.getProperty("com.codenvy.security.masterpwd") == null) {
            System.setProperty("com.codenvy.security.masterpwd", "changeMe");
        }
        return System.getProperty("com.codenvy.security.masterpwd");
    }


}
