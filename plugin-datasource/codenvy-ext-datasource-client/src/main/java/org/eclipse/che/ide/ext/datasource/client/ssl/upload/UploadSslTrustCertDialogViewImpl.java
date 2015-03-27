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

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.ext.datasource.client.ssl.SslMessages;
import org.eclipse.che.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UploadSslTrustCertDialogViewImpl extends Window implements UploadSslTrustCertDialogView {
    interface UploadSshKeyViewImplUiBinder extends UiBinder<Widget, UploadSslTrustCertDialogViewImpl> {
    }

    private static UploadSshKeyViewImplUiBinder ourUiBinder = GWT.create(UploadSshKeyViewImplUiBinder.class);

    @UiField
    protected Label message;

    @UiField
    protected TextBox keyAlias;

    protected Button btnCancel;

    protected Button btnUpload;

    @UiField(provided = true)
    final protected SslMessages locale;

    @UiField
    protected FormPanel     uploadForm;
    @UiField
    protected VerticalPanel uploadFormVPanel;
    protected FileUpload    certFile;

    private ActionDelegate delegate;

    @Inject
    protected UploadSslTrustCertDialogViewImpl(SslMessages locale) {
        this.locale = locale;

        Widget widget = ourUiBinder.createAndBindUi(this);

        setTitle(locale.dialogUploadSslTrustCertTitle());
        setWidget(widget);
        btnCancel = createButton(locale.cancelButton(), "SslKeyDialogCancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });

        btnUpload = createButton(locale.uploadButton(), "SslLeyDialogUpload", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onUploadClicked();
            }
        });
        getFooter().add(btnCancel);
        getFooter().add(btnUpload);

        bind();
    }

    private void bind() {
        uploadForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                delegate.onSubmitComplete(event.getResults());
            }
        });
    }

    @NotNull
    @Override
    public String getAlias() {
        return keyAlias.getText();
    }

    @Override
    public void setHost(@NotNull String host) {
        keyAlias.setText(host);
    }

    @NotNull
    @Override
    public String getCertFileName() {
        return certFile.getFilename();
    }

    @Override
    public void setEnabledUploadButton(boolean enabled) {
        btnUpload.setEnabled(enabled);
    }

    @Override
    public void setMessage(@NotNull String message) {
        this.message.setText(message);
    }

    @Override
    public void setEncoding(@NotNull String encodingType) {
        uploadForm.setEncoding(encodingType);
    }

    @Override
    public void setAction(@NotNull String url) {
        uploadForm.setAction(url);
        uploadForm.setMethod(FormPanel.METHOD_POST);
    }

    @Override
    public void submit() {
        uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
        uploadForm.submit();
    }

    @Override
    public void showDialog() {
        uploadFormVPanel.clear();
        certFile = new FileUpload();
        certFile.setHeight("22px");
        certFile.setWidth("100%");
        certFile.setName("certFile");
        certFile.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                delegate.onFileNameChanged();
            }
        });
        uploadFormVPanel.add(certFile);

        this.show();
    }

    @Override
    public void close() {
        hide();

        uploadForm.remove(certFile);
        certFile = null;
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onClose() {
    }
}
