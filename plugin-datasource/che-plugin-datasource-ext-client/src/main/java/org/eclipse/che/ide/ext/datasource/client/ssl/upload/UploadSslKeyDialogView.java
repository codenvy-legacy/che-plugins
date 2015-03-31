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

import org.eclipse.che.ide.api.mvp.View;

public interface UploadSslKeyDialogView extends View<UploadSslKeyDialogView.ActionDelegate> {
    public interface ActionDelegate {
        void onCancelClicked();

        void onUploadClicked();

        void onSubmitComplete(@NotNull String result);

        void onFileNameChanged();
    }

    @NotNull
    String getAlias();

    void setHost(@NotNull String host);

    @NotNull
    String getCertFileName();

    @NotNull
    String getKeyFileName();

    void setEnabledUploadButton(boolean enabled);

    void setMessage(@NotNull String message);

    void setEncoding(@NotNull String encodingType);

    void setAction(@NotNull String url);

    void submit();

    void showDialog();

    void close();
}
