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
package org.eclipse.che.ide.ext.datasource.client.ssl.upload;

import static org.eclipse.che.ide.api.notification.Notification.Type.ERROR;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.ConsolePart;
import org.eclipse.che.ide.ext.datasource.client.ssl.SslKeyStoreClientService;
import org.eclipse.che.ide.ext.datasource.client.ssl.SslMessages;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UploadSslTrustCertDialogPresenter implements UploadSslTrustCertDialogView.ActionDelegate {
    private UploadSslTrustCertDialogView view;
    private SslMessages                  constant;
    private ConsolePart                  console;
    private NotificationManager          notificationManager;
    private AsyncCallback<Void>          callback;
    private SslKeyStoreClientService     sslKeyStoreService;

    @Inject
    public UploadSslTrustCertDialogPresenter(UploadSslTrustCertDialogView view,
                                             SslMessages constant,
                                             ConsolePart console,
                                             SslKeyStoreClientService sslKeyStoreService,
                                             NotificationManager notificationManager) {
        this.view = view;
        this.sslKeyStoreService = sslKeyStoreService;
        this.view.setDelegate(this);
        this.constant = constant;
        this.console = console;
        this.notificationManager = notificationManager;
    }

    public void showDialog(@NotNull AsyncCallback<Void> callback) {
        this.callback = callback;
        view.setMessage("");
        view.setHost("");
        view.setEnabledUploadButton(false);
        view.showDialog();
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void onUploadClicked() {
        String alias = view.getAlias();
        if (alias.isEmpty()) {
            view.setMessage(constant.aliasValidationError());
            return;
        }
        view.setEncoding(FormPanel.ENCODING_MULTIPART);
        view.setAction(sslKeyStoreService.getUploadServerCertAction(alias));
        view.submit();
    }

    @Override
    public void onSubmitComplete(@NotNull String result) {
        if (result.isEmpty()) {
            UploadSslTrustCertDialogPresenter.this.view.close();
            callback.onSuccess(null);
        } else {
            if (result.startsWith("<pre>") && result.endsWith("</pre>")) {
                result = result.substring(5, (result.length() - 6));
            }
            console.print(result);
            Notification notification = new Notification(result, ERROR);
            notificationManager.showNotification(notification);
            callback.onFailure(new Throwable(result));
        }
    }

    @Override
    public void onFileNameChanged() {
        String certFileName = view.getCertFileName();
        view.setEnabledUploadButton(!certFileName.isEmpty());
    }
}
