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
package org.eclipse.che.ide.ext.datasource.client.newdatasource.view;

import org.eclipse.che.ide.api.mvp.Presenter;
import org.eclipse.che.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

@ImplementedBy(NewDatasourceWizardHeadViewImpl.class)
public interface NewDatasourceWizardHeadView extends View<NewDatasourceWizardHeadView.ActionDelegate> {

    void showPage(Presenter presenter, String place);

    void cleanPage(String place);

    void showDialog();

    void setEnabledAnimation(boolean enabled);

    void close();

    void setFinishButtonEnabled(boolean enabled);

    void reset();

    void enableInput();

    void disableInput();

    void setName(String name);

    void removeNameError();

    void showNameError();

    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Finish button */
        void onSaveClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button */
        void onCancelClicked();

        void datasourceNameChanged(String name);
    }
}
