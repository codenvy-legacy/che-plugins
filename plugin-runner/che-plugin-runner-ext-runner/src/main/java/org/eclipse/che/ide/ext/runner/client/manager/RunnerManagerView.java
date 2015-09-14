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
package org.eclipse.che.ide.ext.runner.client.manager;

import com.google.inject.ImplementedBy;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.tabs.container.TabContainer;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * This is abstract representation of widget that provides an ability to show runners and manage them.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@Singleton
@ImplementedBy(RunnerManagerViewImpl.class)
public interface RunnerManagerView extends View<RunnerManagerView.ActionDelegate> {

    /**
     * Updates runner view representation when runner state changed.
     *
     * @param runner
     *         runner which was changed
     */
    void update(@NotNull Runner runner);

    /**
     * Shows application url on the view.
     *
     * @param applicationUrl
     *         url which needs set
     */
    void setApplicationURl(@Nullable String applicationUrl);

    /**
     * Shows debug port on the view.
     *
     * @param debugPort
     *         debug port which needs set
     */
    void setDebugPort(@Nullable String debugPort);

    /**
     * Shows timeout on the view.
     *
     * @param timeout
     *         timeout that needs to be shown
     */
    void setTimeout(@NotNull String timeout);

    /**
     * Shows special popup panel which displays additional information about runner.
     *
     * @param runner
     *         runner for which need display additional info
     */
    void showMoreInfoPopup(@Nullable Runner runner);

    /**
     * Updates special popup window which contains info about current runner.
     *
     * @param runner
     *         runner for which need update info
     */
    void updateMoreInfoPopup(@NotNull Runner runner);

    /**
     * Sets left panel view representation to container. This panel contains history and templates containers.
     *
     * @param containerPresenter
     *         container to which need set panel
     */
    void setLeftPanel(@NotNull TabContainer containerPresenter);

    /**
     * Sets right properties panel view representation to container. This panel contains terminal container.
     *
     * @param containerPresenter
     *         container to which need set panel
     */
    void setRightPropertiesPanel(@NotNull TabContainer containerPresenter);

    /**
     * Sets left panel view representation to container. This panel contains console and properties containers.
     *
     * @param containerPresenter
     *         container to which need set panel
     */
    void setLeftPropertiesPanel(@NotNull TabContainer containerPresenter);

    /**
     * Sets all tabs in one panel.
     *
     * @param containerPresenter
     *         container which contains all tabs
     */
    void setGeneralPropertiesPanel(@NotNull TabContainer containerPresenter);

    /** Hides all buttons on buttons panel except run button. */
    void hideOtherButtons();

    /** Shows all buttons on buttons panel. */
    void showOtherButtons();

    /**
     * Changes state of run button.
     *
     * @param isEnable
     *         <code>true</code> button is enable, <code>false</code> button is disable
     */
    void setEnableRunButton(boolean isEnable);

    /**
     * Changes state of re-run button.
     *
     * @param isEnable
     *         <code>true</code> button is enable, <code>false</code> button is disable
     */
    void setEnableReRunButton(boolean isEnable);

    /**
     * Changes state of stop button.
     *
     * @param isEnable
     *         <code>true</code> button is enable, <code>false</code> button is disable
     */
    void setEnableStopButton(boolean isEnable);

    /**
     * Changes state of logs button.
     *
     * @param isEnable
     *         <code>true</code> button is enable, <code>false</code> button is disable
     */
    void setEnableLogsButton(boolean isEnable);

    /**
     * Show logs for a runner in new tab.
     *
     * @param url
     *         url where logs are located
     */
    void showLog(@NotNull String url);

    interface ActionDelegate extends BaseActionDelegate {

        /** Performs some actions in response to user's clicking on the 'Run' button. */
        void onRunButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Re-Run' button. */
        void onRerunButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Stop' button. */
        void onStopButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Logs' button. */
        void onLogsButtonClicked();

        /** Performs some actions in response to user's over mouse on timeout label. */
        void onMoreInfoBtnMouseOver();

        /**
         * Performs some actions when user click on toggle splitter button.
         *
         * @param isShowSplitter
         *         <code>true</code> show splitter and shows properties tabs on two different panels, <code>false</code> hide
         *         splitter and shows properties tabs on one panel
         */
        void onToggleSplitterClicked(boolean isShowSplitter);
    }

}