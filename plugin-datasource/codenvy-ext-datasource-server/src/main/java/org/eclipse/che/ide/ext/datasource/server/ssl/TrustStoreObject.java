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

import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Iterator;

import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * The trustStore is pretty similar to the keystore except that - it doesn't take key
 */
public class TrustStoreObject extends KeyStoreObject {


    @Inject
    public TrustStoreObject(@Named("api.endpoint") String apiUrl) throws Exception {
        super(apiUrl);
    }


    @Override
    protected String getKeyStorePassword() {
        return SslKeyStoreService.getDefaultTrustorePassword();
    }

    @Override
    protected String getKeyStorePreferenceName() {
        return TRUST_STORE_PREF_ID;
    }

    @Override
    public Response addNewKeyCertificateAndRespond(@QueryParam("alias") String alias,
                                                   Iterator<FileItem> uploadedFilesIterator) throws Exception {
        addNewServerCACert(alias, uploadedFilesIterator);
        return Response.ok("", MediaType.TEXT_HTML).build();
    }

    public void addNewServerCACert(String alias, Iterator<FileItem> uploadedFilesIterator) throws Exception {
        Certificate[] certs = null;
        while (uploadedFilesIterator.hasNext()) {
            FileItem fileItem = uploadedFilesIterator.next();
            if (!fileItem.isFormField()) {
                if ("certFile".equals(fileItem.getFieldName())) {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    certs = cf.generateCertificates(fileItem.getInputStream()).toArray(new Certificate[]{});
                }
            }
        }

        if (certs == null) {
            throw new WebApplicationException(Response.ok("<pre>Can't find input file.</pre>", MediaType.TEXT_HTML).build());
        }

        keystore.setCertificateEntry(alias, certs[0]);
        save();
    }
}
