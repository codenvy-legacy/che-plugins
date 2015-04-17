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
package org.eclipse.che.env.local.client;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Vitalii Parfonov
 */
@ImplementedBy(WorkspaceMappingViewImpl.class)
public interface WorkspaceMappingView extends View<WorkspaceMappingView.ActionDelegate> {

    public interface ActionDelegate {

        void onDeleteClicked();

        void onAddClicked();

    }

    void setWorkspaces(@Nonnull Map<String, String> ws);
}
