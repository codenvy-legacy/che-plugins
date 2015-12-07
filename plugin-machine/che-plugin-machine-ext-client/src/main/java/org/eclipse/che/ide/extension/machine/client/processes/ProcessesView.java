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
package org.eclipse.che.ide.extension.machine.client.processes;

import com.google.gwt.user.client.ui.IsWidget;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel.MachineTreeNode;

import javax.validation.constraints.NotNull;

/**
 * View of {@link ProcessesPresenter}.
 *
 * @author Anna Shumilova
 */
public interface ProcessesView extends View<ProcessesView.ActionDelegate> {

    /**
     * Set view's title.
     *
     * @param title
     *         new title
     */
    void setTitle(String title);

    void setVisible(boolean visible);

    /** Add console widget*/
    void addConsole(IsWidget widget);


    /**
     * Set process data to be displayed.
     *
     * @param root data which will be displayed
     */
    void setProcessesData(@NotNull ProcessTreeNode root);

    void selectNode(ProcessTreeNode node);

    void showOutputWidget(IsWidget widget);

    interface ActionDelegate extends BaseActionDelegate {

        void onAddTerminal(@NotNull String machineId);

        void onCommandSelected(@NotNull CommandConfiguration command);
    }

}
