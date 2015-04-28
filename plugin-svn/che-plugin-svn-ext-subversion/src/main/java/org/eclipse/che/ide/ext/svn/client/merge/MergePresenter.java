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
package org.eclipse.che.ide.ext.svn.client.merge;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.ProjectExplorerPart;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.generic.StorableNode;
import org.eclipse.che.ide.ext.svn.client.SubversionClientService;
import org.eclipse.che.ide.ext.svn.client.common.RawOutputPresenter;
import org.eclipse.che.ide.ext.svn.client.common.SubversionActionPresenter;
import org.eclipse.che.ide.ext.svn.shared.InfoResponse;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;

import javax.annotation.Nullable;

/**
 * Manages merging the branches and folders.
 */
@Singleton
public class MergePresenter extends SubversionActionPresenter implements MergeView.ActionDelegate {

    private final MergeView view;
//    private final SelectionAgent selectionAgent;
    private final SubversionClientService subversionClientService;
    private final AppContext appContext;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final NotificationManager notificationManager;

    private TreeNode<?> targetNode;

    /**
     * Creates an instance of this presenter.
     */
    @Inject
    public MergePresenter(final MergeView view,
                          final SubversionClientService subversionClientService,
                          final AppContext appContext,
                          DtoUnmarshallerFactory dtoUnmarshallerFactory,
                          final EventBus eventBus,
                          final RawOutputPresenter console,
                          final WorkspaceAgent workspaceAgent,
                          final ProjectExplorerPart projectExplorerPart,
                          final NotificationManager notificationManager) {
        super(appContext, eventBus, console, workspaceAgent, projectExplorerPart);

        this.view = view;
        this.subversionClientService = subversionClientService;
        this.appContext = appContext;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;

        view.setDelegate(this);
    }

    private native void log(String msg) /*-{
        console.log(msg);
    }-*/;

    @Nullable
    private String getStorableNodePath(TreeNode<?> node) {
        return node instanceof StorableNode ? ((StorableNode)node).getPath() : null;
    }

    /**
     * Prepares to merging and opens Merge dialog.
     */
    public void merge() {
        targetNode = getSelectedNode();
        if (targetNode == null) {
            return;
        }

        subversionClientService.info(appContext.getCurrentProject().getRootProject().getPath(), getSelectedPaths(), "HEAD",
                new AsyncRequestCallback<InfoResponse>(dtoUnmarshallerFactory.newUnmarshaller(InfoResponse.class)) {
                    @Override
                    protected void onSuccess(InfoResponse result) {
                        Window.alert("INFO RECEIVED!");

                        printCommand(result.getCommand());
                        printAndSpace(result.getOutput());

                        log("Path: " + result.getPath());
                        log("URL: " + result.getPath());
                        log("Relative URL: " + result.getPath());
                        log("Repository Root: " + result.getPath());
                        log("Repository UUID: " + result.getPath());
                        log("Revision: " + result.getPath());
                        log("Node Kind: " + result.getPath());
                        log("Last Changed Rev: " + result.getPath());
                        log("Last Changed Date: " + result.getPath());

                        view.targetTextBox().setValue(getRelativeNodeURL(targetNode));
                        view.targetCheckBox().setValue(false);
                        view.setTargetIsURL(false);

                        view.show();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showError(exception.getMessage());
                    }
                });
    }

    @Nullable
    private String getRelativeNodeURL(TreeNode<?> node) {
        if (node instanceof StorableNode) {
            StorableNode storableNode = (StorableNode)node;
            String relativeURL = storableNode.getPath();
            return relativeURL;
        }

        return null;
    }

    /**
     * Performs actions when clicking Merge button.
     */
    @Override
    public void mergeClicked() {
    }

    /**
     * Closes the dialog when clicking Cancel button.
     */
    @Override
    public void cancelClicked() {
        view.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void onSourceCheckBoxChanged() {
        if (view.targetCheckBox().getValue()) {
            view.targetTextBox().setValue("");
            view.enableTargetTextBox(true);
        } else {
            view.targetTextBox().setValue(getRelativeNodeURL(targetNode));
        }
    }

}
