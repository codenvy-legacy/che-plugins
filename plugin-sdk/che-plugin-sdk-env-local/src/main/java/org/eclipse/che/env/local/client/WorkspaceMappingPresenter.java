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

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.workspace.gwt.client.WorkspaceServiceClient;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.preferences.AbstractPreferencePagePresenter;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;

/**
 * The presenter for managing user's runners settings,.
 *
 * @author Vitalii Parfonov
 */
@Singleton
public class WorkspaceMappingPresenter extends AbstractPreferencePagePresenter implements WorkspaceMappingView.ActionDelegate {
    private       AppContext             appContext;
    private final DtoFactory             dtoFactory;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private       NotificationManager    notificationManager;
    private final WorkspaceMappingView   view;


    /** Create presenter. */
    @Inject
    public WorkspaceMappingPresenter(WorkspaceMappingView view,
                                     AppContext appContext,
                                     NotificationManager notificationManager,
                                     WorkspaceServiceClient workspaceService,
                                     DtoFactory dtoFactory,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super("Workspases", "Workspaces", null);
        this.view = view;
        this.appContext = appContext;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.view.setDelegate(this);
        this.notificationManager = notificationManager;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void storeChanges() {
    }

    @Override
    public void revertChanges() {

    }

    @Override
    public void onDeleteClicked() {

    }

    @Override
    public void onAddClicked() {

    }
}
