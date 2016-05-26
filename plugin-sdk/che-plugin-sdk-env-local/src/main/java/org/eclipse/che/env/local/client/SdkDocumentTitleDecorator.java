/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.env.local.client;

import com.google.inject.Inject;

import org.eclipse.che.ide.api.DocumentTitleDecorator;

/**
 * @author Vitaly Parfonov
 */
public class SdkDocumentTitleDecorator implements DocumentTitleDecorator {

    private LocalizationConstant localizationConstant;

    @Inject
    public SdkDocumentTitleDecorator(LocalizationConstant localizationConstant) {

        this.localizationConstant = localizationConstant;
    }

    @Override
    public String getDocumentTitle() {
        return localizationConstant.cheTabTitle();
    }

    @Override
    public String getDocumentTitle(String project) {
        return localizationConstant.cheTabTitle(project);
    }

}
