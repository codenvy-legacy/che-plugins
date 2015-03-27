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

public class CodenvySSLSocketFactoryKeyStoreSettings {

    protected byte[] ksContent;
    protected String ksPassword;
    protected byte[] tsContent;
    protected String tsPassword;

    public CodenvySSLSocketFactoryKeyStoreSettings(byte[] sslKeyStoreContent,
                                                   String keyStorePassword,
                                                   byte[] sslTrustStoreContent,
                                                   String trustStorePassword) {
        ksContent = sslKeyStoreContent;
        ksPassword = keyStorePassword;
        tsContent = sslTrustStoreContent;
        tsPassword = trustStorePassword;
    }

    public CodenvySSLSocketFactoryKeyStoreSettings() {
    }

    public String getKeyStorePassword() {
        return ksPassword;
    }

    public byte[] getKeyStoreContent() {
        return ksContent;
    }

    public byte[] getTrustStoreContent() {
        return tsContent;
    }

    public String getTrustStorePassword() {
        return tsPassword;
    }

    public CodenvySSLSocketFactoryKeyStoreSettings withKeyStorePassword(String keyStorePassword) {
        ksPassword = keyStorePassword;
        return this;
    }

    public CodenvySSLSocketFactoryKeyStoreSettings withKeyStoreContent(byte[] sslKeyStoreContent) {
        ksContent = sslKeyStoreContent;
        return this;
    }

    public CodenvySSLSocketFactoryKeyStoreSettings withTrustStoreContent(byte[] sslTrustStoreContent) {
        tsContent = sslTrustStoreContent;
        return this;
    }

    public CodenvySSLSocketFactoryKeyStoreSettings withTrustStorePassword(String trustStorePassword) {
        tsPassword = trustStorePassword;
        return this;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        ksPassword = keyStorePassword;
    }

    public void setKeyStoreContent(byte[] sslKeyStoreContent) {
        ksContent = sslKeyStoreContent;
    }

    public void setTrustStoreContent(byte[] sslTrustStoreContent) {
        tsContent = sslTrustStoreContent;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        tsPassword = trustStorePassword;
    }
}
