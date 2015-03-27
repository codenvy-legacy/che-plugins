/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.tour.client.action.impl;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.plugin.tour.client.action.ExternalAction;
import org.eclipse.che.plugin.tour.client.log.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import static org.eclipse.che.ide.api.notification.Notification.Type.WARNING;

/**
 * Action for opening a file.
 * @author Florent Benoit
 */
public class OpenFileExternalAction implements ExternalAction {

    /**
     * Logger.
     */
    @Inject
    private Log log;

    /**
     * Notification Manager used to send events in order to notify the user.
     */
    @Inject
    private NotificationManager notificationManager;

    /**
     * Editor agent used to open files.
     */
    @Inject
    private EditorAgent editorAgent;

    /**
     * Application context.
     */
    @Inject
    private AppContext appContext;

    /**
     * Accept the category that is "openfile"
     * @param category that should match "openfile" for this action
     * @return true if "openfile" was given as category
     */
    @Override
    public boolean accept(String category) {
        return "openfile".equals(category);
    }

    /**
     * Open a file for the current given path.
     * @param filePath the file path
     */
    @Override
    public void execute(String filePath) {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (filePath != null && !filePath.startsWith("/")) {
            filePath = "/".concat(filePath);
        }
        if (currentProject != null) {
            String fullPath = currentProject.getRootProject().getPath() + filePath;
            log.debug("Open file {0}", fullPath);
            currentProject.getCurrentTree().getNodeByPath(fullPath, new TreeNodeAsyncCallback());
        }
    }


    /**
     * Open a file in the tree editor
     */
    private class TreeNodeAsyncCallback implements AsyncCallback<TreeNode<?>> {
        @Override
        public void onSuccess(TreeNode<?> result) {
            if (result instanceof FileNode) {
                editorAgent.openEditor((FileNode)result);
            }
        }

        @Override
        public void onFailure(Throwable caught) {
            notificationManager.showNotification(new Notification("Unable to open file", WARNING));
        }
    }

}
