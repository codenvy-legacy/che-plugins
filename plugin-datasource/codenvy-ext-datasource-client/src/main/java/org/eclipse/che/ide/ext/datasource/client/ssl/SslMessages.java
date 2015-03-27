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
package org.eclipse.che.ide.ext.datasource.client.ssl;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;

@DefaultLocale("en")
public interface SslMessages extends Messages {
    @DefaultMessage("Cancel")
    String cancelButton();

    @DefaultMessage("Upload")
    String uploadButton();

    @DefaultMessage("Key/Cert files")
    String fileNameFieldTitle();

    @DefaultMessage("Alias")
    String keyAlias();

    @DefaultMessage("Alias can not be empty")
    String aliasValidationError();

    @DefaultMessage("Upload Private Key")
    String uploadClientSslKey();

    @DefaultMessage("Upload Trust Certificate")
    String uploadServerSslCert();

    @DefaultMessage("SSL Keystore")
    String sslManagerTitle();

    @DefaultMessage("Keys And Certificates")
    String sslManagerCategory();

    @DefaultMessage("Do you want to delete ssh keys for <b>{0}</b>")
    String deleteSslKeyQuestion(String alias);

    @DefaultMessage("Upload SSL client key")
    String dialogUploadSslKeyTitle();

    @DefaultMessage("Upload SSL server trust certificate")
    String dialogUploadSslTrustCertTitle();

    @DefaultMessage("SSL Trust Certificates")
    String headerTrustList();

    @DefaultMessage("SSL Private Key Store")
    String headerKeyList();
}
